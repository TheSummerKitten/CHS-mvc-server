package com.kitten.chs.admin.model.vo.rsp;

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
public class FindFoodPageListRespVO {

    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    private String image;

    private String category;

    private Integer status;

    private LocalDateTime createTime;

}
