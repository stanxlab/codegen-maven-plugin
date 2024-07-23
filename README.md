# codegen-maven-plugin

代码生成器maven插件，用于初始化多模块spring项目，以及生成模版代码，支持mybatis和mybtis-plus。
同时支持普通非多模块项目。

## 使用说明

### 初始化mybatis项目

1. 新建maven项目，添加插件依赖

`pom.xml`如下:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>mybatis-example</artifactId>
    <version>1.0-SNAPSHOT</version>

    <build>
        <plugins>
            <plugin>
                <groupId>io.github.stanxlab</groupId>
                <artifactId>codegen</artifactId>
                <version>1.0.3</version>
                <configuration>
                    <parameters>
                        <!--  项目基础包名  -->
                        <outputPackage>com.stanxlab.demo</outputPackage>
                        <!--  项目基础名称，生成模块名前缀使用  -->
                        <baseProjectName>xxxlab</baseProjectName>
                        <!--  配置表名，all表示所有，不配置则在执行时输入  -->
                        <tables>all</tables>
                        <packageConfig>
                            <!--  自定义Mapper的基类  -->
                            <!--  <superMapperClass>com.stanxlab.mybatis.mapper.BaseMapper</superMapperClass>-->
                        </packageConfig>
                        <!--  配置表前缀，生成的实体类名会剔除前缀  -->
                        <!-- <tablePrefix>cms_</tablePrefix>-->
                        <dbInfo>
                            <!--  数据库连接信息  -->
                            <url>jdbc:mysql://192.168.2.229:3306/test?useUnicode=true</url>
                            <username>test</username>
                            <password>a123456</password>
                        </dbInfo>
                    </parameters>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

2. 生成多模块项目

![usage-init-project](doc/usage-init-project.gif)

### 生成dao层代码

点击插件 `mybatis:tables` 命令。
