package com.cweeyii.generator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class ExecuteGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteGenerator.class);

    private boolean overwrite = true;
    private List<String> warnings = Lists.newArrayList();
    private Class pageClass;
    private String databaseUrl, databaseUsername, databasePassword;
    private String javaCodeBasePath, resourcesBasePath;
    private String domainPackage, mapperPackage, mapperXmlFolder;

    public void invoke() throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        Configuration configuration = createConfigurationParser();
        setConnectionConfig(configuration);
        setPageClass(configuration);
        setDomainConfig(configuration);
        setMapperConfig(configuration);
        setMapperXmlConfig(configuration);
        if (overwrite) {
            clearOriginMapperXml(configuration);
        }
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);
        myBatisGenerator.generate(null);
    }

    private Configuration createConfigurationParser() throws IOException, XMLParserException {
        URL generatorConfig = this.getClass().getClassLoader().getResource("generatorConfig.xml");
        Preconditions.checkNotNull(generatorConfig, "无法找到generator的配置文件,默认使用resources下的generatorConfig.xml文件");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        return cp.parseConfiguration(new File(generatorConfig.getFile()));
    }

    private void setConnectionConfig(Configuration configuration) {
        for (Context context : configuration.getContexts()) {
            JDBCConnectionConfiguration jdbcConfig = context.getJdbcConnectionConfiguration();
            jdbcConfig.setDriverClass("com.mysql.jdbc.Driver");
            if (StringUtils.isNotEmpty(databaseUrl)) {
                jdbcConfig.setConnectionURL(databaseUrl);
            }
            if (StringUtils.isNotEmpty(databaseUsername)) {
                jdbcConfig.setUserId(databaseUsername);
            }
            if (StringUtils.isNotEmpty(databasePassword)) {
                jdbcConfig.setPassword(databasePassword);
            }
        }
    }

    private void setPageClass(Configuration configuration) {
        for (Context context : configuration.getContexts()) {
            try {
                java.lang.reflect.Field pluginConfigsField = Context.class.getDeclaredField("pluginConfigurations");
                pluginConfigsField.setAccessible(true);
                List<PluginConfiguration> pluginConfigs = (List<PluginConfiguration>) pluginConfigsField.get(context);
                for (PluginConfiguration pluginConfig : pluginConfigs) {
                    if (PageablePlugin.class.isAssignableFrom(Class.forName(pluginConfig.getConfigurationType()))) {
                        if (pageClass != null) {
                            pluginConfig.getProperties().setProperty(PageablePlugin.PAGE_CLASS_PROPERTY_NAME, pageClass.getName());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setDomainConfig(Configuration configuration) {
        for (Context context : configuration.getContexts()) {
            JavaModelGeneratorConfiguration javaModelConfig = context.getJavaModelGeneratorConfiguration();
            if (StringUtils.isNotEmpty(javaCodeBasePath)) {
                javaModelConfig.setTargetProject(javaCodeBasePath);
            }
            if (StringUtils.isNotEmpty(domainPackage)) {
                javaModelConfig.setTargetPackage(domainPackage);
            }
        }
    }

    private void setMapperXmlConfig(Configuration configuration) {
        for (Context context : configuration.getContexts()) {
            SqlMapGeneratorConfiguration mapperXmlConfig = context.getSqlMapGeneratorConfiguration();
            if (StringUtils.isNotEmpty(resourcesBasePath)) {
                mapperXmlConfig.setTargetProject(resourcesBasePath);
            }
            if (StringUtils.isNotEmpty(mapperXmlFolder)) {
                mapperXmlConfig.setTargetPackage(mapperXmlFolder);
            }
        }
    }

    private void setMapperConfig(Configuration configuration) {
        for (Context context : configuration.getContexts()) {
            JavaClientGeneratorConfiguration mapperConfig = context.getJavaClientGeneratorConfiguration();
            if (StringUtils.isNotEmpty(javaCodeBasePath)) {
                mapperConfig.setTargetProject(javaCodeBasePath);
            }
            if (StringUtils.isNotEmpty(mapperPackage)) {
                mapperConfig.setTargetPackage(mapperPackage);
            }
        }
    }

    /**
     * 清除原来的mapper xml文件
     * 为了避免出现Result Maps collection already contains value异常,因为这里的override参数只会覆盖java代码,xml代码会实现追加操作,导致内容重复
     */
    private void clearOriginMapperXml(Configuration configuration) {
        for (Context context : configuration.getContexts()) {
            SqlMapGeneratorConfiguration mapperXmlConfig = context.getSqlMapGeneratorConfiguration();
            File mapperFolder = new File(mapperXmlConfig.getTargetProject(), mapperXmlConfig.getTargetPackage());
            if (mapperFolder.exists() && mapperFolder.isDirectory()) {
                List<File> mapperXmls = Lists.newArrayList(Iterables.filter(Lists.newArrayList(mapperFolder.listFiles()), new Predicate<File>() {
                    public boolean apply(File input) {
                        return input.isFile() && input.getName().endsWith(".xml");
                    }
                }));
                LOGGER.info("清除原来存在的mapper xml文件,文件有:{}", Lists.newArrayList(Iterables.transform(mapperXmls, new Function<File, String>() {
                    public String apply(File input) {
                        return input.getAbsolutePath();
                    }
                })));
                List<File> failDeleteXml = Lists.newArrayList(Iterables.filter(mapperXmls, new Predicate<File>() {
                    public boolean apply(File input) {
                        return ! input.delete();
                    }
                }));
                Preconditions.checkState(CollectionUtils.isEmpty(failDeleteXml), MessageFormatter.format("有部份xml文件清楚失败,请检查,失败文件:[{}]", Lists.newArrayList(Iterables.transform(failDeleteXml, new Function<File, String>() {
                    public String apply(File input) {
                        return input.getAbsolutePath();
                    }
                }))).getMessage());
            } else {
                LOGGER.error("无法清除原来存在的mapper xml文件,因为路径[{}]不存在或不是一个文件夹", mapperFolder.getAbsoluteFile());
            }
        }
    }

    /**
     * 如果想将override设为false需要谨慎操作
     * 因为mapper xml文件总是以追加的形式重新生成的,有可能导致内容重复而在启动时报Result Maps collection already contains value异常
     * 默认为true,本类已经自动先把原来的mapper xml文件全部清除,所以不会有上面的问题
     */
    public ExecuteGenerator setOverwrite(boolean overwrite) {
        if (! overwrite) {
            LOGGER.warn("!!!!!!!!!!!!!! 如果想将override设为false需要谨慎操作 !!!!!!!!!!!!!!");
            LOGGER.warn("因为mapper xml文件总是以追加的形式重新生成的,有可能导致内容重复而在启动时报Result Maps collection already contains value异常");
        }
        this.overwrite = overwrite;
        return this;
    }

    public ExecuteGenerator setWarnings(List<String> warnings) {
        this.warnings = warnings;
        return this;
    }

    public ExecuteGenerator setPageClass(Class pageClass) {
        this.pageClass = pageClass;
        return this;
    }

    public ExecuteGenerator setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        return this;
    }

    public ExecuteGenerator setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
        return this;
    }

    public ExecuteGenerator setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
        return this;
    }

    public ExecuteGenerator setJavaCodeBasePath(String javaCodeBasePath) {
        this.javaCodeBasePath = javaCodeBasePath;
        return this;
    }

    public ExecuteGenerator setResourcesBasePath(String resourcesBasePath) {
        this.resourcesBasePath = resourcesBasePath;
        return this;
    }

    public ExecuteGenerator setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
        return this;
    }

    public ExecuteGenerator setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
        return this;
    }

    public ExecuteGenerator setMapperXmlFolder(String mapperXmlFolder) {
        this.mapperXmlFolder = mapperXmlFolder;
        return this;
    }
}
