package com.kitten.chs.admin.controller;

import com.kitten.chs.admin.model.vo.req.DeleteUserReqVO;
import com.kitten.chs.admin.model.vo.req.FindUserPageConListReqVO;
import com.kitten.chs.admin.model.vo.req.UpdatePasswordReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateUserReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindUserPageConListRespVO;
import com.kitten.chs.admin.service.AdminUserService;
import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author kitten
 */
@RestController
@RequestMapping("/admin/user")
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

    @GetMapping("/info")
    public Response<?> FindCurrentUserInfo() {
        return adminUserService.findCurrentUserInfo();
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperationLog(description = "用户分页条件查询")
    public PageResponse<FindUserPageConListRespVO> findUserPageConditionList(@RequestBody FindUserPageConListReqVO reqVO) {
        return adminUserService.findUserPageConditionList(reqVO);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperationLog(description = "用户分页条件查询")
    public Response<?> deleteUser(@RequestBody DeleteUserReqVO reqVO) {
        return adminUserService.deleteUser(reqVO);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperationLog(description = "用户基础信息更新")
    public Response<?> updateUser(@RequestBody UpdateUserReqVO reqVO) {
        return adminUserService.updateUserInfo(reqVO);
    }

}
