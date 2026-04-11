package com.kitten.chs.admin.controller;

import com.kitten.chs.admin.model.vo.req.AddFoodReqVO;
import com.kitten.chs.admin.model.vo.req.DeleteFoodReqVO;
import com.kitten.chs.admin.model.vo.req.FindFoodPageListReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateFoodReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateFoodStatusReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindFoodPageListRespVO;
import com.kitten.chs.admin.model.vo.rsp.UploadFoodImageRspVO;
import com.kitten.chs.admin.service.FoodService;
import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.FileUploadUtil;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/food")
@Api(tags = "Admin 菜品模块")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @PostMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "菜品分页查询")
    @ApiOperation(value = "菜品分页查询")
    public PageResponse<FindFoodPageListRespVO> findFoodPageList(@RequestBody FindFoodPageListReqVO reqVO) {
        return foodService.findFoodPageList(reqVO);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "新增菜品")
    @ApiOperation(value = "新增菜品")
    public Response<?> addFood(@RequestBody AddFoodReqVO reqVO) {
        return foodService.addFood(reqVO);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "更新菜品信息")
    @ApiOperation(value = "更新菜品信息")
    public Response<?> updateFood(@RequestBody UpdateFoodReqVO reqVO) {
        return foodService.updateFood(reqVO);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "删除菜品")
    @ApiOperation(value = "删除菜品")
    public Response<?> deleteFood(@RequestBody DeleteFoodReqVO reqVO) {
        return foodService.deleteFood(reqVO);
    }

    @PostMapping("/status/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "更新菜品状态")
    @ApiOperation(value = "更新菜品状态")
    public Response<?> updateFoodStatus(@RequestBody UpdateFoodStatusReqVO reqVO) {
        return foodService.updateFoodStatus(reqVO);
    }

    @PostMapping("/image/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'SW')")
    @ApiOperationLog(description = "上传菜品图片")
    @ApiOperation(value = "上传菜品图片")
    public Response<?> uploadFoodImage(@RequestParam("file") MultipartFile file) {
        FileUploadUtil.UploadResult result = fileUploadUtil.uploadFoodImage(file);
        if (!result.isSuccess()) {
            return Response.fail(result.getMessage());
        }
        UploadFoodImageRspVO rspVO = UploadFoodImageRspVO.builder()
                .imageUrl(result.getUrl())
                .objectName(result.getObjectName())
                .build();
        return Response.success(rspVO);
    }

}
