package io.github.stanxlab.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ORMTypeEnum {
    MYBATIS_PLUS("mybatis-plus", TemplatePathEnum.MYBATIS_PLUS),
    MYBATIS("mybatis", TemplatePathEnum.MYBATIS),
    ;

    private String ormName;

    private TemplatePathEnum templatePath;
}
