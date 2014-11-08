package db.dao;

import db.bean.GraphNode;
import java.util.List;

public interface IGraphDAO {
    public List getGraphNodes();
    public void insertGraphNode(GraphNode graphNode);
    public void deleteGraphNode(GraphNode graphNode);
    public void updateGraphNode(GraphNode graphNode);
    public GraphNode getGraphNodeById(int id);
    public GraphNode getGraphNodeByLabel(String label);
    public void cleanTable();
    /*public int getIdByLabel(String label);*/
}
