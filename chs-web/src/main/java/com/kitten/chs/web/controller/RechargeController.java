package com.kitten.chs.web.controller;

import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.CreateRechargeReqVO;
import com.kitten.chs.web.model.rsp.CreateRechargeRespVO;
import com.kitten.chs.web.model.rsp.FindRechargeListRespVO;
import com.kitten.chs.web.service.AlipayService;
import com.kitten.chs.web.service.RechargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/recharge")
@Api(tags = "Web 用户充值模块")
public class RechargeController {

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private AlipayService alipayService;

    @PostMapping("/create")
    @ApiOperationLog(description = "创建充值订单")
    @ApiOperation(value = "创建充值订单")
    public Response<CreateRechargeRespVO> createRecharge(@RequestBody CreateRechargeReqVO reqVO) {
        return rechargeService.createRecharge(reqVO);
    }

    @GetMapping("/list")
    @ApiOperationLog(description = "查询充值记录列表")
    @ApiOperation(value = "查询充值记录列表")
    public Response<List<FindRechargeListRespVO>> findRechargeList() {
        return rechargeService.findRechargeList();
    }

    @PostMapping("/notify")
    @ApiOperation(value = "支付宝异步通知")
    public String handleAlipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            params.put(name, request.getParameter(name));
        }

        log.info("收到支付宝异步通知: {}", params);

        boolean verifyResult = alipayService.verifyCallback(params);
        if (!verifyResult) {
            log.error("支付宝异步通知验签失败");
            return "failure";
        }

        String tradeStatus = params.get("trade_status");
        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            log.info("支付宝交易状态不是成功: {}", tradeStatus);
            return "success";
        }

        String orderNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");

        Response<?> result = rechargeService.handlePayCallback(orderNo, tradeNo);
        if (result.isSuccess()) {
            return "success";
        } else {
            log.error("处理支付宝回调失败: {}", result.getMessage());
            return "failure";
        }
    }

    @PostMapping("/callback")
    @ApiOperationLog(description = "支付回调接口(预留)")
    @ApiOperation(value = "支付回调接口")
    public Response<?> handlePayCallback(
            @RequestParam String orderNo,
            @RequestParam String tradeNo) {
        return rechargeService.handlePayCallback(orderNo, tradeNo);
    }

}
