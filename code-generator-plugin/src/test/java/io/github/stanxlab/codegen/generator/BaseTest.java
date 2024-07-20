package io.github.stanxlab.codegen.generator;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.github.stanxlab.codegen.entity.DbInfo;
import io.github.stanxlab.codegen.entity.DefaultPackageConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public abstract class BaseTest {

    protected DbInfo dbInfo;

    /**
     * 模拟mojo传入的 ${project.baseDir}参数
     */
    protected File baseDir;

    @BeforeEach
    void setUp() {
        String mysqlUrl = System.getenv("TEST_MYSQL_URL");
        String username = System.getenv("TEST_MYSQL_USER");
        String password = System.getenv("TEST_MYSQL_PASSWORD");
        log.info("mysqlUrl: {}, username: {}, password: {}", mysqlUrl, username, password);

        if (StringUtils.isEmpty(mysqlUrl)) {
            mysqlUrl = "jdbc:mysql://192.168.2.229:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
            username = "test";
            password = "a123456";
        }
        dbInfo = new DbInfo(mysqlUrl, username, password);

        String basedirPath = System.getProperty("basedir");
        baseDir = new File(basedirPath);
    }


    @Test
    void testConfig() {
        DefaultPackageConfig defaultPackageConfig = new DefaultPackageConfig();
        System.out.println(defaultPackageConfig);
        assertEquals("dao", defaultPackageConfig.getDaoModuleName());
    }

    @Test
    void test() {
        String username = System.getenv("user.name");
        System.out.println(username);
        String basedirPath = System.getProperty("basedir");
        System.out.println(basedirPath);

        System.out.println(baseDir.getParentFile().getAbsolutePath());
    }

    protected Path getRandomPath() {
        // 创建临时目录，用于测试
        String tmpSubPath = "tmp-" + DateUtil.format(new DateTime(), "HHmmss");
        String parentBasePath = baseDir.getParentFile().getAbsolutePath();
        return Paths.get(parentBasePath, tmpSubPath);
    }

    protected Path getFixedPath() {
        String tmpSubPath = "tmp_11";
        String parentBasePath = baseDir.getParentFile().getAbsolutePath();
        return Paths.get(parentBasePath, tmpSubPath);
    }
}
