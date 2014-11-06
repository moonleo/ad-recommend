package db.dao.impl;

import db.bean.GraphNode;
import db.dao.IGraphDAO;
import db.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by LENOVO on 2014/11/6.
 */
public class GraphDAOImpl implements IGraphDAO {

    private static HibernateUtil hibernateUtil;

    @Override
    public List getGraphNodes() {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        List list = session.createQuery("").list();
        HibernateUtil.closeSession();
        return list;
    }

    @Override
    public void insertGraphNode(GraphNode graphNode) {

    }

    @Override
    public void deleteGraphNode(GraphNode graphNode) {

    }

    @Override
    public void updateGraphNode(GraphNode graphNode) {

    }

    @Override
    public GraphNode getGraphNode(GraphNode graphNode) {
        return null;
    }

    @Override
    public List getParents(GraphNode graphNode) {
        return null;
    }

    @Override
    public List getChildren(GraphNode graphNode) {
        return null;
    }

    @Override
    public void updateParents(GraphNode graphNode, List parents) {

    }

    @Override
    public void updateChildren(GraphNode graphNode, List children) {

    }

    @Override
    public GraphNode getGraphNodeById(int id) {
        return null;
    }

    @Override
    public GraphNode getGraphNodeByLabel(String label) {
        return null;
    }
}
