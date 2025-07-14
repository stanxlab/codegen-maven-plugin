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
        log.info("entityNameConvert: tableInfo.getEntityName() = {}", tableInfo.getEntityName());
        String entityName = tableInfo.getEntityName();
        return entityName + entitySuffix;
    }

    @Override
    public String propertyNameConvert(TableField tableField) {
        log.info("propertyNameConvert: tableField.getColumnName() = {}, tableField.getPropertyName() = {}", tableField.getColumnName(), tableField.getPropertyName());
        return tableField.getPropertyName() == null ? tableField.getColumnName() : tableField.getPropertyName();
    }
}
