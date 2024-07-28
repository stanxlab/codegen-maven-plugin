package io.github.stanxlab.codegen.generator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.Controller;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.BeetlTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.util.FileUtils;
import io.github.stanxlab.codegen.entity.DbInfo;
import io.github.stanxlab.codegen.entity.DefaultPackageConfig;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import io.github.stanxlab.codegen.enums.*;
import io.github.stanxlab.codegen.util.PathBuilderUtil;
import io.github.stanxlab.codegen.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.function.Function;

/**
 * 基础类
 */
@Slf4j
public abstract class BaseGenerator {

    @Getter
    protected ProjectInfo projectInfo;

    @Getter
    @Setter
    protected ORMTypeEnum ormType;

    protected DefaultPackageConfig packageConfig;

    public abstract void execute();

    /**
     * 获取模板文件的目录
     *
     * @return 模板路径
     */
    protected TemplatePathEnum getTemplateBasePath() {
        return this.ormType.getTemplatePath();
    }

    public BaseGenerator(ProjectInfo projectInfo) {
        this(projectInfo, ORMTypeEnum.MYBATIS_PLUS);
    }

    public BaseGenerator(ProjectInfo projectInfo, ORMTypeEnum ormType) {
        this.projectInfo = projectInfo;
        this.ormType = ormType;
        init();
    }

    private void init() {
        DefaultPackageConfig defaultPackageConfig = projectInfo.getParameters().getPackageConfig();
        if (defaultPackageConfig != null) {
            packageConfig = defaultPackageConfig;
        } else {
            packageConfig = new DefaultPackageConfig();
        }

        // 优先使用 outputPackage配置
        if (StringUtils.isNotEmpty(projectInfo.getParameters().getOutputPackage())) {
            packageConfig.setParent(projectInfo.getParameters().getOutputPackage());
        }

        packageConfig.init(ormType);
    }

    protected AbstractTemplateEngine getTemplateEngine() {
        //模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker 或 Enjoy
//               fastAutoGenerator.templateEngine(new BeetlTemplateEngine())
//               fastAutoGenerator.templateEngine(new FreemarkerTemplateEngine())
        TemplateTypeEnum templateType = getTemplateType();
        switch (templateType) {
            case beetl:
                return new BeetlTemplateEngine();
            case velocity:
                return new VelocityTemplateEngine();
            case freemarker:
                return new FreemarkerTemplateEngine();
            default:
                return new VelocityTemplateEngine();
        }
    }

    protected DataSourceConfig.Builder dataSourceConfigBuilder() {
        DbInfo dbInfo = projectInfo.getParameters().getDbInfo();
        return new DataSourceConfig.Builder(dbInfo.getUrl(), dbInfo.getUsername(), dbInfo.getPassword());
    }

    protected GlobalConfig globalConfigBuilder(GlobalConfig.Builder builder) {
        String baseDirPath = this.projectInfo.getBaseDirPath();
        String author = this.projectInfo.getParameters().getAuthor();
        return builder.author(author)
                .outputDir(baseDirPath)
                .enableSwagger()
                .disableOpenDir()
                .build();
    }

    protected StrategyConfig strategyConfigBuilder(Function<String, String> scanner, StrategyConfig.Builder builder) {
        String configTables = this.projectInfo.getParameters().getTables();
        List<String> tables;
        if (StringUtils.isEmpty(configTables)) {
            tables = getTables(scanner.apply("请输入表名，多个英文逗号分隔，所有表请输入 all"));
        } else {
            tables = getTables(configTables);
        }

        String[] tablePrefix = this.projectInfo.getParameters().getTablePrefix();
        if (null == tablePrefix) {
            tablePrefix = new String[]{};
        }

        builder.addInclude(tables)
                .addTablePrefix(tablePrefix)
                .serviceBuilder().convertServiceFileName((entityName) -> entityName + "Service")
                .serviceTemplate(getTemplateDefault(TemplateFilesEnum.SERVICE)).serviceImplTemplate(getTemplateDefault(TemplateFilesEnum.SERVICE_IMPL))
                .superServiceClass(packageConfig.getSuperServiceClass()).superServiceImplClass(packageConfig.getSuperServiceImplClass())
                .entityBuilder().enableFileOverride().enableLombok().javaTemplate(getTemplateDefault(TemplateFilesEnum.ENTITY))
                .mapperBuilder().enableFileOverride().enableBaseColumnList().enableBaseResultMap().superClass(packageConfig.getSuperMapperClass())
                .mapperTemplate(getTemplateDefault(TemplateFilesEnum.MAPPER))
                .mapperXmlTemplate(getTemplateDefault(TemplateFilesEnum.XML));

        Controller.Builder controllerBuilder = builder.controllerBuilder()
                .enableRestStyle()
                .template(getTemplateDefault(TemplateFilesEnum.CONTROLLER))
                .superClass(packageConfig.getSuperControllerClass());

        // 不生成对应的controller
        if (!packageConfig.isEnableController()) {
            controllerBuilder.disable();
        }

        return builder.build();
    }

