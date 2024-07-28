package io.github.stanxlab.codegen.generator;

import io.github.stanxlab.codegen.entity.DefaultPackageConfig;
import io.github.stanxlab.codegen.entity.Parameters;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

@Slf4j
class AutoGeneratorMybatisPlusTest extends BaseTest {

    @Test
    void testExecute_InitProject() {
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path);
        new DefaultAutoGenerator(projectInfo).execute();
        // 生成pom文件
        new PomGenerator(projectInfo).execute();
        new OtherFilesGenerator(projectInfo).execute();
    }

    @Test
    void testExecute_InitProject_Custom_Package() {
        DefaultPackageConfig packageConfig = new DefaultPackageConfig();
        packageConfig.setEnableController(false);
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path, true, packageConfig);
        new DefaultAutoGenerator(projectInfo).execute();
        // 生成pom文件
        new PomGenerator(projectInfo).execute();
        new OtherFilesGenerator(projectInfo).execute();
    }

    @Test
    void testExecute_InitProject_Disable_MultiModule() {
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path, false);
        new DefaultAutoGenerator(projectInfo).execute();
        // 生成pom文件
        new PomGenerator(projectInfo).execute();
        new OtherFilesGenerator(projectInfo).execute();
    }

    @Test
    void testExecute_PomFile() {
        Path path = getFixedPath();
        ProjectInfo projectInfo = getProjectInfo(path);
        // 生成pom文件
        new PomGenerator(projectInfo).execute();
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
        new DefaultAutoGenerator(projectInfo).execute();
    }

    @Test
    void testExecute_DefaultFiles_Disable_MultiModule() {
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path, false);
        new DefaultAutoGenerator(projectInfo).execute();
    }

    protected ProjectInfo getProjectInfo(Path path) {
        return getProjectInfo(path, true);
    }

    protected ProjectInfo getProjectInfo(Path path, boolean multiModule) {
        return getProjectInfo(path, true, new DefaultPackageConfig());
    }

    protected ProjectInfo getProjectInfo(Path path, boolean multiModule, DefaultPackageConfig packageConfig) {
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
                        .packageConfig(packageConfig)
//                        .templateType(TemplateTypeEnum.beetl)
                        .multiModule(multiModule)
                        .build())
                .build();
        return projectInfo;
    }
}