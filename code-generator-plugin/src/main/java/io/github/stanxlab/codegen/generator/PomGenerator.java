package io.github.stanxlab.codegen.generator;

import io.github.stanxlab.codegen.entity.ProjectInfo;
import io.github.stanxlab.codegen.enums.ModuleNameEnum;
import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import io.github.stanxlab.codegen.enums.TemplateFilesEnum;
import io.github.stanxlab.codegen.enums.TemplatePathEnum;
import io.github.stanxlab.codegen.manager.DependencyManager;
import io.github.stanxlab.codegen.util.PathBuilderUtil;
import io.github.stanxlab.codegen.util.PomPluginsMerger;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

@Slf4j
public class PomGenerator extends BaseGenerator {

    public PomGenerator(ProjectInfo projectInfo) {
        super(projectInfo, ORMTypeEnum.MYBATIS_PLUS);

    }

    public PomGenerator(ProjectInfo projectInfo, ORMTypeEnum ormType) {
        super(projectInfo, ormType);
    }

    @Override
    protected TemplatePathEnum getTemplateBasePath() {
        return TemplatePathEnum.BASE_DIR;
    }

    @Override
    public void execute() {
        boolean multiModule = this.projectInfo.getParameters().isMultiModule();

        // 生成各个模块的pom
        for (ModuleNameEnum moduleNameEnum : ModuleNameEnum.values()) {
            String curPomFile = PathBuilderUtil.buildPomPath(this.projectInfo, moduleNameEnum);
            if (ModuleNameEnum.PARENT.equals(moduleNameEnum)) {
                genParentPom(curPomFile);
                continue;
            }

            // 多模块时才存在其他pom
            if (multiModule) {
                outputPomFile(curPomFile, getContext(moduleNameEnum));
            }
        }
    }

    private void genParentPom(String parentPomFile) {
        boolean multiModule = this.projectInfo.getParameters().isMultiModule();
        boolean bakPomSuccess = copyBackupParentPomFile(parentPomFile);

        // 生成主pom
        Map<String, Object> context = getContext(ModuleNameEnum.PARENT);

        if (multiModule) {
            String[] allModuleNames = ModuleNameEnum.getAllModuleNames(this.projectInfo.getBaseProjectName());
            context.put("allModuleNames", Arrays.asList(allModuleNames));
        } else {
            context.put("allModuleNames", Collections.EMPTY_LIST);
        }

        context.put("isParent", true);
        context.put("artifactId", this.projectInfo.getBaseProjectName());

        outputPomFile(parentPomFile, context);

        if (bakPomSuccess) {
            mergeParentPom(parentPomFile);
        }
    }

    private static void mergeParentPom(String parentPomFile) {
        File bakFile = new File(getParentBackupPomName(parentPomFile)); // 构建备份文件名
        PomPluginsMerger.merge(bakFile, new File(parentPomFile));
    }

    private static String getParentBackupPomName(String parentPomFile) {
        return parentPomFile.replace(".xml", "-bak.xml");
    }

    /**
     * 备份原来的pom文件
     *
     * @param parentPomFile
     * @return
     */
    private boolean copyBackupParentPomFile(String parentPomFile) {
        File pomFile = new File(parentPomFile);
        if (!pomFile.exists()) {
            return false;
        }
        System.out.println("pom.xml exists.");
        try {
            File bakFile = new File(getParentBackupPomName(parentPomFile)); // 构建备份文件名

            // 重命名文件
            if (pomFile.renameTo(bakFile)) {
                System.out.println("pom.xml has been renamed to pom-bak.xml");
                return true;
            } else {
                System.out.println("Failed to rename pom.xml");
            }
        } catch (SecurityException e) {
            System.err.println("Security exception occurred while renaming the file: " + e.getMessage());
        }
        return false;
    }

    private Map<String, Object> getContext(ModuleNameEnum moduleNameEnum) {
        String moduleName = moduleNameEnum.getModuleName(this.projectInfo.getBaseProjectName());

        // 准备数据模型
        Map<String, Object> context = new HashMap<>();
        context.put("groupId", this.projectInfo.getParameters().getOutputPackage());
        context.put("artifactId", moduleName);
        context.put("isParent", false);
        context.put("multiModule", this.projectInfo.getParameters().isMultiModule());
        context.put("parentArtifactId", this.projectInfo.getBaseProjectName());

        context.put("isStartModule", moduleNameEnum.equals(ModuleNameEnum.WEB));
        context.put("revision", "${revision}");
        context.put("spring_version", "${spring-boot.version}");


        DependencyManager dependencyManager = new DependencyManager(this.projectInfo, this.ormType);
        List<Map<String, String>> dependenciesList;
        if (ModuleNameEnum.PARENT.equals(moduleNameEnum)) {
            dependenciesList = dependencyManager.getAllDependenciesList();
        } else {
            dependenciesList = dependencyManager.getModuleDependenciesList(moduleNameEnum);
        }

        // 将依赖项集合放入VelocityContext
        context.put("dependencies", dependenciesList);
        return context;
    }

    private void outputPomFile(String pomFile, Map<String, Object> objectMap) {
        File file = new File(pomFile);
        log.info("pomFile path: {}", file.getPath());
        if (!file.exists()) {
            log.info("Generate pom.xml -> path: {}", file.getPath());
            outputFile(file, objectMap, getTemplateFilePath(TemplateFilesEnum.POM));
        }
    }
}