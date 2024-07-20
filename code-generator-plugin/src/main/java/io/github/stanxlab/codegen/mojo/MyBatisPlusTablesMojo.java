package io.github.stanxlab.codegen.mojo;

import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * mybatis-plus 表实体类生成
 */
@Mojo(name = "mybatis-plus:tables")
public class MyBatisPlusTablesMojo extends BaseGeneratorMojo {

    @Override
    protected ORMTypeEnum getORMType() {
        return ORMTypeEnum.MYBATIS_PLUS;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("生成mybatis-plus");
        super.execute();
    }
}
