package com.kitten.chs.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kitten.chs.common.config.MinioConfig;
import com.kitten.chs.common.domain.dataObject.FoodDO;
import com.kitten.chs.common.domain.mapper.FoodMapper;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.FindUserFoodListReqVO;
import com.kitten.chs.web.model.rsp.FindUserFoodListRespVO;
import com.kitten.chs.web.service.UserFoodService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserFoodServiceImpl implements UserFoodService {

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public Response<List<FindUserFoodListRespVO>> findUserFoodList(FindUserFoodListReqVO reqVO) {
        LambdaQueryWrapper<FoodDO> wrapper = new LambdaQueryWrapper<>();
        String name = reqVO.getName();
        String category = reqVO.getCategory();

        wrapper.like(StringUtils.isNotBlank(name), FoodDO::getName, name != null ? name.trim() : null)
                .eq(StringUtils.isNotBlank(category), FoodDO::getCategory, category)
                .eq(FoodDO::getStatus, 1)
                .eq(FoodDO::getIsDeleted, false)
                .orderByDesc(FoodDO::getCreateTime);

        List<FoodDO> foodDOS = foodMapper.selectList(wrapper);

        List<FindUserFoodListRespVO> vos = null;
        if (foodDOS != null && !foodDOS.isEmpty()) {
            vos = foodDOS.stream()
                    .map(foodDO -> {
                        String imageUrl = null;
                        if (StringUtils.isNotBlank(foodDO.getImage())) {
                            imageUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + foodDO.getImage();
                        }
                        return FindUserFoodListRespVO.builder()
                                .id(foodDO.getId())
                                .name(foodDO.getName())
                                .price(foodDO.getPrice())
                                .description(foodDO.getDescription())
                                .calories(foodDO.getCalories())
                                .protein(foodDO.getProtein())
                                .fat(foodDO.getFat())
                                .carbohydrate(foodDO.getCarbohydrate())
                                .healthTags(foodDO.getHealthTags())
                                .image(imageUrl)
                                .category(foodDO.getCategory())
                                .build();
                    })
                    .collect(Collectors.toList());
        } else {
            vos = new ArrayList<>();
        }

        return Response.success(vos);
    }

}
