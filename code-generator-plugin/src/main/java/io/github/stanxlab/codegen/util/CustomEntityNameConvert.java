package io.github.stanxlab.codegen.util;

import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomEntityNameConvert implements INameConvert {

    private final String entitySuffix;

    public CustomEntityNameConvert(String entitySuffix) {
        this.entitySuffix = entitySuffix;
    }

    @Override
    public String entityNameConvert(TableInfo tableInfo) {
        // 以表名为基础，转为驼峰并首字母大写
        String tableName = tableInfo.getName();
        String entityName = toCamelCaseUpperFirst(tableName);
        return entityName + entitySuffix;
    }

    // 工具方法：下划线转驼峰并首字母大写
    private String toCamelCaseUpperFirst(String tableName) {
        StringBuilder result = new StringBuilder();
        for (String part : tableName.split("_")) {
            if (part.isEmpty()) continue;
            result.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    @Override
    public String propertyNameConvert(TableField tableField) {
        log.info("propertyNameConvert: tableField.getColumnName() = {}, tableField.getPropertyName() = {}", tableField.getColumnName(), tableField.getPropertyName());
        return tableField.getPropertyName() == null ? tableField.getColumnName() : tableField.getPropertyName();
    }
}
