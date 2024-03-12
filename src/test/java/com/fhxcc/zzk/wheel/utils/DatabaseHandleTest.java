package com.fhxcc.zzk.wheel.utils;

import org.junit.jupiter.api.Test;

import java.util.*;

import static com.fhxcc.zzk.wheel.component.DataBase.*;
import static com.fhxcc.zzk.wheel.utils.DatabaseHandle.generateDomain;

public class DatabaseHandleTest {
    @Test
    public void testGenerateMybatis() {
        String chars = "CJ_ID\n" +
                "DMMC\n" +
                "GDWR_DAFL\n" +
                "CJIDOLD\n" +
                "W_DEPTH\n";

        // 批量生成Mybatis语句
        DatabaseHandle.generateMybatis(chars, "cj_id");

        System.out.println("\n<-------------------------------------------------------------->\n");

        // 使用changeToJavaFiled拼凑一个例子: 前端rules规则的限制条件
        for (String var : handleFields(chars)) {
            System.out.println(
                    changeToJavaFiled(var) +
                            ": [\n" +
                            "  {\n" +
                            "    pattern: /^-?\\d{0,3}(?:\\.\\d{0,9})?$/,\n" +
                            "    message: \"请输入一个小数位不超过9位、整数位不超过3位的浮点数\",\n" +
                            "    trigger: \"blur\",\n" +
                            "  },\n" +
                            "],"
            );
        }
    }

    @Test
    public void testGenerateDomain() {
        String s1 = "WSWL_CD\n" +
                "WSWL_NM\n" +
                "LGTD\n" +
                "LTTD\n";
        String s2 = "机井代码\n" +
                "机井名称\n" +
                "经度\n" +
                "纬度\n";

        // 生成实体类属性及Excel注释(以名称及对应的另一字段,如:名称->描述/备注)
        System.out.println(generateDomain(s1, s2));

        System.out.println("\n<-------------------------------------------------------------->\n");

        //使用handleFieldsAndTip拼凑一个例子: 批量生成element-ui的表单信息(以名称及对应的备注为例)
        ArrayList<Map.Entry<String, String>> arrayList = handleFieldsAndTip(s1, s2);
        for (int i = 0; i < arrayList.size(); i = i + 2) {
            System.out.println(
                    "<el-row>\n" +
                    "  <el-col :span=\"11\">\n" +
                    "    <el-form-item label=\"" + arrayList.get(i).getValue() + ":\" prop=\"" + changeToJavaFiled(arrayList.get(i).getKey()) + "\">\n" +
                    "      <el-input\n" +
                    "        clearable\n" +
                    "        v-model.trim=\"form." + changeToJavaFiled(arrayList.get(i).getKey()) + "\"\n" +
                    "        placeholder=\"请输入" + arrayList.get(i).getValue() + "\"\n" +
                    "      />\n" +
                    "    </el-form-item>\n" +
                    "  </el-col>\n" +
                    "  <el-col :span=\"11\">\n" +
                    "    <el-form-item label=\"" + arrayList.get(i + 1).getValue() + ":\" prop=\"" + changeToJavaFiled(arrayList.get(i + 1).getKey()) + "\">\n" +
                    "      <el-input\n" +
                    "        clearable\n" +
                    "        v-model.trim=\"form." + changeToJavaFiled(arrayList.get(i + 1).getKey()) + "\"\n" +
                    "        placeholder=\"请输入" + arrayList.get(i + 1).getValue() + "\"\n" +
                    "      />\n" +
                    "    </el-form-item>\n" +
                    "  </el-col>\n" +
                    "</el-row>"
            );
        }
    }
}
