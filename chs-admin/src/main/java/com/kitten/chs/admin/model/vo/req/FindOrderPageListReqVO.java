package com.kitten.chs.admin.model.vo.req;

import com.kitten.chs.common.model.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindOrderPageListReqVO extends BasePageQuery {

    private String username;

    private Integer status;

    private String orderNo;

}
