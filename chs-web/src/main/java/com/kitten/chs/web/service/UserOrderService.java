package com.kitten.chs.web.service;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.CreateOrderReqVO;
import com.kitten.chs.web.model.rsp.FindUserOrderListRespVO;
import com.kitten.chs.web.model.rsp.MonthlyConsumptionRspVO;

import java.util.List;

public interface UserOrderService {

    Response<?> createOrder(CreateOrderReqVO reqVO);

    Response<List<FindUserOrderListRespVO>> findUserOrderList();

    Response<MonthlyConsumptionRspVO> getMonthlyConsumption();

}
