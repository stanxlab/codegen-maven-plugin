package io.github.stanxlab.codegen.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplatePathEnum {

    MYBATIS_PLUS("/templates/mybatis-plus/", "mybatis-plus模板文件"),
    MYBATIS("/templates/mybatis/", "mybatis 模板文件"),
    OTHERS("/templates/others/", "其他文件，启动类、配置文件"),
    BASE_DIR("/templates/", "pom 等通用文件"),
    ;

    private final String path;
    private final String desc;

}
