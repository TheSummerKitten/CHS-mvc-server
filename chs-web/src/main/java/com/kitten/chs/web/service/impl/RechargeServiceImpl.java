package com.kitten.chs.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kitten.chs.common.domain.dataObject.RechargeDO;
import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.mapper.RechargeMapper;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.CreateRechargeReqVO;
import com.kitten.chs.web.model.rsp.CreateRechargeRespVO;
import com.kitten.chs.web.model.rsp.FindRechargeListRespVO;
import com.kitten.chs.web.service.AlipayService;
import com.kitten.chs.web.service.RechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    private RechargeMapper rechargeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AlipayService alipayService;

    @Override
    public Response<CreateRechargeRespVO> createRecharge(CreateRechargeReqVO reqVO) {
        if (reqVO.getAmount() == null || reqVO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Response.fail("充值金额必须大于0");
        }

        if (reqVO.getAmount().compareTo(new BigDecimal("30")) < 0) {
            return Response.fail("充值金额不能少于30元");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserDO userDO = userMapper.selectByUsername(username);
        if (userDO == null) {
            return Response.fail("用户不存在");
        }

        String orderNo = generateRechargeNo();

        RechargeDO rechargeDO = RechargeDO.builder()
                .userId(userDO.getId())
                .orderNo(orderNo)
                .amount(reqVO.getAmount())
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        rechargeMapper.insert(rechargeDO);

        String payForm = alipayService.createPayment(orderNo, reqVO.getAmount(), "账户充值");

        CreateRechargeRespVO respVO = CreateRechargeRespVO.builder()
                .id(rechargeDO.getId())
                .orderNo(orderNo)
                .amount(reqVO.getAmount())
                .status(0)
                .createTime(rechargeDO.getCreateTime())
                .payUrl(payForm)
                .build();

        return Response.success(respVO);
    }

    @Override
    public Response<List<FindRechargeListRespVO>> findRechargeList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserDO userDO = userMapper.selectByUsername(username);
        if (userDO == null) {
            return Response.fail("用户不存在");
        }

        LambdaQueryWrapper<RechargeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargeDO::getUserId, userDO.getId())
                .eq(RechargeDO::getIsDeleted, false)
                .orderByDesc(RechargeDO::getCreateTime);

        List<RechargeDO> rechargeDOS = rechargeMapper.selectList(wrapper);

        List<FindRechargeListRespVO> vos = rechargeDOS.stream()
                .map(rechargeDO -> FindRechargeListRespVO.builder()
                        .id(rechargeDO.getId())
                        .orderNo(rechargeDO.getOrderNo())
                        .amount(rechargeDO.getAmount())
                        .status(rechargeDO.getStatus())
                        .payTime(rechargeDO.getPayTime())
                        .tradeNo(rechargeDO.getTradeNo())
                        .createTime(rechargeDO.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return Response.success(vos);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> handlePayCallback(String orderNo, String tradeNo) {
        if (orderNo == null || tradeNo == null) {
            return Response.fail("参数无效");
        }

        LambdaQueryWrapper<RechargeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RechargeDO::getOrderNo, orderNo)
                .eq(RechargeDO::getIsDeleted, false);

        RechargeDO rechargeDO = rechargeMapper.selectOne(queryWrapper);
        if (rechargeDO == null) {
            return Response.fail("充值订单不存在");
        }

        if (rechargeDO.getStatus() == 1) {
            return Response.success("该订单已支付");
        }

        LambdaUpdateWrapper<RechargeDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(RechargeDO::getStatus, 1)
                .set(RechargeDO::getTradeNo, tradeNo)
                .set(RechargeDO::getPayTime, LocalDateTime.now())
                .set(RechargeDO::getUpdateTime, LocalDateTime.now())
                .eq(RechargeDO::getId, rechargeDO.getId());

        int result = rechargeMapper.update(null, updateWrapper);
        if (result <= 0) {
            return Response.fail("更新充值订单状态失败");
        }

        LambdaUpdateWrapper<UserDO> userUpdateWrapper = new LambdaUpdateWrapper<>();
        userUpdateWrapper.setSql("balance = balance + " + rechargeDO.getAmount())
                .set(UserDO::getUpdateTime, LocalDateTime.now())
                .eq(UserDO::getId, rechargeDO.getUserId());

        int userResult = userMapper.update(null, userUpdateWrapper);
        if (userResult <= 0) {
            throw new RuntimeException("更新用户余额失败");
        }

        log.info("充值成功，订单号: {}, 交易号: {}, 金额: {}", orderNo, tradeNo, rechargeDO.getAmount());

        return Response.success("支付成功");
    }

    private String generateRechargeNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "R" + timestamp + uuid;
    }

}
