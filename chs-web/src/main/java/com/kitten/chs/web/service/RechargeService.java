package com.kitten.chs.web.service;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.CreateRechargeReqVO;
import com.kitten.chs.web.model.rsp.CreateRechargeRespVO;
import com.kitten.chs.web.model.rsp.FindRechargeListRespVO;

import java.util.List;

public interface RechargeService {

    Response<CreateRechargeRespVO> createRecharge(CreateRechargeReqVO reqVO);

    Response<List<FindRechargeListRespVO>> findRechargeList();

    Response<?> handlePayCallback(String orderNo, String tradeNo);

}
