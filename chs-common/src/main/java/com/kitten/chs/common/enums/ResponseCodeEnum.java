package com.kitten.chs.common.enums;

import com.kitten.chs.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author kitten
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("S-10000", "出错啦，后台小哥正在努力修复中..."),

    // ----------- 业务异常状态码 -----------
    INIT_PROJECT_ERROR("B-20000", "业务异常 - 请重试"),
    PARAM_NOT_VALID("B-20001", "参数校验失败")
    ;

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

}
