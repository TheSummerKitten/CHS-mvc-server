package com.kitten.chs.admin.controller;

import com.kitten.chs.admin.model.vo.req.FindOrderPageListReqVO;
import com.kitten.chs.admin.model.vo.req.OrderActionReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindOrderPageListRespVO;
import com.kitten.chs.admin.service.OrderService;
import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/order")
@Api(tags = "Admin 订单模块")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "订单分页查询")
    @ApiOperation(value = "订单分页查询")
    public PageResponse<FindOrderPageListRespVO> findOrderPageList(@RequestBody FindOrderPageListReqVO reqVO) {
        return orderService.findOrderPageList(reqVO);
    }

    @PutMapping("/accept")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "接单")
    @ApiOperation(value = "接单")
    public Response<?> acceptOrder(@RequestBody OrderActionReqVO reqVO) {
        return orderService.acceptOrder(reqVO);
    }

    @PutMapping("/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "拒单")
    @ApiOperation(value = "拒单")
    public Response<?> rejectOrder(@RequestBody OrderActionReqVO reqVO) {
        return orderService.rejectOrder(reqVO);
    }

    @PutMapping("/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "完成订单")
    @ApiOperation(value = "完成订单")
    public Response<?> completeOrder(@RequestBody OrderActionReqVO reqVO) {
        return orderService.completeOrder(reqVO);
    }

    @PutMapping("/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "取消订单")
    @ApiOperation(value = "取消订单")
    public Response<?> cancelOrder(@RequestBody OrderActionReqVO reqVO) {
        return orderService.cancelOrder(reqVO);
    }

}
