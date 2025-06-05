package com.nichi.mergednifty50index.database.pavan.dao;

import com.nichi.mergednifty50index.DTO.ComboDataDTO;
import com.nichi.mergednifty50index.database.pavan.model.TradeList;
import com.nichi.mergednifty50index.database.pavan.model.StocksList;
import com.nichi.mergednifty50index.database.pavan.utils.HibernateUtils;
import com.nichi.mergednifty50index.database.pavan.model.TradeListId;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class TradeEntryDAO {

    public void saveAll(List<TradeList> tradeLists) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            for (TradeList trade : tradeLists) {
                session.merge(trade);
            }

            transaction.commit();

        }catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println(e.getMessage());

        }finally {
            session.close();

        }
    }

    public List<TradeList> getAllTradeList() {
        Session session = HibernateUtils.getSessionFactory().openSession();
        List<TradeList> tradeLists = null;

        try {
            Query<TradeList> query = session.createQuery("FROM TradeList order by tradeNo asc", TradeList.class);
            tradeLists = query.list();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            session.close();
        }
        return tradeLists;
    }

    public void deleteTrade(Integer tradeNo, String code) {
        System.out.println(tradeNo + " " + code);
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            TradeListId tradeListId = new TradeListId(tradeNo, code);
            TradeList trade = session.get(TradeList.class, tradeListId);
            System.out.println("hehe boy");

            if (trade != null) {
                session.remove(trade);
                System.out.println("delete aythu");
            }
            transaction.commit();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            session.close();
        }
    }

    public List<ComboDataDTO> getCodeData() {
        Session session = HibernateUtils.getSessionFactory().openSession();
        String selectDistinctCode = "SELECT DISTINCT code FROM tradeslist";
        List<ComboDataDTO> combo = new ArrayList<>();
        try {
            List<String> codes = session.createNativeQuery(selectDistinctCode).getResultList();
            for (String v : codes) {
                combo.add(new ComboDataDTO(v));
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            session.close();
        }
        return combo;
    }
}
