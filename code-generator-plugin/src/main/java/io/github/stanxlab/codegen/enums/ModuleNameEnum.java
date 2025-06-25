package io.github.stanxlab.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块名枚举
 * 4层模型：dao、service、manager、web ; 生成模块名为 proj-dao 、proj-service、proj-manager、proj-web
 * 额外加入： common(通用), facade(用于Feign调用) ; 生成模块名为 proj-common、proj-facade
 */
@Getter
@AllArgsConstructor
public enum ModuleNameEnum {
    DAO("dao"),
    MANAGER("manager"),
    SERVICE("service"),
    WEB("start"),
    COMMON("common"),
    FACADE("facade"),
    TEST("test"),
    PARENT(""), // 父pom
    ;

    private final String suffix;

}
