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

    public String getModuleName(String baseName) {
        if (StringUtils.isEmpty(this.suffix)) {
            return "";
        }
        return String.format("%s-%s", baseName, this.suffix);
    }

    // 静态方法来获取特定类型的模块名
    public static String getDaoModuleName(String baseName) {
        return DAO.getModuleName(baseName);
    }

    public static String getServiceModuleName(String baseName) {
        return SERVICE.getModuleName(baseName);
    }

    public static String getManagerModuleName(String baseName) {
        return MANAGER.getModuleName(baseName);
    }

    public static String getWebModuleName(String baseName) {
        return WEB.getModuleName(baseName);
    }

    public static String getCommonModuleName(String baseName) {
        return COMMON.getModuleName(baseName);
    }

    public static String getFacadeModuleName(String baseName) {
        return FACADE.getModuleName(baseName);
    }

    // 获取所有模块名的列表
    public static String[] getAllModuleNames(String baseName) {
        return new String[]{
                DAO.getModuleName(baseName),
                SERVICE.getModuleName(baseName),
                MANAGER.getModuleName(baseName),
                WEB.getModuleName(baseName),
                COMMON.getModuleName(baseName),
                TEST.getModuleName(baseName),
                FACADE.getModuleName(baseName)
        };
    }

    // getAllModuleNames 返回结果Map<ModuleNameEnum, String>
    public static Map<ModuleNameEnum, String> getAllModuleNamesMap(String baseName) {
        return new HashMap<ModuleNameEnum, String>() {{
            put(DAO, DAO.getModuleName(baseName));
            put(SERVICE, SERVICE.getModuleName(baseName));
            put(MANAGER, MANAGER.getModuleName(baseName));
            put(WEB, WEB.getModuleName(baseName));
            put(COMMON, COMMON.getModuleName(baseName));
            put(TEST, TEST.getModuleName(baseName));
            put(FACADE, FACADE.getModuleName(baseName));
        }};
    }

}
