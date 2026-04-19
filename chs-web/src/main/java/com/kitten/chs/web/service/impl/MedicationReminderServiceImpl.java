package com.kitten.chs.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kitten.chs.common.domain.dataObject.HealthRecordDO;
import com.kitten.chs.common.domain.dataObject.MedicationReminderDO;
import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.mapper.HealthRecordMapper;
import com.kitten.chs.common.domain.mapper.MedicationReminderMapper;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.CreateMedicationReminderReqVO;
import com.kitten.chs.web.model.req.UpdateMedicationReminderReqVO;
import com.kitten.chs.web.model.rsp.FindMedicationReminderListRespVO;
import com.kitten.chs.web.service.MedicationReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MedicationReminderServiceImpl implements MedicationReminderService {

    @Autowired
    private MedicationReminderMapper medicationReminderMapper;

    @Autowired
    private HealthRecordMapper healthRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Response<?> createMedicationReminder(CreateMedicationReminderReqVO reqVO) {
        HealthRecordDO healthRecordDO = healthRecordMapper.selectById(reqVO.getHealthRecordId());
        if (healthRecordDO == null || healthRecordDO.getIsDeleted()) {
            return Response.fail("健康记录不存在");
        }

        MedicationReminderDO reminderDO = MedicationReminderDO.builder()
                .healthRecordId(reqVO.getHealthRecordId())
                .userId(reqVO.getUserId())
                .medicineName(reqVO.getMedicineName())
                .dosage(reqVO.getDosage())
                .frequency(reqVO.getFrequency())
                .course(reqVO.getCourse())
                .remark(reqVO.getRemark())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        medicationReminderMapper.insert(reminderDO);

        return Response.success();
    }

    @Override
    public Response<?> updateMedicationReminder(UpdateMedicationReminderReqVO reqVO) {
        MedicationReminderDO reminderDO = medicationReminderMapper.selectById(reqVO.getId());
        if (reminderDO == null || reminderDO.getIsDeleted()) {
            return Response.fail("用药提醒不存在");
        }

        reminderDO.setMedicineName(reqVO.getMedicineName());
        reminderDO.setDosage(reqVO.getDosage());
        reminderDO.setFrequency(reqVO.getFrequency());
        reminderDO.setCourse(reqVO.getCourse());
        reminderDO.setRemark(reqVO.getRemark());
        reminderDO.setUpdateTime(LocalDateTime.now());

        medicationReminderMapper.updateById(reminderDO);

        return Response.success();
    }

    @Override
    public Response<?> deleteMedicationReminder(Long id) {
        MedicationReminderDO reminderDO = medicationReminderMapper.selectById(id);
        if (reminderDO == null || reminderDO.getIsDeleted()) {
            return Response.fail("用药提醒不存在");
        }

        reminderDO.setIsDeleted(true);
        reminderDO.setUpdateTime(LocalDateTime.now());
        medicationReminderMapper.updateById(reminderDO);

        return Response.success();
    }

    @Override
    public Response<List<FindMedicationReminderListRespVO>> findMedicationReminderList(Long healthRecordId) {
        LambdaQueryWrapper<MedicationReminderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicationReminderDO::getIsDeleted, false);
        wrapper.eq(MedicationReminderDO::getHealthRecordId, healthRecordId);
        wrapper.orderByDesc(MedicationReminderDO::getCreateTime);

        List<MedicationReminderDO> reminderList = medicationReminderMapper.selectList(wrapper);

        List<FindMedicationReminderListRespVO> respVOList = reminderList.stream()
                .map(reminder -> FindMedicationReminderListRespVO.builder()
                        .id(reminder.getId())
                        .healthRecordId(reminder.getHealthRecordId())
                        .medicineName(reminder.getMedicineName())
                        .dosage(reminder.getDosage())
                        .frequency(reminder.getFrequency())
                        .course(reminder.getCourse())
                        .remark(reminder.getRemark())
                        .createTime(reminder.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return Response.success(respVOList);
    }

    @Override
    public Response<List<FindMedicationReminderListRespVO>> findMyMedicationReminderList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDO currentUser = userMapper.selectByUsername(username);

        if (currentUser == null) {
            return Response.fail("用户不存在");
        }

        LambdaQueryWrapper<MedicationReminderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicationReminderDO::getIsDeleted, false);
        wrapper.eq(MedicationReminderDO::getUserId, currentUser.getId());
        wrapper.orderByDesc(MedicationReminderDO::getCreateTime);

        List<MedicationReminderDO> reminderList = medicationReminderMapper.selectList(wrapper);

        List<FindMedicationReminderListRespVO> respVOList = reminderList.stream()
                .map(reminder -> {
                    HealthRecordDO healthRecord = healthRecordMapper.selectById(reminder.getHealthRecordId());
                    return FindMedicationReminderListRespVO.builder()
                            .id(reminder.getId())
                            .healthRecordId(reminder.getHealthRecordId())
                            .medicineName(reminder.getMedicineName())
                            .dosage(reminder.getDosage())
                            .frequency(reminder.getFrequency())
                            .course(reminder.getCourse())
                            .remark(reminder.getRemark())
                            .createTime(reminder.getCreateTime())
                            .recordDate(healthRecord != null ? healthRecord.getRecordDate() : null)
                            .diagnosis(healthRecord != null ? healthRecord.getDiagnosis() : null)
                            .build();
                })
                .collect(Collectors.toList());

        return Response.success(respVOList);
    }

}
