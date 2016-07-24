package ru.nekki.test.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.internal.StandardServiceRegistryImpl;

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
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            logger.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void save(Entry entry) {
        Session session = sessionFactory.openSession();  //TODO open created session?
        session.saveOrUpdate(entry);
        session.flush();
        session.close();
        logger.debug("saved");
    }
}
