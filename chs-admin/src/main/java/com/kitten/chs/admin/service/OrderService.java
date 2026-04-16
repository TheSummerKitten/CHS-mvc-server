package com.kitten.chs.admin.service;

import com.kitten.chs.admin.model.vo.req.FindOrderPageListReqVO;
import com.kitten.chs.admin.model.vo.req.OrderActionReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindOrderPageListRespVO;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;

public interface OrderService {

    PageResponse<FindOrderPageListRespVO> findOrderPageList(FindOrderPageListReqVO reqVO);

    Response<?> acceptOrder(OrderActionReqVO reqVO);

    Response<?> rejectOrder(OrderActionReqVO reqVO);

    Response<?> completeOrder(OrderActionReqVO reqVO);

    Response<?> cancelOrder(OrderActionReqVO reqVO);

}
