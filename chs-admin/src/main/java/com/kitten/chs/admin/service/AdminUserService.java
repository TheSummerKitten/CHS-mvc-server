package com.kitten.chs.admin.service;

import com.kitten.chs.admin.model.vo.req.UpdatePasswordReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindUserInfoRspVO;
import com.kitten.chs.common.utils.Response;

/**
 * @author kitten
 */
public interface AdminUserService {

    Response<?> updatePassword(UpdatePasswordReqVO reqVO);

    Response<FindUserInfoRspVO> findCurrentUserInfo();
}
