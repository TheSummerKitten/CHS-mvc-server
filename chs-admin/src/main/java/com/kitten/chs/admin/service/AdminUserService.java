package com.kitten.chs.admin.service;

import com.kitten.chs.admin.model.vo.req.DeleteUserReqVO;
import com.kitten.chs.admin.model.vo.req.FindUserPageConListReqVO;
import com.kitten.chs.admin.model.vo.req.UpdatePasswordReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateUserReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindUserInfoRspVO;
import com.kitten.chs.admin.model.vo.rsp.FindUserPageConListRespVO;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;

/**
 * @author kitten
 */
public interface AdminUserService {

    Response<?> updatePassword(UpdatePasswordReqVO reqVO);

    Response<FindUserInfoRspVO> findCurrentUserInfo();

    // 分页条件查询
    PageResponse<FindUserPageConListRespVO> findUserPageConditionList(FindUserPageConListReqVO reqVO);

    // 删除用户
    Response<?> deleteUser(DeleteUserReqVO reqVO);

    Response<?> updateUserInfo(UpdateUserReqVO reqVO);

    Response<?> clearUserAvatar(DeleteUserReqVO reqVO);
}
