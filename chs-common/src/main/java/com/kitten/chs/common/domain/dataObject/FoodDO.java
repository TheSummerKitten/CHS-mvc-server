package com.kitten.chs.common.domain.dataObject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_food")
public class FoodDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    private Integer calories;

    private BigDecimal protein;

    private BigDecimal fat;

    private BigDecimal carbohydrate;

    private String healthTags;

    private String image;

    private String category;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isDeleted;

}
