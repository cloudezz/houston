package com.cloudezz.houston.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("com.cloudezz.houston.repository")
@EnableTransactionManagement
public class DatabaseConfiguration implements EnvironmentAware {

	private final Logger log = LoggerFactory
			.getLogger(DatabaseConfiguration.class);

	private RelaxedPropertyResolver propertyResolver;

	@Inject
	private Environment env;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment,
				"spring.datasource.");
	}

	@Bean
	public DataSource dataSource() {
		log.debug("Configuring Datasource");
		if (propertyResolver.getProperty("url") == null
				&& propertyResolver.getProperty("databaseName") == null) {
			log.error(
					"Your database connection pool configuration is incorrect! The application"
							+ "cannot start. Please check your Spring profile, current profiles are: {}",
					Arrays.toString(env.getActiveProfiles()));

			throw new ApplicationContextException(
					"Database connection pool is not configured correctly");
		}

		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassName(propertyResolver
				.getProperty("driverClass"));
		if (propertyResolver.getProperty("url") == null
				|| "".equals(propertyResolver.getProperty("url"))) {
		} else {
			dataSource.setUrl(propertyResolver.getProperty("url"));
		}
		dataSource.setUsername(propertyResolver.getProperty("username"));
		dataSource.setPassword(propertyResolver.getProperty("password"));
		return dataSource;
	}

	@Bean(name = { "org.springframework.boot.autoconfigure.AutoConfigurationUtils.basePackages" })
	public List<String> getBasePackages() {
		List<String> basePackages = new ArrayList<>();
		basePackages.add("com.cloudezz.houston.domain");
		return basePackages;
	}

	@Bean
	@Profile(value={"prod","production"})
	public SpringLiquibase liquibase() {
		log.debug("Configuring Liquibase");
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource());
		liquibase.setChangeLog("classpath:config/liquibase/db-changelog.xml");
		liquibase.setContexts("development, production");
		return liquibase;
	}
}
