package com.kitten.chs.admin.controller;

import com.kitten.chs.admin.model.vo.req.CreateHealthRecordReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateHealthRecordReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindHealthRecordListRespVO;
import com.kitten.chs.admin.service.HealthRecordService;
import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/admin/health-record")
@Api(tags = "Admin 健康记录模块")
public class AdminHealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping("/create")
    @ApiOperationLog(description = "创建健康记录")
    @ApiOperation(value = "创建健康记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<?> createHealthRecord(@RequestBody CreateHealthRecordReqVO reqVO) {
        return healthRecordService.createHealthRecord(reqVO);
    }

    @PostMapping("/update")
    @ApiOperationLog(description = "更新健康记录")
    @ApiOperation(value = "更新健康记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<?> updateHealthRecord(@RequestBody UpdateHealthRecordReqVO reqVO) {
        return healthRecordService.updateHealthRecord(reqVO);
    }

    @PostMapping("/delete/{id}")
    @ApiOperationLog(description = "删除健康记录")
    @ApiOperation(value = "删除健康记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<?> deleteHealthRecord(@PathVariable Long id) {
        return healthRecordService.deleteHealthRecord(id);
    }

    @GetMapping("/list")
    @ApiOperationLog(description = "查询健康记录列表")
    @ApiOperation(value = "查询健康记录列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<List<FindHealthRecordListRespVO>> findHealthRecordList(
            @RequestParam(required = false) Long userId) {
        return healthRecordService.findHealthRecordList(userId);
    }

    @GetMapping("/detail/{id}")
    @ApiOperationLog(description = "查询健康记录详情")
    @ApiOperation(value = "查询健康记录详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Response<FindHealthRecordListRespVO> findHealthRecordDetail(@PathVariable Long id) {
        return healthRecordService.findHealthRecordDetail(id);
    }

    @GetMapping("/export/{id}")
    @ApiOperation(value = "导出健康记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public void exportHealthRecord(@PathVariable Long id, HttpServletResponse response) {
        try {
            healthRecordService.exportHealthRecord(id, response);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

}
