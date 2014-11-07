package db.bean;

import java.util.List;

/**
 * Created by LENOVO on 2014/11/6.
 */
public class GraphNode {
    private int id;
    private String label;
    private List parents;
    private List children;

    public GraphNode() {

    }

    public GraphNode(String label) {
        this.label = label;
    }

    public GraphNode(String label, List parents, List children) {
        this.label = label;
        this.parents = parents;
        this.children = children;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List getParents() {
        return parents;
    }

    public void setParents(List parents) {
        this.parents = parents;
    }

    public List getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }
}
