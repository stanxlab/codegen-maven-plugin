package io.github.stanxlab.codegen.mojo;

import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * mybatis-plus初始化项目
 */
@Mojo(name = "mybatis-plus-init")
public class MyBatisPlusInitMojo extends BaseGeneratorMojo {

    @Override
    protected ORMTypeEnum getORMType() {
        return ORMTypeEnum.MYBATIS_PLUS;
    }

    @Override
    protected boolean checkInitialProject() {
        // 初始化项目
        return true;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("生成mybatis-plus-init，项目初始化");
        super.execute();
    }
}
