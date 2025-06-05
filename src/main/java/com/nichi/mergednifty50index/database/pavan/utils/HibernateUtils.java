package com.nichi.mergednifty50index.database.pavan.utils;

import com.nichi.mergednifty50index.database.pavan.model.StocksList;
import com.nichi.mergednifty50index.database.pavan.model.TradeList;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    private static SessionFactory sessionFactory;

    public static void connectToDatabase(){
        try {
            sessionFactory = new Configuration()
                    .configure()
                    .addAnnotatedClass(StocksList.class)
                    .addAnnotatedClass(TradeList.class)
                    .buildSessionFactory();
            System.out.println("running");
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
