package io.github.stanxlab.codegen.generator;

import com.alibaba.fastjson.JSON;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import io.github.stanxlab.codegen.enums.ModuleNameEnum;
import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import io.github.stanxlab.codegen.enums.TemplateFilesEnum;
import io.github.stanxlab.codegen.enums.TemplatePathEnum;
import io.github.stanxlab.codegen.util.PathBuilderUtil;
import io.github.stanxlab.codegen.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 生成其他文件： StartupApplication.java,
 * </p>
 */
@Slf4j
public class OtherFilesGenerator extends BaseGenerator {

    @Getter
    @Setter
    private ORMTypeEnum ormType;

    public OtherFilesGenerator(ProjectInfo projectInfo) {
        this(projectInfo, ORMTypeEnum.MYBATIS_PLUS);
    }

    public OtherFilesGenerator(ProjectInfo projectInfo, ORMTypeEnum ormType) {
        super(projectInfo);
        this.ormType = ormType;
    }

    @Override
    protected TemplatePathEnum getTemplateBasePath() {
        return TemplatePathEnum.OTHERS;
    }

    @Override
    public void execute() {
        String parentPackage = this.packageConfig.getParent();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("ormName", this.ormType.getOrmName());
        objectMap.put("baseProjectName", this.projectInfo.getBaseProjectName());
        objectMap.put("parentPackage", parentPackage);
        objectMap.put("mapperPackage", parentPackage + StringUtil.DOT + this.packageConfig.getMapper());

        outputStartupApplication(objectMap);
        outputYamlFile(objectMap);
        outputCommonResult(objectMap);
        outputGitKeep(objectMap);
    }

    private void outputGitKeep(Map<String, Object> objectMap) {
        File file = null;
        // test包子路径
        String testPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.TEST, "");

        TemplateFilesEnum filesEnum = TemplateFilesEnum.GIT_KEEP;
        file = new File(Paths.get(testPath, filesEnum.getFileName()).toString());
        outputFile(file, objectMap, getTemplateFilePath(filesEnum));


        // 创建web包的子路径
        String webPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.WEB, "");

        file = new File(Paths.get(webPath, "config", filesEnum.getFileName()).toString());
        outputFile(file, objectMap, getTemplateFilePath(filesEnum));

        file = new File(Paths.get(webPath, "interceptor", filesEnum.getFileName()).toString());
        outputFile(file, objectMap, getTemplateFilePath(filesEnum));

        file = new File(Paths.get(webPath, "exception", filesEnum.getFileName()).toString());
        outputFile(file, objectMap, getTemplateFilePath(filesEnum));

        // 创建common包的子路径
        String commonPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.COMMON, packageConfig.getCommon());
        file = new File(Paths.get(commonPath, "consts", filesEnum.getFileName()).toString());
        outputFile(file, objectMap, getTemplateFilePath(filesEnum));

        file = new File(Paths.get(commonPath, "enums", filesEnum.getFileName()).toString());
        outputFile(file, objectMap, getTemplateFilePath(filesEnum));

        file = new File(Paths.get(commonPath, "utils", filesEnum.getFileName()).toString());
        outputFile(file, objectMap, getTemplateFilePath(filesEnum));
    }

    private void outputCommonResult(Map<String, Object> objectMap) {
        String path = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.COMMON, packageConfig.getCommon() + File.separator);

        TemplateFilesEnum filesEnum = TemplateFilesEnum.COMMON_RESULT;
        File file = new File(path + filesEnum.getFileName());
        log.info("commonResult path: {}", file.getPath());
        if (!file.exists()) {
            log.info("Generate CommonResult.java");
            outputFile(file, objectMap, getTemplateFilePath(filesEnum));
        }
    }

    private void outputStartupApplication(Map<String, Object> objectMap) {
        // web模块根路径
        String webPath = PathBuilderUtil.buildPath(this.projectInfo, packageConfig,
                ModuleNameEnum.WEB, "");
        log.info("---->webPath: {}", webPath);
        TemplateFilesEnum filesEnum = TemplateFilesEnum.APPLICATION;
        File applicationFile = new File(Paths.get(webPath, filesEnum.getFileName()).toString());
        if (!applicationFile.exists()) {
            log.info("Generate StartupApplication.java");
            outputFile(applicationFile, objectMap, getTemplateFilePath(filesEnum));
        }
    }

    private void outputYamlFile(Map<String, Object> objectMap) {
        String parentPackage = this.packageConfig.getParent();

        // web 资源路径
        String webResourcePath = PathBuilderUtil.buildResourcePath(this.projectInfo, ModuleNameEnum.WEB, "");

        objectMap.put("entityPackage", parentPackage + StringUtil.DOT + this.packageConfig.getEntity());
        objectMap.put("xmlPath", PathBuilderUtil.replacePackageToPath(this.packageConfig.getXml()).replace("\\", "/"));

        log.info("---->webResourcePath: {}", webResourcePath);
        TemplateFilesEnum filesEnum = TemplateFilesEnum.APPLICATION_YAML;
        File yamlFile = new File(Paths.get(webResourcePath, filesEnum.getFileName()).toString());
        if (!yamlFile.exists()) {
            log.info("Generate application.yaml");
            objectMap.put("dbInfo", this.projectInfo.getParameters().getDbInfo());
            outputFile(yamlFile, objectMap, getTemplateFilePath(filesEnum));
        }
    }
}
