package io.github.stanxlab.codegen.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateTypeEnum {

    beetl("btl"),
    velocity("vm"),
    freemarker("ftl");

    private final String suffix;

}
