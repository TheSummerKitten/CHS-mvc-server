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
    LOGIN_FAIL("S-10001", "登录失败"),
    // ----------- 业务异常状态码 -----------
    INIT_PROJECT_ERROR("B-20000", "业务异常 - 请重试"),
    PARAM_NOT_VALID("B-20001", "参数校验失败"),
    USERNAME_OR_PWD_ERROR("B-20002", "用户名或密码错误"),
    UNAUTHORIZED("B-20003", "未登录"),
    FORBIDDEN("B-20004", "无权限"),
    USER_DELETE_ERROR("B-20005", "用户删除失败"),
    UPDATE_USER_REQ_PARAM_INVALID("B-20006", "更新参数异常"),
    USER_NOT_EXIST("B-20007", "该用户不存在"),
    ;

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

}
