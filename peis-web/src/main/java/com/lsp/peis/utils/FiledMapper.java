package com.lsp.peis.utils;

import java.lang.annotation.*;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/10/31 19:43
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FiledMapper {
    public String filed();
}
