package com.kitten.chs.web.model.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserFoodListRespVO {

    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    private String image;

    private String category;

}
