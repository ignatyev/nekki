package ru.nekki.test.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class DAOService {
    private final static Logger logger =
            LogManager.getLogger(DAOService.class);

    private static SessionFactory sessionFactory;

    static {
        setUp();
    }

    private static void setUp() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration().configure();
            ServiceRegistry serviceRegistry =
                    new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            logger.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void save(Entry entry) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(entry);
        session.flush();
        transaction.commit();
        session.close();
        logger.debug("Saved entry of id: " + entry.getId());
    }
}
