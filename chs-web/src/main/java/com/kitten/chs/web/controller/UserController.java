package com.kitten.chs.web.controller;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.RegisterReqVO;
import com.kitten.chs.web.model.req.UpdateSelfInfoReqVO;
import com.kitten.chs.web.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@Api(tags = "Web 用户模块")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Response<?> register(@RequestBody RegisterReqVO reqVO) {
        return userService.register(reqVO);
    }

    @GetMapping("/info")
    @ApiOperation(value = "获取当前用户信息")
    public Response<?> getCurrentUserInfo() {
        return userService.getCurrentUserInfo();
    }

    @PostMapping("/info/update")
    @ApiOperation(value = "更新个人信息")
    public Response<?> updateSelfInfo(@RequestBody UpdateSelfInfoReqVO reqVO) {
        return userService.updateSelfInfo(reqVO);
    }

    @PostMapping("/avatar/upload")
    @ApiOperation(value = "上传头像")
    public Response<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    @GetMapping("/phone/check")
    @ApiOperation(value = "检查手机号是否已存在")
    public Response<?> checkPhoneExists(
            @RequestParam String phone,
            @RequestParam(required = false) Long excludeUserId) {
        return userService.checkPhoneExists(phone, excludeUserId);
    }

}
