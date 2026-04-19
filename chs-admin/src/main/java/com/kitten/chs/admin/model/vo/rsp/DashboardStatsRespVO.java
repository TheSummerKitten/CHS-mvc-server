package com.kitten.chs.admin.model.vo.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsRespVO {

    private Long totalUsers;

    private Long todayNewUsers;

    private Long totalHealthRecords;

    private Long todayHealthRecords;

    private Long totalOrders;

    private BigDecimal totalOrderAmount;

    private List<DailyOrderStats> weeklyOrderStats;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DailyOrderStats {
        private String date;
        private Long orderCount;
        private BigDecimal orderAmount;
    }
}
