package com.kitten.chs.web.controller;

import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.FindUserFoodListReqVO;
import com.kitten.chs.web.model.rsp.FindUserFoodListRespVO;
import com.kitten.chs.web.service.UserFoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
@Api(tags = "Web 用户菜品模块")
public class UserFoodController {

    @Autowired
    private UserFoodService userFoodService;

    @PostMapping("/list")
    @ApiOperationLog(description = "用户查询菜品列表")
    @ApiOperation(value = "用户查询菜品列表")
    public Response<List<FindUserFoodListRespVO>> findUserFoodList(@RequestBody FindUserFoodListReqVO reqVO) {
        return userFoodService.findUserFoodList(reqVO);
    }

}