    protected String getTemplateFilePath(TemplateFilesEnum fileType) {
        TemplatePathEnum templatePathEnum = getTemplateBasePath();
        TemplateTypeEnum templateType = getTemplateType();
        return templatePathEnum.getPath() + fileType.getFileName() + StringUtil.DOT + templateType.getSuffix();
    }

    /**
     * 不需要后缀的文件名
     *
     * @param fileType
     * @return
     */
    protected String getTemplateDefault(TemplateFilesEnum fileType) {
        TemplatePathEnum templatePathEnum = getTemplateBasePath();
        return templatePathEnum.getPath() + fileType.getFileName();
    }

    private TemplateTypeEnum getTemplateType() {
        TemplateTypeEnum templateType = this.projectInfo.getParameters().getTemplateType();
        if (null == templateType) {
            // 默认使用beetl模版
            templateType = TemplateTypeEnum.beetl;
        }
        return templateType;
    }

    protected InjectionConfig injectionConfigBuilder(InjectionConfig.Builder builder) {
        // 自定义变量
        Map<String, Object> customMap = new HashMap<>();

        String managerPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.MANAGER, packageConfig.getManager());
        String managerImplPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.MANAGER, packageConfig.getManagerImpl());

        String commonPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.COMMON, packageConfig.getCommon());
        String facadePath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.FACADE, packageConfig.getFacade());

        customMap.put("multiModule", this.projectInfo.getParameters().isMultiModule());
        customMap.put("enableCrudCode", this.projectInfo.getParameters().isEnableCrudCode());
        customMap.put("parentPackage", packageConfig.getParent());
        customMap.put("commonPackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getCommon());
        customMap.put("managerPackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getManager());
        customMap.put("managerImplPackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getManagerImpl());
        customMap.put("facadePackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getFacade());
        customMap.put("isDefaultSuperMapper", packageConfig.isDefaultSuperMapper());
        customMap.put("commonResultClass", packageConfig.getCommonResultClass());
        customMap.put("commonResultClassName", getCommonResultClassName());

        List<CustomFile> list = new ArrayList<>();

        list.add(new CustomFile.Builder()
                .fileName(TemplateFilesEnum.DTO.getFileName())
                .templatePath(getTemplateFilePath(TemplateFilesEnum.DTO))
                .filePath(facadePath)
                .packageName("dto")
                .build());

        // manager层 Converter
        list.add(new CustomFile.Builder()
                .fileName(TemplateFilesEnum.CONVERTER.getFileName())
                .templatePath(getTemplateFilePath(TemplateFilesEnum.CONVERTER))
                .filePath(managerPath)
                .packageName("converter")
                .build());

        list.add(new CustomFile.Builder()
                // 通过格式化函数添加文件最后缀
//                    .formatNameFunction(tableInfo -> "Prefix" + tableInfo.getEntityName() + "Suffix")
                .fileName(StrUtil.upperFirst(TemplateFilesEnum.MANAGER.getFileName()))
                .templatePath(getTemplateFilePath(TemplateFilesEnum.MANAGER))
                .filePath(managerPath).build());
        list.add(new CustomFile.Builder()
                .fileName(StrUtil.upperFirst(TemplateFilesEnum.MANAGER_IMPL.getFileName()))
                .templatePath(getTemplateFilePath(TemplateFilesEnum.MANAGER_IMPL))
                .filePath(managerImplPath).build());

        return builder
                .beforeOutputFile((tableInfo, objectMap) -> {
                    // 首字母小写的实体名
                    objectMap.put("lowEntityName", StrUtil.lowerFirst(tableInfo.getEntityName()));
                    objectMap.put("lowMapperName", StrUtil.lowerFirst(tableInfo.getMapperName()));
                })
                .customMap(customMap)
                .customFile(list)
                .build();
    }

    /**
     * 获取 CommonResult 类名
     *
     * @return
     */
    private String getCommonResultClassName() {
        String[] split = packageConfig.getCommonResultClass().split("\\.");
        return split[split.length - 1];
    }

    protected PackageConfig packageConfigBuilder(PackageConfig.Builder builder) {
        Map<OutputFile, String> pathInfoMap = PathBuilderUtil.buildPaths(this.projectInfo, packageConfig);
        return builder
                .parent(packageConfig.getParent())
                .entity(packageConfig.getEntity())
                .mapper(packageConfig.getMapper())
                .service(packageConfig.getService())
                .serviceImpl(packageConfig.getServiceImpl())
                .controller(packageConfig.getController())
                .pathInfo(pathInfoMap)
                .build();
    }

    // 处理 all 情况
    protected List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

    /**
     * 按模板生成文件
     *
     * @param file
     * @param objectMap
     * @param templatePath
     */
    protected void outputFile(File file, Map<String, Object> objectMap, String templatePath) {
        log.info("Generate file -> path: {}", file.getPath());
        try {
            // 全局判断【默认】
            boolean exist = file.exists();
            if (!exist) {
                File parentFile = file.getParentFile();
                FileUtils.forceMkdir(parentFile);
            }
            getTemplateEngine().init(null).writer(objectMap, templatePath, file);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
