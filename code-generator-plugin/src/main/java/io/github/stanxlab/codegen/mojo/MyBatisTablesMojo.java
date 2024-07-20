package io.github.stanxlab.codegen.mojo;

import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "mybatis:tables")
public class MyBatisTablesMojo extends BaseGeneratorMojo {

    @Override
    protected ORMTypeEnum getORMType() {
        return ORMTypeEnum.MYBATIS;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("生成mybatis");
        super.execute();
    }
}
