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
import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import io.github.stanxlab.codegen.enums.TemplateFilesEnum;
import io.github.stanxlab.codegen.enums.TemplatePathEnum;
import io.github.stanxlab.codegen.enums.TemplateTypeEnum;
import io.github.stanxlab.codegen.util.CustomEntityNameConvert;
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
            // 在用户输入表名之前，显示数据库中的所有表名
            displayAllTableNames();
            tables = getTablesFromUserInput();
        } else {
            tables = getTables(configTables);
        }

        String[] tablePrefix = this.projectInfo.getParameters().getTablePrefix();
        if (null == tablePrefix) {
            tablePrefix = new String[]{};
        }

        String entitySuffix = packageConfig.getEntitySuffix();
        builder.addInclude(tables)
                .addTablePrefix(tablePrefix)
                .serviceBuilder()
                    .convertServiceFileName(entityName -> removeSuffix(entityName, entitySuffix) + "Service")
                    .convertServiceImplFileName(entityName -> removeSuffix(entityName, entitySuffix) + "ServiceImpl")
                    .serviceTemplate(getTemplateDefault(TemplateFilesEnum.SERVICE))
                    .serviceImplTemplate(getTemplateDefault(TemplateFilesEnum.SERVICE_IMPL))
                .controllerBuilder()
                    .convertFileName(entityName -> removeSuffix(entityName, entitySuffix) + "Controller")
                .mapperBuilder()
                    .convertMapperFileName(entityName -> removeSuffix(entityName, entitySuffix) + "Mapper")
                    .convertXmlFileName(entityName -> removeSuffix(entityName, entitySuffix) + "Mapper")
                    .enableFileOverride()
                    .enableBaseColumnList()
                    .enableBaseResultMap()
                    .superClass(packageConfig.getSuperMapperClass())
                    .mapperTemplate(getTemplateDefault(TemplateFilesEnum.MAPPER))
                    .mapperXmlTemplate(getTemplateDefault(TemplateFilesEnum.XML))
                .entityBuilder()
                    .nameConvert(new CustomEntityNameConvert(entitySuffix))
                    .enableFileOverride()
                    .enableLombok()
                    .javaTemplate(getTemplateDefault(TemplateFilesEnum.ENTITY))
                .build();

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

    /**
     * 显示数据库中的所有表名
     */
    private void displayAllTableNames() {
        try {
            DbInfo dbInfo = projectInfo.getParameters().getDbInfo();
            String url = dbInfo.getUrl();
            String username = dbInfo.getUsername();
            String password = dbInfo.getPassword();

            // 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 建立数据库连接
            try (java.sql.Connection connection = java.sql.DriverManager.getConnection(url, username, password)) {
                // 查询所有表名
                String sql = "SHOW TABLES";
                try (java.sql.PreparedStatement statement = connection.prepareStatement(sql);
                     java.sql.ResultSet resultSet = statement.executeQuery()) {
                    
                    System.out.println("\n==================== 数据库中的表名 ====================");
                    boolean hasTable = false;
                    while (resultSet.next()) {
                        String tableName = resultSet.getString(1);
                        System.out.println(tableName);
                        hasTable = true;
                    }
                    
                    if (!hasTable) {
                        System.out.println("数据库中没有表");
                    }
                    System.out.println("========================================================\n");
                }
            }
        } catch (Exception e) {
            log.warn("无法获取数据库表名: {}", e.getMessage());
            System.out.println("注意：无法获取数据库表名，请检查数据库连接配置");
        }
    }

    /**
     * 从用户输入获取表名，支持多行输入
     */
    private List<String> getTablesFromUserInput() {
        try {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            System.out.println("请输入表名：");
            System.out.println("  • 可以每行输入一个表名");
            System.out.println("  • 也可以用英文逗号分隔多个表名");
            System.out.println("  • 输入 'all' 生成所有表");
            System.out.println("  • 输入空行结束输入");
            System.out.print("> ");
            
            List<String> inputLines = new ArrayList<>();
            String line;
            
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    break;
                }
                inputLines.add(line);
                System.out.print("> ");
            }
            
            if (inputLines.isEmpty()) {
                System.out.println("没有输入任何表名，请重新输入");
                return getTablesFromUserInput();
            }
            
            // 处理输入的表名
            List<String> tableNames = new ArrayList<>();
            for (String inputLine : inputLines) {
                if ("all".equalsIgnoreCase(inputLine.trim())) {
                    // 如果任何一行是 "all"，则返回空列表（表示所有表）
                    return Collections.emptyList();
                }
                
                // 检查是否包含逗号分隔的表名
                if (inputLine.contains(",")) {
                    String[] tables = inputLine.split(",");
                    for (String table : tables) {
                        String trimmedTable = table.trim();
                        if (!trimmedTable.isEmpty()) {
                            tableNames.add(trimmedTable);
                        }
                    }
                } else {
                    // 单个表名
                    tableNames.add(inputLine.trim());
                }
            }
            
            return tableNames;
            
        } catch (Exception e) {
            log.error("读取用户输入时出现错误: {}", e.getMessage());
            System.out.println("输入错误，将生成所有表");
            return Collections.emptyList();
        }
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

        String managerPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig, packageConfig.getManager());
        String managerImplPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig, packageConfig.getManagerImpl());

        String commonPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig, packageConfig.getCommon());
        String dtoPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig, packageConfig.getDto());
        String converterPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig, packageConfig.getConverter());

        customMap.put("multiModule", false);
        customMap.put("enableCrudCode", this.projectInfo.getParameters().isEnableCrudCode());
        customMap.put("parentPackage", packageConfig.getParent());
        customMap.put("commonPackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getCommon());
        customMap.put("managerPackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getManager());
        customMap.put("managerImplPackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getManagerImpl());
        customMap.put("dtoPackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getDto());
        customMap.put("converterPackage", packageConfig.getParent() + StringUtil.DOT + packageConfig.getConverter());
        customMap.put("isDefaultSuperMapper", packageConfig.isDefaultSuperMapper());
        customMap.put("commonResultClass", packageConfig.getCommonResultClass());
        customMap.put("commonResultClassName", getCommonResultClassName());
        
        // 添加分页相关配置
        customMap.put("pageInfoClass", packageConfig.getPageInfoClass());
        customMap.put("pageInfoClassName", getSimpleClassName(packageConfig.getPageInfoClass()));
        customMap.put("pageRequestClass", packageConfig.getPageRequestClass());
        customMap.put("pageRequestClassName", getSimpleClassName(packageConfig.getPageRequestClass()));
        
        // 添加实体后缀配置
        customMap.put("entitySuffix", packageConfig.getEntitySuffix());
        
        // 添加 superService 相关的包名变量
        customMap.put("superServiceClass", getSimpleClassName(packageConfig.getSuperServiceClass()));
        customMap.put("superServiceClassPackage", packageConfig.getSuperServiceClass());
        customMap.put("superServiceImplClass", getSimpleClassName(packageConfig.getSuperServiceImplClass()));
        customMap.put("superServiceImplClassPackage", packageConfig.getSuperServiceImplClass());

        // 移除 customMap.put("originalEntityName", ...) 的 lambda，改为 beforeOutputFile 阶段注入

        List<CustomFile> list = new ArrayList<>();

        list.add(new CustomFile.Builder()
                // DTO 文件名：UserDTO.java (formatNameFunction返回"UserDTO", fileName是".java")
                .formatNameFunction(tableInfo -> removeSuffix(toCamelCaseUpperFirst(tableInfo.getName()), packageConfig.getEntitySuffix()) + "DTO")
                .fileName(".java")
                .templatePath(getTemplateFilePath(TemplateFilesEnum.DTO))
                .filePath(dtoPath)
                .build());

        // manager层 Converter 文件名：UserConverter.java (formatNameFunction返回"UserConverter", fileName是".java")
        list.add(new CustomFile.Builder()
                .formatNameFunction(tableInfo -> removeSuffix(toCamelCaseUpperFirst(tableInfo.getName()), packageConfig.getEntitySuffix()) + "Converter")
                .fileName(".java")
                .templatePath(getTemplateFilePath(TemplateFilesEnum.CONVERTER))
                .filePath(converterPath)
                .build());

        // manager 文件名：UserManager.java (formatNameFunction返回"UserManager", fileName是".java")
/**
        list.add(new CustomFile.Builder()
                .formatNameFunction(tableInfo -> removeSuffix(toCamelCaseUpperFirst(tableInfo.getName()), packageConfig.getEntitySuffix()) + "Manager")
                .fileName(".java")
                .templatePath(getTemplateFilePath(TemplateFilesEnum.MANAGER))
                .filePath(managerPath).build());
        // managerImpl 文件名：UserManagerImpl.java (formatNameFunction返回"UserManagerImpl", fileName是".java")
        list.add(new CustomFile.Builder()
                .formatNameFunction(tableInfo -> removeSuffix(toCamelCaseUpperFirst(tableInfo.getName()), packageConfig.getEntitySuffix()) + "ManagerImpl")
                .fileName(".java")
                .templatePath(getTemplateFilePath(TemplateFilesEnum.MANAGER_IMPL))
                .filePath(managerImplPath).build());
 */

        return builder
                .beforeOutputFile((tableInfo, objectMap) -> {
                    log.info("beforeOutputFile: tableInfo.getEntityName() = {}", tableInfo.getEntityName());
                    log.info("beforeOutputFile: tableInfo.getFields() = {}", tableInfo.getFields());
                    // 原始 entity 名（无后缀），供 DTO/Converter 命名，确保去除实体后缀
                    String originalEntityName = removeSuffix(toCamelCaseUpperFirst(tableInfo.getName()), packageConfig.getEntitySuffix());
                    // 首字母小写的实体名（基于原始名，不包含后缀）
                    objectMap.put("lowEntityName", StrUtil.lowerFirst(originalEntityName));
                    objectMap.put("lowMapperName", StrUtil.lowerFirst(tableInfo.getMapperName()));
                    objectMap.put("originalEntityName", originalEntityName);
                    // 添加不包含实体后缀的Mapper类名
                    objectMap.put("originalMapperName", originalEntityName + "Mapper");
                    // 添加不包含实体后缀的XML文件名（调试用）
                    objectMap.put("originalMapperXmlName", originalEntityName + "Mapper.xml");
                    
                    // 调试信息：打印关键变量
                    log.info("tableInfo.getName() = {}", tableInfo.getName());
                    log.info("tableInfo.getEntityName() = {}", tableInfo.getEntityName());
                    log.info("tableInfo.getMapperName() = {}", tableInfo.getMapperName());
                    log.info("originalEntityName = {}", originalEntityName);
                    log.info("originalMapperName = {}", originalEntityName + "Mapper");
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

    // 新增工具方法：去除 entitySuffix
    private String removeSuffix(String name, String suffix) {
        if (name != null && suffix != null && name.endsWith(suffix)) {
            return name.substring(0, name.length() - suffix.length());
        }
        return name;
    }

    // 新增工具方法：获取简单类名
    private String getSimpleClassName(String fullClassName) {
        if (fullClassName == null || fullClassName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fullClassName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return fullClassName;
        }
        return fullClassName.substring(lastDotIndex + 1);
    }
}
