package com.kitten.chs.admin.service;

import com.kitten.chs.admin.model.vo.req.AddFoodReqVO;
import com.kitten.chs.admin.model.vo.req.DeleteFoodReqVO;
import com.kitten.chs.admin.model.vo.req.FindFoodPageListReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateFoodReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateFoodStatusReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindFoodPageListRespVO;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;

public interface FoodService {

    PageResponse<FindFoodPageListRespVO> findFoodPageList(FindFoodPageListReqVO reqVO);

    Response<?> addFood(AddFoodReqVO reqVO);

    Response<?> updateFood(UpdateFoodReqVO reqVO);

    Response<?> deleteFood(DeleteFoodReqVO reqVO);

    Response<?> updateFoodStatus(UpdateFoodStatusReqVO reqVO);

}
