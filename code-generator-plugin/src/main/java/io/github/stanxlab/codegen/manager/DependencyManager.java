package io.github.stanxlab.codegen.manager;

import com.google.common.collect.Maps;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import io.github.stanxlab.codegen.enums.ModuleNameEnum;
import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class DependencyManager {

    private ProjectInfo projectInfo;
    private ORMTypeEnum ormType;

    public DependencyManager(ProjectInfo projectInfo, ORMTypeEnum ormType) {
        this.projectInfo = projectInfo;
        this.ormType = ormType;
    }

    /**
     * mybatis 相关依赖
     */
    public static final Map<ModuleNameEnum, String[][]> MYBATIS_DEPENDENCY_MAP = Maps.newHashMap();

    {
        MYBATIS_DEPENDENCY_MAP.put(ModuleNameEnum.COMMON, new String[][]{
                {"org.projectlombok", "lombok", "${lombok.version}"},
                {"io.swagger", "swagger-annotations", "${swagger-annotations.version}"},
                {"org.mapstruct", "mapstruct-processor", "${mapstruct.version}", "compile"},
                {"org.mapstruct", "mapstruct", "${mapstruct.version}", "compile"},
        });

        MYBATIS_DEPENDENCY_MAP.put(ModuleNameEnum.FACADE, new String[][]{
                {ModuleNameEnum.COMMON.name()},
                {"org.springframework.boot", "spring-boot-starter-validation", "${spring-boot.version}"},
                {"org.springframework.cloud", "spring-cloud-starter-openfeign", "${spring-cloud-starter-openfeign.version}"},
        });

        MYBATIS_DEPENDENCY_MAP.put(ModuleNameEnum.DAO, new String[][]{
                {ModuleNameEnum.FACADE.name()},
                {"io.mybatis", "mybatis-mapper", "${mybatis-mapper.version}"},
//                {"org.mybatis.spring.boot", "mybatis-spring-boot-starter", "${mybatis-spring-boot-starter.version}"},
                {"com.github.pagehelper", "pagehelper-spring-boot-starter", "${pagehelper-spring-boot-starter.version}"},
                {"mysql", "mysql-connector-java", "${mysql-connector.version}"},
        });

        MYBATIS_DEPENDENCY_MAP.put(ModuleNameEnum.MANAGER, new String[][]{
                {ModuleNameEnum.DAO.name()}
        });

        MYBATIS_DEPENDENCY_MAP.put(ModuleNameEnum.SERVICE, new String[][]{
                {ModuleNameEnum.MANAGER.name()}
        });

        MYBATIS_DEPENDENCY_MAP.put(ModuleNameEnum.WEB, new String[][]{
                {ModuleNameEnum.SERVICE.name()},
                {"org.springframework.boot", "spring-boot-starter-web", "${spring-boot.version}"},
                {"com.github.xiaoymin", "knife4j-openapi2-spring-boot-starter", "${knife4j-openapi2-spring-boot-starter}"},
                {"org.mybatis.spring.boot", "mybatis-spring-boot-starter", "${mybatis-spring-boot-starter.version}"},
        });

        MYBATIS_DEPENDENCY_MAP.put(ModuleNameEnum.TEST, new String[][]{
                {ModuleNameEnum.WEB.name()},
                {"org.springframework.boot", "spring-boot-starter-test", "${spring-boot.version}", "test"},
        });
    }

    /**
     * mybatis-plus 的必要依赖
     */
    public static final Map<ModuleNameEnum, String[][]> MP_DEPENDENCY_MAP = Maps.newHashMap();

    {
        MP_DEPENDENCY_MAP.put(ModuleNameEnum.COMMON, new String[][]{
                {"org.projectlombok", "lombok", "${lombok.version}"},
                {"io.swagger", "swagger-annotations", "${swagger-annotations.version}"},
                {"org.mapstruct", "mapstruct-processor", "${mapstruct.version}", "compile"},
                {"org.mapstruct", "mapstruct", "${mapstruct.version}", "compile"},
        });

        MP_DEPENDENCY_MAP.put(ModuleNameEnum.FACADE, new String[][]{
                {ModuleNameEnum.COMMON.name()},
                {"org.springframework.boot", "spring-boot-starter-validation", "${spring-boot.version}"},
                {"org.springframework.cloud", "spring-cloud-starter-openfeign", "${spring-cloud-starter-openfeign.version}"},
        });

        MP_DEPENDENCY_MAP.put(ModuleNameEnum.DAO, new String[][]{
                {ModuleNameEnum.FACADE.name()},
                {"com.baomidou", "mybatis-plus-boot-starter", "${mybatis-plus.version}"},
                {"mysql", "mysql-connector-java", "${mysql-connector.version}"},
        });

        MP_DEPENDENCY_MAP.put(ModuleNameEnum.MANAGER, new String[][]{
                {ModuleNameEnum.DAO.name()}
        });

        MP_DEPENDENCY_MAP.put(ModuleNameEnum.SERVICE, new String[][]{
                {ModuleNameEnum.MANAGER.name()}
        });

        MP_DEPENDENCY_MAP.put(ModuleNameEnum.WEB, new String[][]{
                {ModuleNameEnum.SERVICE.name()},
                {"org.springframework.boot", "spring-boot-starter-web", "${spring-boot.version}"},
                {"com.github.xiaoymin", "knife4j-openapi2-spring-boot-starter", "${knife4j-openapi2-spring-boot-starter}"},
                {"com.baomidou", "mybatis-plus-boot-starter", "${mybatis-plus.version}"},
        });

        MP_DEPENDENCY_MAP.put(ModuleNameEnum.TEST, new String[][]{
                {ModuleNameEnum.WEB.name()},
                {"org.springframework.boot", "spring-boot-starter-test", "${spring-boot.version}", "test"},
        });
    }

    /**
     * 获取所有依赖
     *
     * @return 所有依赖list
     */
    public List<Map<String, String>> getAllDependenciesList() {
        switch (this.ormType) {
            case MYBATIS:
                return getAllDependenciesList(MYBATIS_DEPENDENCY_MAP);
            case MYBATIS_PLUS:
                return getAllDependenciesList(MP_DEPENDENCY_MAP);
            default:
                return null;
        }
    }

    /**
     * 获取模块依赖
     *
     * @param moduleName
     * @return 模块的依赖list
     */
    public List<Map<String, String>> getModuleDependenciesList(ModuleNameEnum moduleName) {
        String[][] dependencyDefs = null;
        switch (this.ormType) {
            case MYBATIS:
                dependencyDefs = MYBATIS_DEPENDENCY_MAP.get(moduleName);
                break;
            case MYBATIS_PLUS:
                dependencyDefs = MP_DEPENDENCY_MAP.get(moduleName);
                break;
            default:
                return null;
        }
        return getDependencies(dependencyDefs);
    }

    private List<Map<String, String>> getAllDependenciesList(Map<ModuleNameEnum, String[][]> dependencyMap) {
        List<Map<String, String>> dependencies = new ArrayList<>();

        for (Map.Entry<ModuleNameEnum, String[][]> entry : dependencyMap.entrySet()) {
            String[][] dependencyDefs = entry.getValue();
            dependencies.addAll(getDependencies(dependencyDefs));
        }

        dependencies.sort(Comparator.comparing(o -> o.get("groupId")));

        return filterUniqueDependencies(dependencies);
    }

    private List<Map<String, String>> filterUniqueDependencies(List<Map<String, String>> dependencies) {
        // 使用HashSet来存储已存在的依赖组合（groupId+artifactId）
        Set<String> seen = new HashSet<>();

        // 创建一个新的List来存放去重后的依赖
        List<Map<String, String>> uniqueDependencies = new ArrayList<>();

        for (Map<String, String> dependency : dependencies) {
            // 创建一个唯一的标识符，用于判断是否重复
            String identifier = dependency.get("groupId") + ":" + dependency.get("artifactId");

            // 如果这个标识符还没有出现过，则添加到结果列表和seen集合中
            if (!seen.contains(identifier)) {
                seen.add(identifier);
                uniqueDependencies.add(dependency);
            }
        }

        return uniqueDependencies;
    }

    private List<Map<String, String>> getDependencies(String[][] dependencyDefs) {
        List<Map<String, String>> dependencies = new ArrayList<>();

        for (String[] def : dependencyDefs) {
            Map<String, String> dependency = new HashMap<>();
            if (def.length == 1) {
                // 内部依赖处理
                ModuleNameEnum moduleNameEnum = ModuleNameEnum.valueOf(def[0]);
                String depModuleName = moduleNameEnum.getModuleName(this.projectInfo.getBaseProjectName());
                dependency.put("groupId", this.projectInfo.getParameters().getOutputPackage());
                dependency.put("artifactId", depModuleName);
                dependency.put("version", "${revision}");
            } else {
                dependency.put("groupId", def[0]);
                dependency.put("artifactId", def[1]);

                // 版本号默认为空字符串，如果没有提供则不加入map中
                if (def.length >= 3 && StringUtils.isNotEmpty(def[2])) {
                    dependency.put("version", def[2]);
                }

                // scope也是可选的，只有当数组长度大于等于4且scope不为空时才加入
                if (def.length >= 4 && StringUtils.isNotEmpty(def[3])) {
                    dependency.put("scope", def[3]);
                }
            }
            dependencies.add(dependency);
        }

        return dependencies;
    }
}
