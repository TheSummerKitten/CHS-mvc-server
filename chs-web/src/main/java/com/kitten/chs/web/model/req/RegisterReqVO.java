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
public class RegisterReqVO {

    private String username;

    private String password;

    private String phone;

}
