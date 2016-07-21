package ru.nekki.test.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class DAOService {
    private final static Logger logger =
            LogManager.getLogger(DAOService.class);

    private static SessionFactory sessionFactory;

    static {
        setUp();
    }

    private static void setUp() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory =
                    new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            logger.error("DB is not available", e);
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }

    public static void save(Entry entry) {
        Session session = sessionFactory.openSession();
        session.save(entry);
        session.flush();
        session.close();
    }
}