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
    private String entity = "entity";
    private String mapper = "mapper";
    private String xml = "mapper.mysql";
    private String service = "service";
    private String serviceImpl = "service.impl";
    private String controller = "controller";
    private String common = "common";
    private String manager = "manager";
    private String managerImpl = "manager.impl";
    private String dto = "dto";
    private String converter = "converter";

    /**
     * 实体后缀，默认为空
     */
    private String entitySuffix = "";

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
     * PageInfo类全称，带包名 (分页信息返回类型)
     */
    private String pageInfoClass;

    /**
     * PageRequest类全称，带包名 (分页请求参数类型)
     */
    private String pageRequestClass;

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
    private boolean enableManager = false;

    /**
     * 是否使用默认的BaseMapper
     */
    private boolean isDefaultSuperMapper = false;

    public void init(ORMTypeEnum ormType) {
        

        if (StringUtils.isEmpty(this.commonResultClass)) {
            this.commonResultClass = this.parent + "." + this.common + "." +
                    TemplateFilesEnum.COMMON_RESULT.getFileName().replace(".java", "");
        }

        // 初始化分页相关配置
        if (StringUtils.isEmpty(this.pageInfoClass)) {
            this.pageInfoClass = "com.github.pagehelper.PageInfo";
        }

        if (StringUtils.isEmpty(this.pageRequestClass)) {
            this.pageRequestClass = "com.github.pagehelper.PageRequest";
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
        
        if (StringUtils.isEmpty(this.superServiceClass)) {
            // 设置MyBatis的BaseService接口
            this.superServiceClass = this.parent + "." + this.common + ".BaseService";
        }
        
        if (StringUtils.isEmpty(this.superServiceImplClass)) {
            // 设置MyBatis的BaseServiceImpl类
            this.superServiceImplClass = this.parent + "." + this.common + ".BaseServiceImpl";
        }
    }

}
