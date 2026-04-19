package com.kitten.chs.admin.controller;

import com.kitten.chs.admin.model.vo.req.CreateMedicationReminderReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateMedicationReminderReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindMedicationReminderListRespVO;
import com.kitten.chs.admin.service.MedicationReminderService;
import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/medication-reminder")
@Api(tags = "Admin 用药提醒模块")
public class AdminMedicationReminderController {

    @Autowired
    private MedicationReminderService medicationReminderService;

    @PostMapping("/create")
    @ApiOperationLog(description = "创建用药提醒")
    @ApiOperation(value = "创建用药提醒")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<?> createMedicationReminder(@RequestBody CreateMedicationReminderReqVO reqVO) {
        return medicationReminderService.createMedicationReminder(reqVO);
    }

    @PostMapping("/update")
    @ApiOperationLog(description = "更新用药提醒")
    @ApiOperation(value = "更新用药提醒")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<?> updateMedicationReminder(@RequestBody UpdateMedicationReminderReqVO reqVO) {
        return medicationReminderService.updateMedicationReminder(reqVO);
    }

    @PostMapping("/delete/{id}")
    @ApiOperationLog(description = "删除用药提醒")
    @ApiOperation(value = "删除用药提醒")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<?> deleteMedicationReminder(@PathVariable Long id) {
        return medicationReminderService.deleteMedicationReminder(id);
    }

    @GetMapping("/list/{healthRecordId}")
    @ApiOperationLog(description = "查询某健康记录的用药提醒列表")
    @ApiOperation(value = "查询某健康记录的用药提醒列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<List<FindMedicationReminderListRespVO>> findMedicationReminderList(
            @PathVariable Long healthRecordId) {
        return medicationReminderService.findMedicationReminderList(healthRecordId);
    }

}
