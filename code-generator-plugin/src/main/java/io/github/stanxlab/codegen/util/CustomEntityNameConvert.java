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

    // 新增工具方法：下划线转驼峰并首字母小写（用于属性名）
    private String toCamelCaseLowerFirst(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            return columnName;
        }
        
        StringBuilder result = new StringBuilder();
        String[] parts = columnName.split("_");
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            
            if (i == 0) {
                // 第一个部分首字母小写
                result.append(Character.toLowerCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            } else {
                // 后续部分首字母大写
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }

    @Override
    public String propertyNameConvert(TableField tableField) {
        log.info("propertyNameConvert: tableField.getColumnName() = {}, tableField.getPropertyName() = {}", tableField.getColumnName(), tableField.getPropertyName());
        
        // 如果已经有propertyName，则直接使用；否则对columnName进行下划线转驼峰处理
        if (tableField.getPropertyName() != null && !tableField.getPropertyName().isEmpty()) {
            return tableField.getPropertyName();
        } else {
            // 将数据库字段名（下划线格式）转换为Java属性名（驼峰格式，首字母小写）
            String camelCaseName = toCamelCaseLowerFirst(tableField.getColumnName());
            log.info("转换字段名: {} -> {}", tableField.getColumnName(), camelCaseName);
            return camelCaseName;
        }
    }
}
