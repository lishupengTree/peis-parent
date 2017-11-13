package com.lsp.his.utils;

import java.lang.annotation.*;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 20:45
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

public @interface FiledMapper {
    //public int id();
    public String filed();

}
