package io.github.stanxlab.codegen.util;

import com.baomidou.mybatisplus.generator.config.OutputFile;
import io.github.stanxlab.codegen.consts.CommonConsts;
import io.github.stanxlab.codegen.entity.DefaultPackageConfig;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import io.github.stanxlab.codegen.enums.ModuleNameEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PathBuilderUtil {

    public static Map<OutputFile, String> buildPaths(ProjectInfo projectInfo, DefaultPackageConfig packageConfig) {
        boolean multiModule = projectInfo.getParameters().isMultiModule();
        String baseDirPath = projectInfo.getBaseDirPath();
        Map<ModuleNameEnum, String> moduleNamesMap = multiModule ? ModuleNameEnum.getAllModuleNamesMap(
                projectInfo.getBaseProjectName()) : new HashMap<>();

        Map<OutputFile, String> pathInfo = new HashMap<>();
        String daoBase = buildPath(baseDirPath, moduleNamesMap.getOrDefault(ModuleNameEnum.DAO, ""));
        String entityPath = buildPath(daoBase, CommonConsts.JAVA_SRC_DIR, packageConfig.getParent(), packageConfig.getEntity());
        String mapperPath = buildPath(daoBase, CommonConsts.JAVA_SRC_DIR, packageConfig.getParent(), packageConfig.getMapper());
        String servicePath = buildPath(baseDirPath, moduleNamesMap.getOrDefault(ModuleNameEnum.SERVICE, ""), CommonConsts.JAVA_SRC_DIR,
                packageConfig.getParent(), packageConfig.getService());
        String serviceImplPath = buildPath(baseDirPath, moduleNamesMap.getOrDefault(ModuleNameEnum.SERVICE, ""), CommonConsts.JAVA_SRC_DIR,
                packageConfig.getParent(), packageConfig.getServiceImpl());
        String controllerPath = buildPath(baseDirPath, moduleNamesMap.getOrDefault(ModuleNameEnum.WEB, ""), CommonConsts.JAVA_SRC_DIR,
                packageConfig.getParent(), packageConfig.getController());

        pathInfo.put(OutputFile.entity, entityPath);
        pathInfo.put(OutputFile.mapper, mapperPath);
        pathInfo.put(OutputFile.service, servicePath);
        pathInfo.put(OutputFile.serviceImpl, serviceImplPath);
        pathInfo.put(OutputFile.controller, controllerPath);
        // 设置 Mapper XML 文件生成路径
        pathInfo.put(OutputFile.xml, buildPath(daoBase, CommonConsts.SRC_RESOURCE, packageConfig.getXml()));

        return pathInfo;
    }

    public static String buildPath(ProjectInfo projectInfo, DefaultPackageConfig packageConfig,
                                   ModuleNameEnum moduleNameEnum, String subPackage) {
        boolean multiModule = projectInfo.getParameters().isMultiModule();
        String baseDirPath = projectInfo.getBaseDirPath();
        String baseProjectName = projectInfo.getBaseProjectName();
        return buildPath(baseDirPath, multiModule ? moduleNameEnum.getModuleName(baseProjectName) : "", CommonConsts.JAVA_SRC_DIR,
                packageConfig.getParent(), subPackage);
    }

    /**
     * 生成资源路径
     *
     * @param projectInfo
     * @param moduleNameEnum
     * @return 资源路径
     */
    public static String buildResourcePath(ProjectInfo projectInfo, ModuleNameEnum moduleNameEnum, String subPackage) {
        boolean multiModule = projectInfo.getParameters().isMultiModule();
        String baseDirPath = projectInfo.getBaseDirPath();
        String baseProjectName = projectInfo.getBaseProjectName();
        return buildPath(baseDirPath, multiModule ? moduleNameEnum.getModuleName(baseProjectName) : "", CommonConsts.SRC_RESOURCE, subPackage);
    }

    public static String buildPomPath(ProjectInfo projectInfo, ModuleNameEnum moduleNameEnum) {
        boolean multiModule = projectInfo.getParameters().isMultiModule();
        String baseDirPath = projectInfo.getBaseDirPath();
        String baseProjectName = projectInfo.getBaseProjectName();
        return buildPath(baseDirPath, multiModule ? moduleNameEnum.getModuleName(baseProjectName) : "") + "/pom.xml";
    }
//
//    public static String buildManagerPath(ProjectInfo projectInfo, DefaultPackageConfig packageConfig) {
//        String baseDirPath = projectInfo.getBaseDirPath();
//        String baseProjectName = projectInfo.getBaseProjectName();
//        return buildPath(baseDirPath, ModuleNameEnum.getManagerModuleName(baseProjectName), CommonConsts.JAVA_SRC_DIR,
//                packageConfig.getParent(), packageConfig.getManager());
//    }
//
//    public static String buildManagerImplPath(ProjectInfo projectInfo, DefaultPackageConfig packageConfig) {
//        String baseDirPath = projectInfo.getBaseDirPath();
//        String baseProjectName = projectInfo.getBaseProjectName();
//        return buildPath(baseDirPath, ModuleNameEnum.getManagerModuleName(baseProjectName), CommonConsts.JAVA_SRC_DIR,
//                packageConfig.getParent(), packageConfig.getManagerImpl());
//    }

    public static String buildPath(String baseDir, String... subDirs) {
        StringBuilder pathBuilder = new StringBuilder(baseDir);
        for (String subDir : subDirs) {
            if (StringUtils.isEmpty(subDir)) {
                continue;
            }
            pathBuilder.append(File.separator).append(replacePackageToPath(subDir));
        }
        return pathBuilder.toString();
    }

    public static String replacePackageToPath(String subDir) {
        if (subDir == null || !subDir.contains(".")) {
            return subDir; // 如果字符串为空或不含'.'，直接返回原字符串
        }

        String result = null;
        try {
//            result = subDir.replaceAll( "\\.", File.separator);
            String[] parts = subDir.split("\\.");
            result = String.join(File.separator, parts);
        } catch (Exception e) {
            log.error("subDir: {}", subDir);
            e.printStackTrace();
        }
        return result;
    }
}
