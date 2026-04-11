package com.kitten.chs.web.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kitten
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSelfInfoReqVO {

    private String phone;

    private Integer gender;

    private String avatar;

}
