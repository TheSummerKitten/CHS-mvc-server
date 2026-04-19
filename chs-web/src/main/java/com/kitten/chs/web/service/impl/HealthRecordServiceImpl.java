package com.kitten.chs.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.kitten.chs.web.model.rsp.FindHealthRecordListRespVO;
import com.kitten.chs.web.service.HealthRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HealthRecordServiceImpl implements HealthRecordService {

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
    public Response<List<FindHealthRecordListRespVO>> findMyHealthRecordList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDO currentUser = userMapper.selectByUsername(username);

        if (currentUser == null) {
            return Response.fail("用户不存在");
        }

        LambdaQueryWrapper<HealthRecordDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecordDO::getIsDeleted, false);
        wrapper.eq(HealthRecordDO::getUserId, currentUser.getId());
        wrapper.orderByDesc(HealthRecordDO::getRecordDate);

        List<HealthRecordDO> recordList = healthRecordMapper.selectList(wrapper);

        List<FindHealthRecordListRespVO> respVOList = recordList.stream().map(record -> {
            return FindHealthRecordListRespVO.builder()
                    .id(record.getId())
                    .userId(record.getUserId())
                    .username(currentUser.getUsername())
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
    public void exportMyHealthRecord(Long id, HttpServletResponse response) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDO currentUser = userMapper.selectByUsername(username);

        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        HealthRecordDO record = healthRecordMapper.selectById(id);
        if (record == null || record.getIsDeleted()) {
            throw new RuntimeException("健康记录不存在");
        }

        if (!record.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("无权导出该记录");
        }

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
                .username(currentUser.getUsername())
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
