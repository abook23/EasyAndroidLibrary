package com.android.easy.base.util;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abook23 on 2016/9/6.
 * E-mail abook23@163.com
 */
public class BeanUtils {

    public static Map<String, Object> toMap(Object o){
        Map<String, Object> map = new HashMap<>();
        try {
            Field[] fields = o.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String proName = field.getName();
                Object proValue = field.get(o);
                if (proValue != null)
                    map.put(proName, proValue);
                //map.put(proName.toUpperCase(), proValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }


    public static Object map2PO(Map<String, Object> map, Object o) throws Exception {
        if (!map.isEmpty()) {
            for (String k : map.keySet()) {
                Object v = "";
                if (!k.isEmpty()) {
                    v = map.get(k);
                }
                Field[] fields = o.getClass().getDeclaredFields();
                // String clzName = o.getClass().getSimpleName();
                for (Field field : fields) {
                    int mod = field.getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    if (field.getName().toUpperCase().equals(k)) {
                        field.setAccessible(true);
                        field.set(o, v);
                    }

                }
            }
        }
        return o;
    }

    public static <T> List<T> cursor2Bean(Cursor cursor, Class<T> c) throws IllegalAccessException, InstantiationException {
        List<T> list = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            Map<String, String> columnNameMap = new HashMap<>();
            T bean;
            Field[] arrField = c.getDeclaredFields();//getFields()只能获取public的字段
            while (cursor.moveToNext()) {
                bean = c.newInstance();
                for (Field f : arrField) {
                    String columnName = f.getName();
                    int columnIdx = cursor.getColumnIndex(columnName);//列是否存在
                    if (columnIdx == -1) {//将驼峰式命名的字符串转换为下划线大写方式
                        String _name = columnNameMap.get(columnName);
                        if (_name == null) {
                            _name = StringUtils.underscoreName(columnName);
                            columnNameMap.put(columnName, _name);
                        }
                        columnIdx = cursor.getColumnIndex(_name);//列是否存在
                    }
                    if (columnIdx != -1) {
                        if (!f.isAccessible()) {
                            f.setAccessible(true);//类中的成员变量为private,故必须进行此操作
                        }
                        Class<?> type = f.getType();
                        if (type == byte.class) {
                            f.set(bean, (byte) cursor.getShort(columnIdx));//set 值
                        } else if (type == short.class) {
                            f.set(bean, cursor.getShort(columnIdx));
                        } else if (type == int.class) {
                            f.set(bean, cursor.getInt(columnIdx));
                        } else if (type == long.class) {
                            f.set(bean, cursor.getLong(columnIdx));
                        } else if (type == String.class) {
                            f.set(bean, cursor.getString(columnIdx));
                        } else if (type == byte[].class) {
                            f.set(bean, cursor.getBlob(columnIdx));
                        } else if (type == boolean.class) {
                            f.set(bean, cursor.getInt(columnIdx) == 1);
                        } else if (type == float.class) {
                            f.set(bean, cursor.getFloat(columnIdx));
                        } else if (type == double.class) {
                            f.set(bean, cursor.getDouble(columnIdx));
                        }
                    }
                }
                list.add(bean);
            }
            columnNameMap.clear();
        }
        return list;
    }
}
