package test;

import db.bean.GraphNode;
import db.dao.IGraphDAO;
import db.dao.impl.GraphDAOImpl;
import hierarchygraph.HierarchyGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LENOVO on 2014/11/6.
 */
public class TestHibernateUtil {
    public static void main(String[] args) {
        IGraphDAO graphDAO = new GraphDAOImpl();
        graphDAO.cleanTable();
        /*List list1 = new ArrayList();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        List list2 = new ArrayList();
        list2.add("4");
        list2.add("5");
        list2.add("6");
        GraphNode graphNode = new GraphNode("testlabel", list1, list2);
        System.out.println("generate graphNode");
        graphDAO.insertGraphNode(graphNode);
        System.out.println("finish insert");*/

        /*List list = graphDAO.getGraphNodes();
        for (int i = 0; i < list.size(); i++) {
            System.out.print("id:"+((GraphNode)list.get(i)).getId()+"\t");
            System.out.print("label:"+((GraphNode)list.get(i)).getLabel()+"\t");
        }

        GraphNode graphNode = graphDAO.getGraphNodeById(1);
        System.out.print("id:"+graphNode.getId()+"\t");
        System.out.print("label:"+graphNode.getLabel()+"\t");

        GraphNode graphNode1 = graphDAO.getGraphNodeByLabel("testlabel");
        System.out.print("id:"+graphNode.getId()+"\t");
        System.out.print("label:"+graphNode.getLabel()+"\t");*/

        /*GraphNode graphNode = graphDAO.getGraphNodeByLabel("test3");
        System.out.println(graphNode==null?null:graphNode.getId());*/
        /*List<String> plist = new ArrayList<>();
        plist.add("1");
        plist.add("2");
        plist.add("3");
        graphNode.setParents(plist);
        graphDAO.updateGraphNode(graphNode);
        System.out.println(graphNode.getParents()==null?null:graphNode.getParents().size());*/
        /*HierarchyGraph hierarchyGraph = new HierarchyGraph();
        GraphNode graphNode = graphDAO.getGraphNodeByLabel("test2");
        GraphNode graphNode1 = graphDAO.getGraphNodeByLabel("test");
        //hierarchyGraph.updateParentNode(graphNode, 5);
        hierarchyGraph.updateChildNode(graphNode1, 1);*/
    }
}
