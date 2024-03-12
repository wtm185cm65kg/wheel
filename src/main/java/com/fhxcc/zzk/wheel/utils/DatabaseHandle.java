package com.fhxcc.zzk.wheel.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static com.fhxcc.zzk.wheel.component.DataBase.*;
import static com.fhxcc.zzk.wheel.component.DataBase.handleFields;

/**
 * 根据数据库字段生成一系列语句
 *
 * @author fhxcc
 */
public class DatabaseHandle {
    /**
     * TODO:
     * 生成实体类属性及Excel注释(以名称及对应的另一字段,如:名称->描述/备注)
     *
     * @param s1 从 navicat '设计表'页面粘贴过来的 字段名(一整列字段名)
     * @param s2 从 navicat '设计表'页面粘贴过来的 其他字段(一整列字段)
     */
    public static String generateDomain(String s1, String s2) {
        ArrayList<Map.Entry<String, String>> arrayList = handleFieldsAndTip(s1, s2);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> var : arrayList) {
            builder.append("@Excel(name = \"").append(var.getValue()).append("\")\n")
                    .append("private String ").append(changeToJavaFiled(var.getKey())).append(";\n");
        }
        return builder.toString();
    }


    /**
     * TODO: 集合多项方法批量生成MyBatis语句
     * 批量生成单表的Mybatis语句
     *
     * @param str 从navicat '设计表'页面粘贴过来的字段名列表
     * @param id  该字段数组中的主键,可大写也可小写 (仍含有下划线'_')
     */
    public static void generateMybatis(String str, String id) {
        // 将从navicat的'设计表'页面粘贴过来的字段名列表转为全小写字段名数组
        String[] chars = handleFields(str);
        // 判断该字段数组中是否包含主键
        if (Arrays.asList(chars).contains(id.toLowerCase())) {
            // 从原数组中剔除主键,获取新数组result
            ArrayList<String> list = new ArrayList<>();
            for (String var : chars) {
                if (!var.equals(id.toLowerCase())) {
                    list.add(var);
                }
            }
            String[] result = list.toArray(new String[0]);
            String tab = "  ";

            System.out.println(sql(chars, tab) + "\n");
            System.out.println(resultMap(result, id.toUpperCase(), tab) + "\n");
            System.out.println(selectByCondition(chars, tab) + "\n");
            System.out.println(trimInsert(chars, tab) + "\n");
            System.out.println(trimUpdate(result, id.toUpperCase(), tab) + "\n");
            System.out.println(delete(id.toUpperCase(), tab) + "\n");
        } else {
            throw new RuntimeException("主键输入有误,字段数组中并不包含该主键");
        }
    }

    /**
     * 生成MyBatis的<sql/>片段 (含该表的所有字段,主要用于查询语句中)
     */
    public static String sql(String[] chars, String tab) {
        StringBuilder builder = new StringBuilder();
        builder.append("<sql id=\"Base\">\n")
                .append(tab);
        for (String var : chars) {
            builder.append(var.toUpperCase()).append(",");
        }
        //删除当前最后一个字符
        builder.deleteCharAt(builder.length() - 1);
        builder.append("\n</sql>");
        return builder.toString();
    }

    /**
     * 生成MyBatis的<resultMap/>内容 (只对含'_'的字段进行处理,普通字段无需映射)
     */
    public static String resultMap(String[] chars, String id, String tab) {
        StringBuilder builder = new StringBuilder();
        builder.append("<resultMap id=\"BaseResult\" type=\"类型\">\n");
        builder.append(tab).append("<id column=\"").append(id).append("\" property=\"").append(changeToJavaFiled(id)).append("\"/>\n");
        for (String var : chars) {
            if (var.contains("_")) {
                builder.append(tab).append("<result column=\"").append(var.toUpperCase()).append("\" property=\"").append(changeToJavaFiled(var)).append("\"/>\n");
            }
        }
        builder.append("</resultMap>");
        return builder.toString();
    }

    /**
     * 生成MyBatis的<select/>基础查询语句部分 (使用<where/>及<if/>标签智能添加条件)
     */
    public static String selectByCondition(String[] chars, String tab) {
        StringBuilder builder = new StringBuilder();
        builder.append("<select id=\"方法名\" resultMap=\"BaseResult\">\n")
                .append(tab).append("select <include refid=\"Base\" /> from 表名\n")
                .append(tab).append("<where>\n");
        for (String var : chars) {
            builder.append(tab).append(tab).append("<if test=\"").append(changeToJavaFiled(var)).append(" != null\">and ").append(var.toUpperCase()).append(" = #{").append(changeToJavaFiled(var)).append("}</if>\n");
        }
        builder.append(tab).append("<where>\n")
                .append("</select>");
        return builder.toString();
    }

    /**
     * 生成MyBatis的<insert/>插入语句部分 (使用<trim/>及<if/>标签智能插入)
     */
    public static String trimInsert(String[] chars, String tab) {
        StringBuilder builder = new StringBuilder();
        builder.append("<insert id=\"方法名\">\n")
                .append(tab).append("insert into 表名\n")
                .append(tab).append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (String var : chars) {
            builder.append(tab).append(tab).append("<if test=\"").append(changeToJavaFiled(var)).append(" != null\">").append(var.toUpperCase()).append(",</if>\n");
        }
        builder.append(tab).append("</trim>\n")
                .append(tab).append("<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
        for (String var : chars) {
            builder.append(tab).append(tab).append("<if test=\"").append(changeToJavaFiled(var)).append(" != null\">#{").append(changeToJavaFiled(var)).append("},</if>\n");
        }
        builder.append(tab).append("</trim>\n")
                .append("</insert>");
        return builder.toString();
    }

    /**
     * 生成MyBatis的<update/>更新语句部分 (使用<trim/>及<if/>标签智能更新)
     */
    public static String trimUpdate(String[] chars, String id, String tab) {
        StringBuilder builder = new StringBuilder();
        builder.append("<update id=\"方法名\">\n")
                .append(tab).append("update 表名\n")
                .append(tab).append("<trim prefix=\"SET\" suffixOverrides=\",\">\n");
        for (String var : chars) {
            builder.append(tab).append(tab).append("<if test=\"").append(changeToJavaFiled(var)).append(" != null\">").append(var.toUpperCase()).append(" = #{").append(changeToJavaFiled(var)).append("},</if>\n");
        }
        builder.append(tab).append("</trim>\n")
                .append(tab).append("where ").append(id).append("= trim(#{").append(changeToJavaFiled(id)).append("})\n")
                .append("</update>");
        return builder.toString();
    }

    /**
     * 生成MyBatis的<delete/>删除语句部分
     */
    public static String delete(String id, String tab) {
        return "<delete id=\"方法名\">\n" +
                tab + "delete from 表名\n" +
                tab + "where " + id + "= trim(#{" + changeToJavaFiled(id) + "})\n" +
                "</delete>";
    }
}
