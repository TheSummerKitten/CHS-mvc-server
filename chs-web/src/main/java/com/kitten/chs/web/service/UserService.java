package com.kitten.chs.web.service;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.RegisterReqVO;
import com.kitten.chs.web.model.req.UpdateSelfInfoReqVO;
import com.kitten.chs.web.model.rsp.UserInfoRspVO;

/**
 * @author kitten
 */
public interface UserService {

    Response<?> register(RegisterReqVO reqVO);

    Response<UserInfoRspVO> getCurrentUserInfo();

    Response<?> updateSelfInfo(UpdateSelfInfoReqVO reqVO);

}
