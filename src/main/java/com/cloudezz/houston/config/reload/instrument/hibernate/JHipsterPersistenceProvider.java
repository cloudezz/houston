package com.cloudezz.houston.config.reload.instrument.hibernate;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to instrument the HibernatePersistenceProvider class
 * @see com.mycompany.myapp.config.reload.instrument.JHipsterLoadtimeInstrumentationPlugin
 */
public abstract class JHipsterPersistenceProvider implements PersistenceProvider {

    private Logger log = LoggerFactory.getLogger(JHipsterPersistenceProvider.class);

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        log.trace( "Starting createContainerEntityManagerFactory : {}", info.getPersistenceUnitName() );

        return new JHipsterEntityManagerFactoryWrapper(info, properties);
    }
}
