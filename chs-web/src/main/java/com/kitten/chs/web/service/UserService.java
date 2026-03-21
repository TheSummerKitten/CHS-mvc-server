package com.kitten.chs.web.service;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.RegisterReqVO;

/**
 * @author kitten
 */
public interface UserService {

    Response<?> register(RegisterReqVO reqVO);

}
