package com.kitten.chs.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kitten.chs.common.domain.dataObject.FoodDO;
import com.kitten.chs.common.domain.dataObject.OrderDO;
import com.kitten.chs.common.domain.dataObject.OrderItemDO;
import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.mapper.FoodMapper;
import com.kitten.chs.common.domain.mapper.OrderItemMapper;
import com.kitten.chs.common.domain.mapper.OrderMapper;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.CreateOrderReqVO;
import com.kitten.chs.web.model.rsp.FindUserOrderListRespVO;
import com.kitten.chs.web.model.rsp.MonthlyConsumptionRspVO;
import com.kitten.chs.web.service.UserOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserOrderServiceImpl implements UserOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> createOrder(CreateOrderReqVO reqVO) {
        if (reqVO.getItems() == null || reqVO.getItems().isEmpty()) {
            return Response.fail("订单项不能为空");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserDO userDO = userMapper.selectByUsername(username);
        if (userDO == null) {
            return Response.fail("用户不存在");
        }

        String orderNo = generateOrderNo();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemDO> orderItems = new ArrayList<>();

        for (CreateOrderReqVO.OrderItem item : reqVO.getItems()) {
            if (item.getFoodId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                return Response.fail("订单项数据无效");
            }

            FoodDO foodDO = foodMapper.selectById(item.getFoodId());
            if (foodDO == null || foodDO.getIsDeleted() || foodDO.getStatus() != 1) {
                return Response.fail("菜品不存在或已下架: " + item.getFoodId());
            }

            BigDecimal subtotal = foodDO.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItemDO orderItemDO = OrderItemDO.builder()
                    .foodId(item.getFoodId())
                    .foodName(foodDO.getName())
                    .foodPrice(foodDO.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .createTime(LocalDateTime.now())
                    .build();

            orderItems.add(orderItemDO);
        }

        if (userDO.getBalance() == null || userDO.getBalance().compareTo(totalAmount) < 0) {
            return Response.fail("余额不足，当前余额：" + (userDO.getBalance() != null ? userDO.getBalance() : BigDecimal.ZERO) + " 元");
        }

        int deductResult = userMapper.deductBalance(username, totalAmount);
        if (deductResult <= 0) {
            return Response.fail("余额扣减失败，请稍后重试");
        }

        OrderDO orderDO = OrderDO.builder()
                .orderNo(orderNo)
                .username(username)
                .totalAmount(totalAmount)
                .receiverName(reqVO.getReceiverName())
                .receiverPhone(reqVO.getReceiverPhone())
                .receiverAddress(reqVO.getReceiverAddress())
                .status(0)
                .remark(reqVO.getRemark())
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .build();

        int orderResult = orderMapper.insert(orderDO);
        if (orderResult <= 0) {
            return Response.fail("创建订单失败");
        }

        for (OrderItemDO orderItem : orderItems) {
            orderItem.setOrderId(orderDO.getId());
            orderItemMapper.insert(orderItem);
        }

        return Response.success("下单成功");
    }

    @Override
    public Response<List<FindUserOrderListRespVO>> findUserOrderList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        LambdaQueryWrapper<OrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDO::getUsername, username)
                .eq(OrderDO::getIsDeleted, false)
                .orderByDesc(OrderDO::getCreateTime);

        List<OrderDO> orderDOS = orderMapper.selectList(wrapper);

        List<FindUserOrderListRespVO> vos = new ArrayList<>();
        if (orderDOS == null || orderDOS.isEmpty()) {
            return Response.success(vos);
        }

        List<Long> orderIds = orderDOS.stream()
                .map(OrderDO::getId)
                .collect(Collectors.toList());

        List<OrderItemDO> allOrderItems = orderItemMapper.selectByOrderIds(orderIds);

        Map<Long, List<OrderItemDO>> orderItemMap = allOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItemDO::getOrderId));

        for (OrderDO orderDO : orderDOS) {
            List<OrderItemDO> orderItemDOS = orderItemMap.getOrDefault(orderDO.getId(), new ArrayList<>());

            List<FindUserOrderListRespVO.OrderItemVO> itemVOs = orderItemDOS.stream()
                    .map(orderItemDO -> FindUserOrderListRespVO.OrderItemVO.builder()
                            .id(orderItemDO.getId())
                            .foodName(orderItemDO.getFoodName())
                            .foodPrice(orderItemDO.getFoodPrice())
                            .quantity(orderItemDO.getQuantity())
                            .subtotal(orderItemDO.getSubtotal())
                            .build())
                    .collect(Collectors.toList());

            FindUserOrderListRespVO vo = FindUserOrderListRespVO.builder()
                    .id(orderDO.getId())
                    .orderNo(orderDO.getOrderNo())
                    .totalAmount(orderDO.getTotalAmount())
                    .receiverName(orderDO.getReceiverName())
                    .receiverPhone(orderDO.getReceiverPhone())
                    .receiverAddress(orderDO.getReceiverAddress())
                    .status(orderDO.getStatus())
                    .remark(orderDO.getRemark())
                    .rejectReason(orderDO.getRejectReason())
                    .createTime(orderDO.getCreateTime())
                    .items(itemVOs)
                    .build();
            vos.add(vo);
        }

        return Response.success(vos);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ORD" + timestamp + uuid;
    }

    @Override
    public Response<MonthlyConsumptionRspVO> getMonthlyConsumption() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        BigDecimal totalAmount = orderMapper.sumMonthlyConsumption(username, year, month);
        Integer orderCount = orderMapper.countMonthlyOrders(username, year, month);

        MonthlyConsumptionRspVO vo = MonthlyConsumptionRspVO.builder()
                .totalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO)
                .orderCount(orderCount != null ? orderCount : 0)
                .build();

        return Response.success(vo);
    }

}
