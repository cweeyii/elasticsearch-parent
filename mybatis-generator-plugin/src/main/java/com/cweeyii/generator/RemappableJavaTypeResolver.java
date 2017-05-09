package com.cweeyii.generator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 可以重新映射数据库中的type跟java中的type的映射,比如TINYINT映射成Integer
 */
public class RemappableJavaTypeResolver extends JavaTypeResolverDefaultImpl {
	private final Map<String, Integer> jdbcTypeIndexByName;

	public RemappableJavaTypeResolver() {
		jdbcTypeIndexByName = Maps.newHashMap();
		for (Map.Entry<Integer, JdbcTypeInformation> typeEntry : typeMap.entrySet()) {
			jdbcTypeIndexByName.put(typeEntry.getValue().getJdbcTypeName(), typeEntry.getKey());
		}
	}

	@Override
	public void addConfigurationProperties(Properties properties) {
		super.addConfigurationProperties(properties);
		Set<String> ignoreProperties = ignorePropertiesName();
		for (Map.Entry<Object, Object> typeMapEntry : properties.entrySet()) {
			String typeName = typeMapEntry.getKey().toString();
			if (! ignoreProperties.contains(typeName)) {
				Integer typeIndex = jdbcTypeIndexByName.get(typeName);
				Preconditions.checkNotNull(typeIndex, "RemappableJavaTypeResolver:不存在名字为" + typeName + "的jdbc类型,具体名字可以参考java.sql.Types类的变量的名字");
				try {
					Class javaType = Class.forName(typeMapEntry.getValue().toString());
					super.typeMap.put(typeIndex, new JdbcTypeInformation(typeName,
							new FullyQualifiedJavaType(javaType.getName())));
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException("RemappableJavaTypeResolver:java中不存在类型:" + typeMapEntry.getValue().toString());
				}
			}
		}
	}

	protected Set<String> ignorePropertiesName() {
		return Sets.newHashSet(PropertyRegistry.TYPE_RESOLVER_FORCE_BIG_DECIMALS);
	}
}
