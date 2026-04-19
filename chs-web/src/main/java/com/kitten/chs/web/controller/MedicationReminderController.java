package com.kitten.chs.web.controller;

import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.rsp.FindMedicationReminderListRespVO;
import com.kitten.chs.web.service.MedicationReminderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/medication-reminder")
@Api(tags = "Web 用药提醒模块")
public class MedicationReminderController {

    @Autowired
    private MedicationReminderService medicationReminderService;

    @GetMapping("/list/{healthRecordId}")
    @ApiOperationLog(description = "查询某健康记录的用药提醒列表")
    @ApiOperation(value = "查询某健康记录的用药提醒列表")
    public Response<List<FindMedicationReminderListRespVO>> findMedicationReminderList(
            @PathVariable Long healthRecordId) {
        return medicationReminderService.findMedicationReminderList(healthRecordId);
    }

    @GetMapping("/my-list")
    @ApiOperationLog(description = "用户查询自己的用药提醒列表")
    @ApiOperation(value = "用户查询自己的用药提醒列表")
    public Response<List<FindMedicationReminderListRespVO>> findMyMedicationReminderList() {
        return medicationReminderService.findMyMedicationReminderList();
    }

}
