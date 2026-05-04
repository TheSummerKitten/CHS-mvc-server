package com.kitten.chs.admin.model.vo.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddFoodReqVO {

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

}
