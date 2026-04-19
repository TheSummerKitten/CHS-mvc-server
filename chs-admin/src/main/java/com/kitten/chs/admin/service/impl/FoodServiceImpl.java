package com.kitten.chs.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kitten.chs.admin.model.vo.req.AddFoodReqVO;
import com.kitten.chs.admin.model.vo.req.DeleteFoodReqVO;
import com.kitten.chs.admin.model.vo.req.FindFoodPageListReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateFoodReqVO;
import com.kitten.chs.admin.model.vo.req.UpdateFoodStatusReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindFoodPageListRespVO;
import com.kitten.chs.admin.service.FoodService;
import com.kitten.chs.common.config.MinioConfig;
import com.kitten.chs.common.domain.dataObject.FoodDO;
import com.kitten.chs.common.domain.mapper.FoodMapper;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public PageResponse<FindFoodPageListRespVO> findFoodPageList(FindFoodPageListReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();

        Page<FoodDO> page = new Page<>(current, size);

        LambdaQueryWrapper<FoodDO> wrapper = new LambdaQueryWrapper<>();
        String name = reqVO.getName();
        String category = reqVO.getCategory();
        Integer status = reqVO.getStatus();

        wrapper.like(StringUtils.isNotBlank(name), FoodDO::getName, name != null ? name.trim() : null)
                .eq(StringUtils.isNotBlank(category), FoodDO::getCategory, category)
                .eq(Objects.nonNull(status), FoodDO::getStatus, status)
                .eq(FoodDO::getIsDeleted, false)
                .orderByDesc(FoodDO::getCreateTime);

        Page<FoodDO> foodDOPage = foodMapper.selectPage(page, wrapper);

        List<FoodDO> foodDOS = foodDOPage.getRecords();

        List<FindFoodPageListRespVO> vos = null;
        if (!CollectionUtils.isEmpty(foodDOS)) {
            vos = foodDOS.stream()
                    .map(foodDO -> {
                        String imageUrl = null;
                        if (StringUtils.isNotBlank(foodDO.getImage())) {
                            imageUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + foodDO.getImage();
                        }
                        return FindFoodPageListRespVO.builder()
                                .id(foodDO.getId())
                                .name(foodDO.getName())
                                .price(foodDO.getPrice())
                                .description(foodDO.getDescription())
                                .image(imageUrl)
                                .category(foodDO.getCategory())
                                .status(foodDO.getStatus())
                                .createTime(foodDO.getCreateTime())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return PageResponse.success(foodDOPage, vos);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> addFood(AddFoodReqVO reqVO) {
        if (StringUtils.isBlank(reqVO.getName())) {
            return Response.fail("菜品名称不能为空");
        }
        if (reqVO.getPrice() == null || reqVO.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return Response.fail("菜品价格必须大于0");
        }

        LambdaQueryWrapper<FoodDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodDO::getName, reqVO.getName())
                .eq(FoodDO::getIsDeleted, false);
        FoodDO existingFood = foodMapper.selectOne(wrapper);
        if (existingFood != null) {
            return Response.fail("菜品名称已存在");
        }

        FoodDO foodDO = FoodDO.builder()
                .name(reqVO.getName())
                .price(reqVO.getPrice())
                .description(reqVO.getDescription())
                .image(reqVO.getImage())
                .category(reqVO.getCategory())
                .status(reqVO.getStatus() != null ? reqVO.getStatus() : 1)
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .build();

        int result = foodMapper.insert(foodDO);
        if (result <= 0) {
            return Response.fail("新增菜品失败");
        }

        return Response.success("新增菜品成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> updateFood(UpdateFoodReqVO reqVO) {
        if (reqVO.getId() == null) {
            return Response.fail("菜品ID不能为空");
        }

        FoodDO existingFood = foodMapper.selectById(reqVO.getId());
        if (existingFood == null || existingFood.getIsDeleted()) {
            return Response.fail("菜品不存在");
        }

        if (StringUtils.isNotBlank(reqVO.getName()) && !reqVO.getName().equals(existingFood.getName())) {
            LambdaQueryWrapper<FoodDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FoodDO::getName, reqVO.getName())
                    .eq(FoodDO::getIsDeleted, false)
                    .ne(FoodDO::getId, reqVO.getId());
            FoodDO foodWithSameName = foodMapper.selectOne(wrapper);
            if (foodWithSameName != null) {
                return Response.fail("菜品名称已存在");
            }
        }

        FoodDO updateFood = FoodDO.builder()
                .id(reqVO.getId())
                .name(StringUtils.isNotBlank(reqVO.getName()) ? reqVO.getName() : existingFood.getName())
                .price(reqVO.getPrice() != null ? reqVO.getPrice() : existingFood.getPrice())
                .description(reqVO.getDescription() != null ? reqVO.getDescription() : existingFood.getDescription())
                .image(reqVO.getImage() != null ? reqVO.getImage() : existingFood.getImage())
                .category(reqVO.getCategory() != null ? reqVO.getCategory() : existingFood.getCategory())
                .status(reqVO.getStatus() != null ? reqVO.getStatus() : existingFood.getStatus())
                .updateTime(LocalDateTime.now())
                .build();

        int result = foodMapper.updateById(updateFood);
        if (result <= 0) {
            return Response.fail("更新菜品失败");
        }

        return Response.success("更新菜品成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> deleteFood(DeleteFoodReqVO reqVO) {
        if (reqVO.getId() == null) {
            return Response.fail("菜品ID不能为空");
        }

        FoodDO existingFood = foodMapper.selectById(reqVO.getId());
        if (existingFood == null || existingFood.getIsDeleted()) {
            return Response.fail("菜品不存在");
        }

        FoodDO deleteFood = FoodDO.builder()
                .id(reqVO.getId())
                .isDeleted(true)
                .updateTime(LocalDateTime.now())
                .build();

        int result = foodMapper.updateById(deleteFood);
        if (result <= 0) {
            return Response.fail("删除菜品失败");
        }

        return Response.success("删除菜品成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> updateFoodStatus(UpdateFoodStatusReqVO reqVO) {
        if (reqVO.getId() == null) {
            return Response.fail("菜品ID不能为空");
        }
        if (reqVO.getStatus() == null) {
            return Response.fail("状态不能为空");
        }

        FoodDO existingFood = foodMapper.selectById(reqVO.getId());
        if (existingFood == null || existingFood.getIsDeleted()) {
            return Response.fail("菜品不存在");
        }

        FoodDO updateFood = FoodDO.builder()
                .id(reqVO.getId())
                .status(reqVO.getStatus())
                .updateTime(LocalDateTime.now())
                .build();

        int result = foodMapper.updateById(updateFood);
        if (result <= 0) {
            return Response.fail("更新状态失败");
        }

        return Response.success("更新状态成功");
    }

}
