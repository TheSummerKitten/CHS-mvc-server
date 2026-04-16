package com.kitten.chs.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kitten.chs.admin.model.vo.req.FindOrderPageListReqVO;
import com.kitten.chs.admin.model.vo.req.OrderActionReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindOrderPageListRespVO;
import com.kitten.chs.admin.service.OrderService;
import com.kitten.chs.common.domain.dataObject.OrderDO;
import com.kitten.chs.common.domain.dataObject.OrderItemDO;
import com.kitten.chs.common.domain.mapper.OrderItemMapper;
import com.kitten.chs.common.domain.mapper.OrderMapper;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public PageResponse<FindOrderPageListRespVO> findOrderPageList(FindOrderPageListReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();

        Page<OrderDO> page = new Page<>(current, size);

        LambdaQueryWrapper<OrderDO> wrapper = new LambdaQueryWrapper<>();
        String username = reqVO.getUsername();
        Integer status = reqVO.getStatus();
        String orderNo = reqVO.getOrderNo();

        wrapper.like(StringUtils.isNotBlank(username), OrderDO::getUsername, username.trim())
                .eq(Objects.nonNull(status), OrderDO::getStatus, status)
                .like(StringUtils.isNotBlank(orderNo), OrderDO::getOrderNo, orderNo.trim())
                .eq(OrderDO::getIsDeleted, false)
                .orderByDesc(OrderDO::getCreateTime);

        Page<OrderDO> orderDOPage = orderMapper.selectPage(page, wrapper);

        List<OrderDO> orderDOS = orderDOPage.getRecords();

        List<FindOrderPageListRespVO> vos = new ArrayList<>();
        if (CollectionUtils.isEmpty(orderDOS)) {
            return PageResponse.success(orderDOPage, vos);
        }

        List<Long> orderIds = orderDOS.stream()
                .map(OrderDO::getId)
                .collect(Collectors.toList());

        List<OrderItemDO> allOrderItems = orderItemMapper.selectByOrderIds(orderIds);

        Map<Long, List<OrderItemDO>> orderItemMap = allOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItemDO::getOrderId));

        for (OrderDO orderDO : orderDOS) {
            List<OrderItemDO> orderItemDOS = orderItemMap.getOrDefault(orderDO.getId(), new ArrayList<>());

            List<FindOrderPageListRespVO.OrderItemVO> itemVOs = orderItemDOS.stream()
                    .map(orderItemDO -> FindOrderPageListRespVO.OrderItemVO.builder()
                            .id(orderItemDO.getId())
                            .foodName(orderItemDO.getFoodName())
                            .foodPrice(orderItemDO.getFoodPrice())
                            .quantity(orderItemDO.getQuantity())
                            .subtotal(orderItemDO.getSubtotal())
                            .build())
                    .collect(Collectors.toList());

            FindOrderPageListRespVO vo = FindOrderPageListRespVO.builder()
                    .id(orderDO.getId())
                    .orderNo(orderDO.getOrderNo())
                    .username(orderDO.getUsername())
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

        return PageResponse.success(orderDOPage, vos);
    }

    @Override
    public Response<?> acceptOrder(OrderActionReqVO reqVO) {
        if (reqVO.getOrderId() == null) {
            return Response.fail("订单ID不能为空");
        }

        OrderDO orderDO = orderMapper.selectById(reqVO.getOrderId());
        if (orderDO == null || orderDO.getIsDeleted()) {
            return Response.fail("订单不存在");
        }

        if (orderDO.getStatus() != 0) {
            return Response.fail("只有待处理的订单才能接单");
        }

        LambdaUpdateWrapper<OrderDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(OrderDO::getStatus, 1)
                .set(OrderDO::getUpdateTime, LocalDateTime.now())
                .eq(OrderDO::getId, reqVO.getOrderId())
                .eq(OrderDO::getIsDeleted, false);

        int result = orderMapper.update(null, wrapper);
        if (result <= 0) {
            return Response.fail("接单失败");
        }

        return Response.success("接单成功，订单正在处理中");
    }

    @Override
    public Response<?> rejectOrder(OrderActionReqVO reqVO) {
        if (reqVO.getOrderId() == null) {
            return Response.fail("订单ID不能为空");
        }

        if (reqVO.getRejectReason() == null || reqVO.getRejectReason().trim().isEmpty()) {
            return Response.fail("请填写拒单原因");
        }

        OrderDO orderDO = orderMapper.selectById(reqVO.getOrderId());
        if (orderDO == null || orderDO.getIsDeleted()) {
            return Response.fail("订单不存在");
        }

        if (orderDO.getStatus() != 0) {
            return Response.fail("只有待处理的订单才能拒单");
        }

        LambdaUpdateWrapper<OrderDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(OrderDO::getStatus, 3)
                .set(OrderDO::getRejectReason, reqVO.getRejectReason().trim())
                .set(OrderDO::getUpdateTime, LocalDateTime.now())
                .eq(OrderDO::getId, reqVO.getOrderId())
                .eq(OrderDO::getIsDeleted, false);

        int result = orderMapper.update(null, wrapper);
        if (result <= 0) {
            return Response.fail("拒单失败");
        }

        return Response.success("已拒绝该订单");
    }

    @Override
    public Response<?> completeOrder(OrderActionReqVO reqVO) {
        if (reqVO.getOrderId() == null) {
            return Response.fail("订单ID不能为空");
        }

        OrderDO orderDO = orderMapper.selectById(reqVO.getOrderId());
        if (orderDO == null || orderDO.getIsDeleted()) {
            return Response.fail("订单不存在");
        }

        if (orderDO.getStatus() != 1) {
            return Response.fail("只有处理中的订单才能完成");
        }

        LambdaUpdateWrapper<OrderDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(OrderDO::getStatus, 2)
                .set(OrderDO::getUpdateTime, LocalDateTime.now())
                .eq(OrderDO::getId, reqVO.getOrderId())
                .eq(OrderDO::getIsDeleted, false);

        int result = orderMapper.update(null, wrapper);
        if (result <= 0) {
            return Response.fail("完成订单失败");
        }

        return Response.success("订单已完成");
    }

    @Override
    public Response<?> cancelOrder(OrderActionReqVO reqVO) {
        if (reqVO.getOrderId() == null) {
            return Response.fail("订单ID不能为空");
        }

        OrderDO orderDO = orderMapper.selectById(reqVO.getOrderId());
        if (orderDO == null || orderDO.getIsDeleted()) {
            return Response.fail("订单不存在");
        }

        if (orderDO.getStatus() == 2 || orderDO.getStatus() == 3) {
            return Response.fail("已完成或已取消的订单无法操作");
        }

        LambdaUpdateWrapper<OrderDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(OrderDO::getStatus, 3)
                .set(OrderDO::getUpdateTime, LocalDateTime.now())
                .eq(OrderDO::getId, reqVO.getOrderId())
                .eq(OrderDO::getIsDeleted, false);

        int result = orderMapper.update(null, wrapper);
        if (result <= 0) {
            return Response.fail("取消订单失败");
        }

        return Response.success("订单已取消");
    }

}
