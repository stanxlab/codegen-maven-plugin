package io.github.stanxlab.codegen.entity;

import cn.hutool.system.SystemUtil;
import io.github.stanxlab.codegen.enums.TemplateTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 入参类
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class Parameters {

    /**
     * default: com.stanxlab.demo
     */
    private String outputPackage = "com.stanxlab.demo";

    /**
     * default: codegen
     */
    private String baseProjectName = "codegen";

    private String author = SystemUtil.get("user.name", false);

    private String tables;

    private String[] tablePrefix;

    /**
     * 默认模板类型，目前只支持beetl模板引擎
     */
    private TemplateTypeEnum templateType = TemplateTypeEnum.beetl;

    /**
     * 是否为lombok模型（默认 true）
     */
    private boolean lombokModel = true;

    /**
     * 是否生成CRUD 代码
     */
    private boolean enableCrudCode = true;

    /**
     * 是否为多模块模式
     */
    private boolean multiModule = true;

    /**
     * 数据库配置
     */
    private DbInfo dbInfo;

    /**
     * 默认包配置
     */
    private DefaultPackageConfig packageConfig;
}
