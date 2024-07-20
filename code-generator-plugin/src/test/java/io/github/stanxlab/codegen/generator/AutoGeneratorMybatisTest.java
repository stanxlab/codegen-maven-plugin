package io.github.stanxlab.codegen.generator;

import com.baomidou.mybatisplus.generator.config.OutputFile;
import io.github.stanxlab.codegen.entity.DefaultPackageConfig;
import io.github.stanxlab.codegen.entity.Parameters;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import io.github.stanxlab.codegen.util.PathBuilderUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
class AutoGeneratorMybatisTest extends BaseTest {

    private final static ORMTypeEnum ormType = ORMTypeEnum.MYBATIS;

    @Test
    void testExecute_InitProject() {
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path);
        new DefaultAutoGenerator(projectInfo, ormType).execute();
        // 生成pom文件
        new PomGenerator(projectInfo, ormType).execute();
        new OtherFilesGenerator(projectInfo, ormType).execute();
    }

    @Test
    void testExecute_InitProject_Disable_MultiModule() {
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path, false);
        new DefaultAutoGenerator(projectInfo, ormType).execute();
        // 生成pom文件
        new PomGenerator(projectInfo, ormType).execute();
        new OtherFilesGenerator(projectInfo, ormType).execute();
    }

    @Test
    void testExecute_PomFile() {
        Path path = getFixedPath();
        ProjectInfo projectInfo = getProjectInfo(path);
        // 生成pom文件
        new PomGenerator(projectInfo, ormType).execute();
    }

    @Test
    void testExecute_OtherFiles() {
        Path path = getFixedPath();
        ProjectInfo projectInfo = getProjectInfo(path);
        new OtherFilesGenerator(projectInfo).execute();
    }

    @Test
    void testExecute_DefaultFiles() {
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path);
        new DefaultAutoGenerator(projectInfo, ormType).execute();
    }

    @Test
    void testExecute_DefaultFiles_Disable_MultiModule() {
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path, false);
        new DefaultAutoGenerator(projectInfo, ormType).execute();
    }

    @Test
    void test_buildPaths_Disable_MultiModule() {
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path, false);
        Map<OutputFile, String> pathMap = PathBuilderUtil.buildPaths(projectInfo, new DefaultPackageConfig());
        System.out.println(pathMap);
    }

    protected ProjectInfo getProjectInfo(Path path) {
        return getProjectInfo(path, true);
    }

    protected ProjectInfo getProjectInfo(Path path, boolean multiModule) {
        DefaultPackageConfig defaultPackageConfig = new DefaultPackageConfig();
//        defaultPackageConfig.setSuperMapperClass("com.xxx.mybatis.mapper.BaseMapper");

        ProjectInfo projectInfo = ProjectInfo.builder()
                .baseDirPath(path.toString())
                .baseDir(baseDir)
                .baseProjectName("demo")
                .initialProject(true)
                .parameters(Parameters.builder()
                        .author("stanxlab")
                        .tables("all")
                        .outputPackage("com.stanxlab.demo")
                        .dbInfo(dbInfo)
                        .multiModule(multiModule) // 是否需要使用多模块
//                        .templateType(TemplateTypeEnum.beetl) // 目前只支持beetl模板引擎
                        .packageConfig(defaultPackageConfig)
                        .build())
                .build();
        return projectInfo;
    }
}