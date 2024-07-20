package io.github.stanxlab.codegen.generator;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.github.stanxlab.codegen.entity.DbInfo;
import io.github.stanxlab.codegen.entity.DefaultPackageConfig;
import io.github.stanxlab.codegen.entity.Parameters;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
class AutoGeneratorMybatisPlusTest extends BaseTest {

    @Test
    void testConfig() {
        DefaultPackageConfig defaultPackageConfig = new DefaultPackageConfig();
        System.out.println(defaultPackageConfig);
    }

    @Test
    void test() {
        String username = System.getenv("user.name");
        System.out.println(username);
        String basedirPath = System.getProperty("basedir");
        System.out.println(basedirPath);

        System.out.println(baseDir.getParentFile().getAbsolutePath());
    }

    @Test
    void testExecute_InitProject() {
        // 创建临时目录，用于测试
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path);
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
        // 创建临时目录，用于测试
        Path path = getFixedPath();

        ProjectInfo projectInfo = getProjectInfo(path);
        new OtherFilesGenerator(projectInfo).execute();
    }

    @Test
    void testExecute_DefaultFiles() {
        // 创建临时目录，用于测试
        Path path = getFixedPath();

        ProjectInfo projectInfo = getProjectInfo(path);
        new DefaultAutoGenerator(projectInfo).execute();
    }

    @Test
    void testExecute_DefaultFiles_Disable_MultiModule() {
        // 创建临时目录，用于测试
        Path path = getRandomPath();
        ProjectInfo projectInfo = getProjectInfo(path, false);
        new DefaultAutoGenerator(projectInfo).execute();
    }

    protected ProjectInfo getProjectInfo(Path path) {
        return getProjectInfo(path, false);
    }

    protected ProjectInfo getProjectInfo(Path path, boolean multiModule) {
        ProjectInfo projectInfo = ProjectInfo.builder()
                .baseDirPath(path.toString())
                .baseDir(baseDir)
                .baseProjectName("ecsm")
                .initialProject(true)
                .parameters(Parameters.builder()
                        .author("stanxlab")
                        .tables("all")
                        .outputPackage("com.stanxlab.ecsm")
                        .dbInfo(dbInfo)
                        .multiModule(multiModule)
                        .build())
                .build();
        return projectInfo;
    }
}