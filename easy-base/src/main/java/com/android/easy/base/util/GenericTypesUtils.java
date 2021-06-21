package com.android.easy.base.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * description:    泛型管理工具
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2021/3/15 15:49
 * updateUser:     更新者：
 * updateDate:     2021/3/15 15:49
 * updateRemark:   更新说明：
 * version:        1.0
 */
public class GenericTypesUtils {

    public static <T> T newInstancePresenter(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArray = parameterizedType.getActualTypeArguments();
            Class<?> aClass = (Class<?>) typeArray[0];
            return (T) aClass.newInstance();
        }
        return null;
    }
}
