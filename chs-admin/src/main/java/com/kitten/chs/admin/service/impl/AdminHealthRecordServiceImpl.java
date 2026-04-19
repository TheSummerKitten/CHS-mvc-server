package com.kitten.chs.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kitten.chs.admin.model.vo.req.CreateHealthRecordReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateHealthRecordReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindHealthRecordListRespVO;
import com.kitten.chs.admin.service.HealthRecordService;
import com.kitten.chs.common.domain.dataObject.HealthRecordDO;
import com.kitten.chs.common.domain.dataObject.MedicationReminderDO;
import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.mapper.HealthRecordMapper;
import com.kitten.chs.common.domain.mapper.MedicationReminderMapper;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.excel.HealthRecordExcelUtil;
import com.kitten.chs.common.excel.HealthRecordExportData;
import com.kitten.chs.common.excel.MedicationReminderExcelVO;
import com.kitten.chs.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminHealthRecordServiceImpl implements HealthRecordService {

    @Autowired
    private HealthRecordMapper healthRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MedicationReminderMapper medicationReminderMapper;

    private static final Map<Integer, String> FREQUENCY_MAP = new HashMap<>();
    static {
        FREQUENCY_MAP.put(1, "每日一次");
        FREQUENCY_MAP.put(2, "每日两次");
        FREQUENCY_MAP.put(3, "每日三次");
        FREQUENCY_MAP.put(4, "隔日一次");
        FREQUENCY_MAP.put(5, "每周一次");
    }

    @Override
    public Response<?> createHealthRecord(CreateHealthRecordReqVO reqVO) {
        UserDO userDO = userMapper.selectById(reqVO.getUserId());
        if (userDO == null) {
            return Response.fail("用户不存在");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDO creator = userMapper.selectByUsername(username);

        HealthRecordDO healthRecordDO = HealthRecordDO.builder()
                .userId(reqVO.getUserId())
                .recordDate(reqVO.getRecordDate())
                .height(reqVO.getHeight())
                .weight(reqVO.getWeight())
                .bloodPressureSystolic(reqVO.getBloodPressureSystolic())
                .bloodPressureDiastolic(reqVO.getBloodPressureDiastolic())
                .heartRate(reqVO.getHeartRate())
                .symptoms(reqVO.getSymptoms())
                .medicalHistory(reqVO.getMedicalHistory())
                .diagnosis(reqVO.getDiagnosis())
                .remark(reqVO.getRemark())
                .creatorId(creator.getId())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        healthRecordMapper.insert(healthRecordDO);

        return Response.success();
    }

    @Override
    public Response<?> updateHealthRecord(UpdateHealthRecordReqVO reqVO) {
        HealthRecordDO healthRecordDO = healthRecordMapper.selectById(reqVO.getId());
        if (healthRecordDO == null) {
            return Response.fail("健康记录不存在");
        }

        healthRecordDO.setRecordDate(reqVO.getRecordDate());
        healthRecordDO.setHeight(reqVO.getHeight());
        healthRecordDO.setWeight(reqVO.getWeight());
        healthRecordDO.setBloodPressureSystolic(reqVO.getBloodPressureSystolic());
        healthRecordDO.setBloodPressureDiastolic(reqVO.getBloodPressureDiastolic());
        healthRecordDO.setHeartRate(reqVO.getHeartRate());
        healthRecordDO.setSymptoms(reqVO.getSymptoms());
        healthRecordDO.setMedicalHistory(reqVO.getMedicalHistory());
        healthRecordDO.setDiagnosis(reqVO.getDiagnosis());
        healthRecordDO.setRemark(reqVO.getRemark());
        healthRecordDO.setUpdateTime(LocalDateTime.now());

        healthRecordMapper.updateById(healthRecordDO);

        return Response.success();
    }

    @Override
    public Response<?> deleteHealthRecord(Long id) {
        HealthRecordDO healthRecordDO = healthRecordMapper.selectById(id);
        if (healthRecordDO == null) {
            return Response.fail("健康记录不存在");
        }

        healthRecordDO.setIsDeleted(true);
        healthRecordDO.setUpdateTime(LocalDateTime.now());
        healthRecordMapper.updateById(healthRecordDO);

        return Response.success();
    }

    @Override
    public Response<List<FindHealthRecordListRespVO>> findHealthRecordList(Long userId) {
        LambdaQueryWrapper<HealthRecordDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecordDO::getIsDeleted, false);
        if (userId != null) {
            wrapper.eq(HealthRecordDO::getUserId, userId);
        }
        wrapper.orderByDesc(HealthRecordDO::getRecordDate);

        List<HealthRecordDO> recordList = healthRecordMapper.selectList(wrapper);

        List<FindHealthRecordListRespVO> respVOList = recordList.stream().map(record -> {
            UserDO userDO = userMapper.selectById(record.getUserId());
            return FindHealthRecordListRespVO.builder()
                    .id(record.getId())
                    .userId(record.getUserId())
                    .username(userDO != null ? userDO.getUsername() : null)
                    .recordDate(record.getRecordDate())
                    .height(record.getHeight())
                    .weight(record.getWeight())
                    .bloodPressureSystolic(record.getBloodPressureSystolic())
                    .bloodPressureDiastolic(record.getBloodPressureDiastolic())
                    .heartRate(record.getHeartRate())
                    .symptoms(record.getSymptoms())
                    .medicalHistory(record.getMedicalHistory())
                    .diagnosis(record.getDiagnosis())
                    .remark(record.getRemark())
                    .createTime(record.getCreateTime())
                    .build();
        }).collect(Collectors.toList());

        return Response.success(respVOList);
    }

    @Override
    public Response<FindHealthRecordListRespVO> findHealthRecordDetail(Long id) {
        HealthRecordDO record = healthRecordMapper.selectById(id);
        if (record == null || record.getIsDeleted()) {
            return Response.fail("健康记录不存在");
        }

        UserDO userDO = userMapper.selectById(record.getUserId());

        FindHealthRecordListRespVO respVO = FindHealthRecordListRespVO.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .username(userDO != null ? userDO.getUsername() : null)
                .recordDate(record.getRecordDate())
                .height(record.getHeight())
                .weight(record.getWeight())
                .bloodPressureSystolic(record.getBloodPressureSystolic())
                .bloodPressureDiastolic(record.getBloodPressureDiastolic())
                .heartRate(record.getHeartRate())
                .symptoms(record.getSymptoms())
                .medicalHistory(record.getMedicalHistory())
                .diagnosis(record.getDiagnosis())
                .remark(record.getRemark())
                .createTime(record.getCreateTime())
                .build();

        return Response.success(respVO);
    }

    @Override
    public void exportHealthRecord(Long id, HttpServletResponse response) throws Exception {
        HealthRecordDO record = healthRecordMapper.selectById(id);
        if (record == null || record.getIsDeleted()) {
            throw new RuntimeException("健康记录不存在");
        }

        UserDO userDO = userMapper.selectById(record.getUserId());

        LambdaQueryWrapper<MedicationReminderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicationReminderDO::getIsDeleted, false);
        wrapper.eq(MedicationReminderDO::getHealthRecordId, id);
        wrapper.orderByAsc(MedicationReminderDO::getCreateTime);
        List<MedicationReminderDO> medicationList = medicationReminderMapper.selectList(wrapper);

        List<MedicationReminderExcelVO> medicationExcelList = medicationList.stream()
                .map(medication -> MedicationReminderExcelVO.builder()
                        .medicineName(medication.getMedicineName())
                        .dosage(medication.getDosage())
                        .frequency(FREQUENCY_MAP.getOrDefault(medication.getFrequency(), "-"))
                        .course(medication.getCourse())
                        .remark(medication.getRemark())
                        .build())
                .collect(Collectors.toList());

        HealthRecordExportData exportData = HealthRecordExportData.builder()
                .username(userDO != null ? userDO.getUsername() : "-")
                .recordDate(record.getRecordDate())
                .createTime(record.getCreateTime())
                .height(record.getHeight() != null ? record.getHeight().toString() : null)
                .weight(record.getWeight() != null ? record.getWeight().toString() : null)
                .heartRate(record.getHeartRate() != null ? record.getHeartRate().toString() : null)
                .bloodPressureSystolic(record.getBloodPressureSystolic() != null ? record.getBloodPressureSystolic().toString() : null)
                .bloodPressureDiastolic(record.getBloodPressureDiastolic() != null ? record.getBloodPressureDiastolic().toString() : null)
                .symptoms(record.getSymptoms())
                .medicalHistory(record.getMedicalHistory())
                .diagnosis(record.getDiagnosis())
                .remark(record.getRemark())
                .medicationList(medicationExcelList)
                .build();

        HealthRecordExcelUtil.exportHealthRecord(exportData, response);
    }

}
