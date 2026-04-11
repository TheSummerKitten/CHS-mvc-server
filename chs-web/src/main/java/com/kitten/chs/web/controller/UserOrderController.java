package com.kitten.chs.web.controller;

import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.CreateOrderReqVO;
import com.kitten.chs.web.model.rsp.FindUserOrderListRespVO;
import com.kitten.chs.web.service.UserOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Api(tags = "Web 用户订单模块")
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    @PostMapping("/create")
    @ApiOperationLog(description = "用户下单")
    @ApiOperation(value = "用户下单")
    public Response<?> createOrder(@RequestBody CreateOrderReqVO reqVO) {
        return userOrderService.createOrder(reqVO);
    }

    @GetMapping("/list")
    @ApiOperationLog(description = "用户查询订单列表")
    @ApiOperation(value = "用户查询订单列表")
    public Response<List<FindUserOrderListRespVO>> findUserOrderList() {
        return userOrderService.findUserOrderList();
    }

}
