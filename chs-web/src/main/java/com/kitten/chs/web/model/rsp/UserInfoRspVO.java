package com.kitten.chs.web.model.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author kitten
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoRspVO {

    private Long id;

    private String username;

    private String phone;

    private BigDecimal balance;

    private Integer gender;

    private String avatar;

    private String role;

    private String address;

}
