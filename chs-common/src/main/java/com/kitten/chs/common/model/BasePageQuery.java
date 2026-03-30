package com.kitten.chs.common.model;

import lombok.Data;

/**
 * @author kitten
 */
@Data
public class BasePageQuery {

    private Long current = 1L;

    private Long size = 10L;

}
