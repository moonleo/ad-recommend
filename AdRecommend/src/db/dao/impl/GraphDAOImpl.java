package db.dao.impl;

import db.bean.GraphNode;
import db.dao.IGraphDAO;
import db.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 2014/11/6.
 */
public class GraphDAOImpl implements IGraphDAO {

    Session session;
    Transaction transaction;

    @Override
    public List getGraphNodes() {
        session = HibernateUtil.getSession();
        List list = session.createQuery("from db.bean.GraphNode").list();
        HibernateUtil.closeSession();
        return list;
    }

    @Override
    public void insertGraphNode(GraphNode graphNode) {
        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            session.save(graphNode);
            transaction.commit();
        } catch (HibernateException e) {
            if(null != transaction)
                transaction.rollback();
            throw e;
        } finally {
            HibernateUtil.closeSession();
        }
    }

    @Override
    public void deleteGraphNode(GraphNode graphNode) {
        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            session.delete(graphNode);
            transaction.commit();
        } catch (HibernateException e) {
            if(null != transaction)
                transaction.rollback();
            throw e;
        } finally {
            HibernateUtil.closeSession();
        }
    }

    @Override
    public void updateGraphNode(GraphNode graphNode) {
        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            session.update(graphNode);
            transaction.commit();
        } catch (HibernateException e) {
            if(null != transaction)
                transaction.rollback();
            throw e;
        } finally {
            HibernateUtil.closeSession();
        }
    }

    @Override
    public GraphNode getGraphNodeById(int id) {
        session = HibernateUtil.getSession();
        String hql = "from db.bean.GraphNode g where g.id=?";
        List<GraphNode> graphList = session.createQuery(hql)
                .setParameter(0, id).list();
        if(graphList.size() != 0)
            return graphList.get(0);
        return null;
    }

    @Override
    public GraphNode getGraphNodeByLabel(String label) {
        session = HibernateUtil.getSession();
        String hql = "from db.bean.GraphNode g where g.label=?";
        List<GraphNode> graphList = session.createQuery(hql)
                .setParameter(0, label).list();
        if(graphList.size() != 0)
            return graphList.get(0);
        return null;
    }

    @Override
    public void cleanTable() {
        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            String hql = "delete db.bean.GraphNode";
            session.createQuery(hql).executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            if(null != transaction)
                transaction.rollback();
            throw e;
        } finally {
            HibernateUtil.closeSession();
        }
    }
}
