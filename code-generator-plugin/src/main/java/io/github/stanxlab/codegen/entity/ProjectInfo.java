package io.github.stanxlab.codegen.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.File;

/**
 * @author stanz
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class ProjectInfo {

    private Parameters parameters;

    private String baseProjectName;

    private String baseDirPath;

    private File baseDir;

    private File sourceDir;

    /**
     * 是否为初始化项目，即生成pom.xml, yaml 等文件
     */
    private boolean initialProject;

}
