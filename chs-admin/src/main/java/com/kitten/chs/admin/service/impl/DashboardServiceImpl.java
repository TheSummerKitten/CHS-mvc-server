package com.kitten.chs.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kitten.chs.admin.model.vo.req.DashboardStatsReqVO;
import com.kitten.chs.admin.model.vo.rsp.DashboardStatsRespVO;
import com.kitten.chs.admin.service.DashboardService;
import com.kitten.chs.common.domain.dataObject.HealthRecordDO;
import com.kitten.chs.common.domain.dataObject.OrderDO;
import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.mapper.HealthRecordMapper;
import com.kitten.chs.common.domain.mapper.OrderMapper;
import com.kitten.chs.common.domain.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final HealthRecordMapper healthRecordMapper;
    private final OrderMapper orderMapper;

    @Override
    public DashboardStatsRespVO getDashboardStats(DashboardStatsReqVO reqVO) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        Long totalUsers = countUsers(null, null);
        Long todayNewUsers = countUsers(todayStart, todayEnd);

        Long totalHealthRecords = countHealthRecords(null, null);
        Long todayHealthRecords = countHealthRecords(todayStart, todayEnd);

        Long totalOrders = countOrders(null, null);
        BigDecimal totalOrderAmount = sumOrderAmount(null, null);

        List<DashboardStatsRespVO.DailyOrderStats> dailyStats = getDailyOrderStats(reqVO);

        return DashboardStatsRespVO.builder()
                .totalUsers(totalUsers)
                .todayNewUsers(todayNewUsers)
                .totalHealthRecords(totalHealthRecords)
                .todayHealthRecords(todayHealthRecords)
                .totalOrders(totalOrders)
                .totalOrderAmount(totalOrderAmount != null ? totalOrderAmount : BigDecimal.ZERO)
                .weeklyOrderStats(dailyStats)
                .build();
    }

    private Long countUsers(LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getIsDeleted, false);
        if (start != null && end != null) {
            wrapper.between(UserDO::getCreateTime, start, end);
        }
        return userMapper.selectCount(wrapper);
    }

    private Long countHealthRecords(LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<HealthRecordDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecordDO::getIsDeleted, false);
        if (start != null && end != null) {
            wrapper.between(HealthRecordDO::getCreateTime, start, end);
        }
        return healthRecordMapper.selectCount(wrapper);
    }

    private Long countOrders(LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<OrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDO::getIsDeleted, false);
        wrapper.in(OrderDO::getStatus, 2, 3, 4);
        if (start != null && end != null) {
            wrapper.between(OrderDO::getCreateTime, start, end);
        }
        return orderMapper.selectCount(wrapper);
    }

    private BigDecimal sumOrderAmount(LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<OrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDO::getIsDeleted, false);
        wrapper.in(OrderDO::getStatus, 2, 3, 4);
        if (start != null && end != null) {
            wrapper.between(OrderDO::getCreateTime, start, end);
        }
        List<OrderDO> orders = orderMapper.selectList(wrapper);
        return orders.stream()
                .map(OrderDO::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<DashboardStatsRespVO.DailyOrderStats> getDailyOrderStats(DashboardStatsReqVO reqVO) {
        List<DashboardStatsRespVO.DailyOrderStats> stats = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        LocalDate startDate;
        LocalDate endDate;

        if (reqVO != null && reqVO.getStartDate() != null && reqVO.getEndDate() != null) {
            startDate = reqVO.getStartDate();
            endDate = reqVO.getEndDate();
        } else {
            endDate = LocalDate.now();
            startDate = endDate.minusDays(6);
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int maxDays = 30;
        if (daysBetween > maxDays) {
            startDate = endDate.minusDays(maxDays - 1);
        }

        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;

        for (LocalDate date = finalStartDate; !date.isAfter(finalEndDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            Long orderCount = countOrders(dayStart, dayEnd);
            BigDecimal orderAmount = sumOrderAmount(dayStart, dayEnd);

            stats.add(DashboardStatsRespVO.DailyOrderStats.builder()
                    .date(date.format(formatter))
                    .orderCount(orderCount)
                    .orderAmount(orderAmount != null ? orderAmount : BigDecimal.ZERO)
                    .build());
        }

        return stats;
    }
}
