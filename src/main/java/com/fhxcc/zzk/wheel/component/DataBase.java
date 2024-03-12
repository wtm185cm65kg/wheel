package com.fhxcc.zzk.wheel.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 处理数据库字段
 * @author fhxcc
 */
public class DataBase {
    /**
     * 将数据库字段名转为符合java的变量名(小驼峰命名),每次只处理一个字串
     *
     * @param field 需处理的数据库字段名(单个字段)
     */
    public static String changeToJavaFiled(String field) {
        String[] fields = field.toLowerCase().split("_");
        StringBuilder builder = new StringBuilder(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            char[] cs = fields[i].toCharArray();
            cs[0] -= 32;
            builder.append(String.valueOf(cs));
        }
        return builder.toString();
    }

    /**
     * 将数据库字段及其对应的描述/备注转为一个类似于List<Map<String, String>>的数组对象(顺序与s1和s2的顺序一致)
     * 使展示字段与其描述/备注一一对应
     *
     * @param s1 从 navicat '设计表'页面粘贴过来的 字段名(一整列字段名)
     * @param s2 从 navicat '设计表'页面粘贴过来的 备注(一整列备注)
     */
    public static ArrayList<Map.Entry<String, String>> handleFieldsAndTip(String s1, String s2) {
        String[] fields = s1.split("\n");
        String[] tips = s2.split("\n");

        if (fields.length == tips.length) {
            // 使用LinkedHashMap而非HashMap,可以保证插入顺序不被打乱
            Map<String, String> map = new LinkedHashMap<>();
            for (int i = 0; i < fields.length; i++) {
                map.put(fields[i].toLowerCase(), tips[i]);
            }
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            return new ArrayList<>(entrySet);
        } else {
            throw new RuntimeException("请检查输入的两组字串元素数量是否相同");
        }
    }

    /**
     * 将从navicat的'设计表'页面粘贴过来的字段名转为全小写字段名数组
     *
     * @param fields 从 navicat '设计表'页面粘贴过来的字段名(一整列字段名)
     * @return 拆解为全小写字段名数组(仍含下划线 ' _ ')
     */
    public static String[] handleFields(String fields) {
        String[] chars = fields.split("\n");
        for (int i = 0; i < chars.length; i++) {
            chars[i] = chars[i].toLowerCase();
        }
        return chars;
    }
}
