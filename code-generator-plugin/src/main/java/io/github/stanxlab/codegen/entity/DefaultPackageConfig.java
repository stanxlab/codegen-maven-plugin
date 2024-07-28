package io.github.stanxlab.codegen.entity;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import io.github.stanxlab.codegen.enums.ORMTypeEnum;
import io.github.stanxlab.codegen.enums.TemplateFilesEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultPackageConfig {
    private String parent = "com.stanz.demo";
    private String daoModuleName = "dao";
    private String entity = "entity";
    private String mapper = "mapper";
    private String xml = "mapper.mysql";
    private String service = "service";
    private String serviceImpl = "service.impl";
    private String controller = "controller";
    private String common = "common";
    private String facade = "facade";
    private String manager = "manager";
    private String managerImpl = "manager.impl";

    /**
     * 自定义继承的Mapper类全称，带包名
     */
    private String superMapperClass;

    /**
     * 自定义继承的Service类全称，带包名
     */
    private String superServiceClass;

    /**
     * 自定义继承的ServiceImpl类全称，带包名
     */
    private String superServiceImplClass;

    /**
     * 自定义继承的Controller类全称，带包名
     */
    private String superControllerClass;

    /**
     * CommonResult类全称，带包名
     */
    private String commonResultClass;

    /**
     * 是否生成controller
     */
    private boolean enableController = true;

    /**
     * 是否生成service
     */
    private boolean enableService = true;

    /**
     * 是否生成manager
     */
    private boolean enableManager = true;

    /**
     * 是否使用默认的BaseMapper
     */
    private boolean isDefaultSuperMapper = false;

    public void init(ORMTypeEnum ormType) {
        if (StringUtils.isNotEmpty(this.daoModuleName) && !this.entity.contains(this.daoModuleName)) {
            this.entity = this.daoModuleName + "." + this.entity;
            this.mapper = this.daoModuleName + "." + this.mapper;
        }

        if (StringUtils.isEmpty(this.commonResultClass)) {
            this.commonResultClass = this.parent + "." + this.common + "." +
                    TemplateFilesEnum.COMMON_RESULT.getFileName().replace(".java", "");
        }

        switch (ormType) {
            case MYBATIS:
                initMybatis();
                break;
            case MYBATIS_PLUS:
                initMybatisPlus();
                break;
        }
    }

    private void initMybatisPlus() {
        if (StringUtils.isEmpty(this.superMapperClass)) {
            this.superMapperClass = ConstVal.SUPER_MAPPER_CLASS;
        }

        if (StringUtils.isEmpty(this.superServiceClass)) {
            this.superServiceClass = ConstVal.SUPER_SERVICE_CLASS;
        }

        if (StringUtils.isEmpty(this.superServiceImplClass)) {
            this.superServiceImplClass = ConstVal.SUPER_SERVICE_IMPL_CLASS;
        }
    }

    private void initMybatis() {
        if (StringUtils.isEmpty(this.superMapperClass)) {
            // default BaseMapper
//            this.superMapperClass = this.parent + "." + this.common + "." +
//                    TemplateFilesEnum.BASE_MAPPER.getFileName().split("\\.")[0];
            this.superMapperClass = "io.mybatis.mapper.BaseMapper";
            this.isDefaultSuperMapper = true;
        }
    }

}
