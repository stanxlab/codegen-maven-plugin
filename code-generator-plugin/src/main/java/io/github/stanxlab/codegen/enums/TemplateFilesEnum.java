package io.github.stanxlab.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateFilesEnum {
    SERVICE_IMPL("serviceImpl.java"),
    SERVICE("service.java"),
    MAPPER("mapper.java"),
    ENTITY("entity.java"),
    XML("mapper.xml"),
    CONTROLLER("controller.java"),
    MANAGER("manager.java"),
    MANAGER_IMPL("managerImpl.java"),
    VO("VO.java"),
    DTO("DTO.java"),
    CONVERTER("Converter.java"),
    PACKAGE_INFO("package-info.java"),
    POM("pom.xml"),
    GIT_KEEP(".gitkeep"),
    APPLICATION("StartupApplication.java"),
    APPLICATION_YAML("application.yaml"),
    COMMON_RESULT("CommonResult.java"),
    BASE_MAPPER("BaseMapper.java"),

    ;

    private final String fileName;

}
