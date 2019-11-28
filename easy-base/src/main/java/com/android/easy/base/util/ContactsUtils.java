package com.android.easy.base.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abook23 on 2016/1/4.
 */
public class ContactsUtils {

    public static List<String[]> getPhoneContacts(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        List<String[]> listPhone = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {

                CallLog callLog = new CallLog();
                String phone_number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (TextUtils.isEmpty(phone_number))
                    continue;
                String phone_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String[] mPhone = new String[2];
                mPhone[0] = phone_number;
                mPhone[1] = phone_name;
                listPhone.add(mPhone);
            }
            cursor.close();
        }
        return listPhone;
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
