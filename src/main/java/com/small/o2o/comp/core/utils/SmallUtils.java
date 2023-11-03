package com.small.o2o.comp.core.utils;

import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @Project : small-o2o-compare
 * @Author : zhangzongyuan
 * @Description : [ SmallUtil ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/4 1:58
 * @Version ： 1.0
 **/
public class SmallUtils {

    public static boolean isNotEmpty(@Nullable Collection<?> collection){
        return !isEmpty(collection);
    }

    public static boolean isEmpty(@Nullable Collection<?> collection){
        return org.springframework.util.CollectionUtils.isEmpty(collection);
    }


    public static boolean isNotEmpty(@Nullable Object object){
        return !isEmpty(object);
    }

    public static boolean isEmpty(@Nullable Object object) {
        return org.springframework.util.ObjectUtils.isEmpty(object);
    }

    public static boolean hasText(@Nullable String str){
        return org.springframework.util.StringUtils.hasText(str);
    }


}
