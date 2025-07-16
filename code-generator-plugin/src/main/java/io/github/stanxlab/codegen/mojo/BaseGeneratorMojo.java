package io.github.stanxlab.codegen.mojo;

import io.github.stanxlab.codegen.entity.Parameters;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import io.github.stanxlab.codegen.enums.ModuleNameEnum;
import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import io.github.stanxlab.codegen.generator.DefaultAutoGenerator;
import io.github.stanxlab.codegen.generator.OtherFilesGenerator;
import io.github.stanxlab.codegen.generator.PomGenerator;
import lombok.Getter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Optional;

@Getter
public abstract class BaseGeneratorMojo extends AbstractMojo {

    @Parameter
    private Parameters parameters;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(defaultValue = "${basedir}")
    private File baseDir;

    @Parameter(defaultValue = "${project.build.sourceDirectory}")
    private File sourceDir;

    @Parameter(defaultValue = "${project.artifactId}")
    private String currentArtifactId;

    /**
     * 获取ORM类型
     *
     * @return ORM类型
     */
    protected abstract ORMTypeEnum getORMType();

    protected boolean checkInitialProject() {
        return false;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("execute1 --> currentArtifactId: " + currentArtifactId);
        try {
            boolean initialProject = checkInitialProject();
            this.parameters = Optional.ofNullable(this.parameters).orElse(new Parameters());
            
            // 从Maven项目信息中获取默认值
            if (this.parameters.getOutputPackage() == null) {
                // 如果没有配置outputPackage或使用默认值，则使用项目的groupId
                String groupId = this.project.getGroupId();
                if (groupId != null && !groupId.isEmpty()) {
                    this.parameters.setOutputPackage(groupId);
                    getLog().info("自动设置 outputPackage 为: " + groupId);
                }
            }
            
            if (this.parameters.getBaseProjectName() == null) {
                // 如果没有配置baseProjectName或使用默认值，则使用项目的artifactId
                String artifactId = this.project.getArtifactId();
                if (artifactId != null && !artifactId.isEmpty()) {
                    this.parameters.setBaseProjectName(artifactId);
                    getLog().info("自动设置 baseProjectName 为: " + artifactId);
                }
            }

            ProjectInfo projectInfo = new ProjectInfo();
            projectInfo.setParameters(this.parameters);
            projectInfo.setBaseProjectName(this.parameters.getBaseProjectName());
            projectInfo.setBaseDir(this.baseDir);
            projectInfo.setBaseDirPath(this.baseDir.getAbsolutePath());
            projectInfo.setSourceDir(this.sourceDir);
            projectInfo.setInitialProject(initialProject);

            new DefaultAutoGenerator(projectInfo, getORMType()).execute();

            if (initialProject) {
                // 初始化项目，生成其他文件
                new PomGenerator(projectInfo, getORMType()).execute();
                new OtherFilesGenerator(projectInfo, getORMType()).execute();
            }
        } catch (Exception e) {
            getLog().error(e);
            throw new RuntimeException(e);
        }
    }

    
}
