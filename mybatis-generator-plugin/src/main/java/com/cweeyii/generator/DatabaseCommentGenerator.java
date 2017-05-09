package com.cweeyii.generator;


import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * 可以在生成domain的时候将数据库相应字段的注释也自动的生成了
 */
public class DatabaseCommentGenerator extends DefaultCommentGenerator {
	private boolean skipDatabaseComment = false;
    private boolean skipMethodComment = true;
    private boolean skipClassComment = true;
    private boolean skipFieldComment = true;
    private boolean skipXmlComment = true;
    private boolean skipEnumComment = true;
    private boolean skipGetterSetterComment = true;
    private boolean skipJavaFileComment = true;

	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
		if (!skipDatabaseComment && StringUtils.isNotEmpty(introspectedColumn.getRemarks())) {
			field.addJavaDocLine("/**");
			field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
			field.addJavaDocLine(" */");
		}
	}

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        if (skipJavaFileComment) {
            return;
        }
        super.addJavaFileComment(compilationUnit);
    }

    @Override
    public void addComment(XmlElement xmlElement) {
        if (skipXmlComment) {
            return;
        }
        super.addComment(xmlElement);
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (skipFieldComment) {
            return;
        }
        super.addFieldComment(field, introspectedTable);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (skipClassComment) {
            return;
        }
        super.addClassComment(innerClass, introspectedTable);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (skipClassComment) {
            return;
        }
        super.addClassComment(innerClass, introspectedTable, markAsDoNotDelete);
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (skipMethodComment) {
            return;
        }
        super.addGeneralMethodComment(method, introspectedTable);
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (skipEnumComment) {
            return;
        }
        super.addEnumComment(innerEnum, introspectedTable);
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (skipGetterSetterComment) {
            return;
        }
        super.addGetterComment(method, introspectedTable, introspectedColumn);
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (skipGetterSetterComment) {
            return;
        }
        super.addSetterComment(method, introspectedTable, introspectedColumn);
    }

	@Override
	public void addConfigurationProperties(Properties properties) {
		super.addConfigurationProperties(properties);
		skipDatabaseComment = isTrue(properties
				.getProperty("skipDatabaseComment"));
        skipMethodComment = isFalse(properties
                .getProperty("skipMethodComment"));
        skipClassComment = isFalse(properties
                .getProperty("skipClassComment"));
        skipJavaFileComment = isFalse(properties
                .getProperty("skipJavaFileComment"));
        skipFieldComment = isFalse(properties
                .getProperty("skipFieldComment"));
        skipXmlComment = isFalse(properties
                .getProperty("skipXmlComment"));
        skipEnumComment = isFalse(properties
                .getProperty("skipEnumComment"));
        skipGetterSetterComment = isFalse(properties
                .getProperty("skipGetterSetterComment"));
    }

    protected boolean isFalse(String s) {
        return ! isTrue(s);
    }
}
