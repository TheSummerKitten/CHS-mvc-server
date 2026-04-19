package com.kitten.chs.admin.controller;

import com.kitten.chs.admin.model.vo.req.DashboardStatsReqVO;
import com.kitten.chs.admin.model.vo.rsp.DashboardStatsRespVO;
import com.kitten.chs.admin.service.DashboardService;
import com.kitten.chs.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@Api(tags = "Admin 仪表盘模块")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @ApiOperation(value = "获取仪表盘统计数据")
    public Response<DashboardStatsRespVO> getDashboardStats(@ModelAttribute DashboardStatsReqVO reqVO) {
        DashboardStatsRespVO stats = dashboardService.getDashboardStats(reqVO);
        return Response.success(stats);
    }
}
