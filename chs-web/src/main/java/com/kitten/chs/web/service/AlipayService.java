package com.kitten.chs.web.service;

import com.kitten.chs.common.utils.Response;

import java.math.BigDecimal;

public interface AlipayService {

    String createPayment(String orderNo, BigDecimal amount, String subject);

    boolean verifyCallback(java.util.Map<String, String> params);

}
