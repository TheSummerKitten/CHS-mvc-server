package com.kitten.chs.web.service;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.FindUserFoodListReqVO;
import com.kitten.chs.web.model.rsp.FindUserFoodListRespVO;

import java.util.List;

public interface UserFoodService {

    Response<List<FindUserFoodListRespVO>> findUserFoodList(FindUserFoodListReqVO reqVO);

}
