package com.kitten.chs.admin.model.vo.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author kitten
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserPageConListRespVO {

    private Long id;

    private String name;

    private String phone;

    private Integer gender;

    private String avatar;

    private LocalDateTime createTime;

}
