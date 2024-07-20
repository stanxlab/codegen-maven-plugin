package io.github.stanxlab.codegen.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import io.github.stanxlab.codegen.entity.ProjectInfo;
import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 快速生成
 * </p>
 */
@Slf4j
public class DefaultAutoGenerator extends BaseGenerator {

    public DefaultAutoGenerator(ProjectInfo projectInfo) {
        // 默认生成 mybatis-plus的模板
        this(projectInfo, ORMTypeEnum.MYBATIS_PLUS);
    }

    public DefaultAutoGenerator(ProjectInfo projectInfo, ORMTypeEnum ormTypeEnum) {
        super(projectInfo, ormTypeEnum);
    }

    @Override
    public void execute() {
        FastAutoGenerator.create(dataSourceConfigBuilder())
                // 全局配置
                .globalConfig(this::globalConfigBuilder)
                // 包名配置
                .packageConfig(this::packageConfigBuilder)
                // 策略配置
                .strategyConfig(this::strategyConfigBuilder)
                // 自定义文件配置
                .injectionConfig(this::injectionConfigBuilder)
                .templateEngine(getTemplateEngine())
                .execute();
    }

}
