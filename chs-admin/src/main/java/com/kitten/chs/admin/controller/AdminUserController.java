package com.kitten.chs.admin.controller;

import com.kitten.chs.admin.model.vo.req.UpdatePasswordReqVO;
import com.kitten.chs.admin.service.AdminUserService;
import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author kitten
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 用户模块")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/password/update")
    @ApiOperation(value = "修改用户密码")
    @ApiOperationLog(description = "修改用户密码")
    public Response updatePassword(@RequestBody @Validated UpdatePasswordReqVO reqVO) {
        return adminUserService.updatePassword(reqVO);
    }

    @GetMapping("/user/info")
    public Response<?> FindCurrentUserInfo() {
        return adminUserService.findCurrentUserInfo();
    }

}
