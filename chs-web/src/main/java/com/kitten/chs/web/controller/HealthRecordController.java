package com.kitten.chs.web.controller;

import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.rsp.FindHealthRecordListRespVO;
import com.kitten.chs.web.service.HealthRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/web/health-record")
@Api(tags = "Web 健康记录模块")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping("/my-list")
    @ApiOperationLog(description = "用户查询自己的健康记录列表")
    @ApiOperation(value = "用户查询自己的健康记录列表")
    public Response<List<FindHealthRecordListRespVO>> findMyHealthRecordList() {
        return healthRecordService.findMyHealthRecordList();
    }

    @GetMapping("/detail/{id}")
    @ApiOperationLog(description = "查询健康记录详情")
    @ApiOperation(value = "查询健康记录详情")
    public Response<FindHealthRecordListRespVO> findHealthRecordDetail(@PathVariable Long id) {
        return healthRecordService.findHealthRecordDetail(id);
    }

    @GetMapping("/export/{id}")
    @ApiOperation(value = "导出健康记录")
    public void exportMyHealthRecord(@PathVariable Long id, HttpServletResponse response) {
        try {
            healthRecordService.exportMyHealthRecord(id, response);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

}
