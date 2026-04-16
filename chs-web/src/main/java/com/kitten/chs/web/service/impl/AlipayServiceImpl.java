package com.kitten.chs.web.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.kitten.chs.web.config.AlipayConfig;
import com.kitten.chs.web.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayConfig alipayConfig;

    @Override
    public String createPayment(String orderNo, BigDecimal amount, String subject) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());

        String amountStr = amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        String bizContent = String.format(
                "{\"out_trade_no\":\"%s\",\"total_amount\":\"%s\",\"subject\":\"%s\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}",
                orderNo, amountStr, subject
        );
        request.setBizContent(bizContent);

        try {
            String form = alipayClient.pageExecute(request).getBody();
            log.info("支付宝支付表单生成成功，订单号: {}", orderNo);
            return form;
        } catch (AlipayApiException e) {
            log.error("支付宝支付表单生成失败，订单号: {}, 错误: {}", orderNo, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        try {
            boolean result = AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getPublicKey(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSignType()
            );
            log.info("支付宝回调验签结果: {}", result);
            return result;
        } catch (AlipayApiException e) {
            log.error("支付宝回调验签失败: {}", e.getMessage(), e);
            return false;
        }
    }

}
