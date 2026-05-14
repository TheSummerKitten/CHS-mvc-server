    package com.kitten.chs.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitten.chs.admin.model.vo.req.DashboardStatsReqVO;
import com.kitten.chs.admin.service.ProtobufDashboardService;
import com.kitten.chs.common.proto.DailyOrderStats;
import com.kitten.chs.common.proto.DashboardStatsResponse;
import com.kitten.chs.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@Api(tags = "Admin 仪表盘模块-Protobuf")
@RequiredArgsConstructor
public class DashboardProtoController {

    private final ProtobufDashboardService protobufDashboardService;
    private final ObjectMapper objectMapper;

    @GetMapping("/proto-stats")
    @ApiOperation(value = "获取仪表盘统计数据(Protobuf格式)")
    public ResponseEntity<?> getDashboardProtoStats(@ModelAttribute DashboardStatsReqVO reqVO,
                                                     HttpServletRequest request) {
        // 根据Accept 请求头自动切换格式
        DashboardStatsResponse protoStats = protobufDashboardService.getDashboardProtoStats(reqVO);

        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        // protobuf 二进制格式
        if (acceptHeader != null && acceptHeader.contains("application/x-protobuf")) {
            byte[] protoBytes = protoStats.toByteArray();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/x-protobuf"))
                    .body(protoBytes);
        }

        try {
            Map<String, Object> jsonMap = convertToJsonMap(protoStats);
            String json = objectMapper.writeValueAsString(jsonMap);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.fail("Protobuf JSON序列化失败: " + e.getMessage()));
        }
    }

    private Map<String, Object> convertToJsonMap(DashboardStatsResponse stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("totalUsers", stats.getTotalUsers());
        map.put("todayNewUsers", stats.getTodayNewUsers());
        map.put("totalHealthRecords", stats.getTotalHealthRecords());
        map.put("todayHealthRecords", stats.getTodayHealthRecords());
        map.put("totalOrders", stats.getTotalOrders());
        map.put("totalOrderAmount", stats.getTotalOrderAmount());

        List<Map<String, Object>> weeklyList = new ArrayList<>();
        for (DailyOrderStats item : stats.getWeeklyOrderStatsList()) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("date", item.getDate());
            itemMap.put("orderCount", item.getOrderCount());
            itemMap.put("orderAmount", item.getOrderAmount());
            weeklyList.add(itemMap);
        }
        map.put("weeklyOrderStats", weeklyList);

        return map;
    }
}