package hierarchygraph;

import db.bean.GraphNode;
import db.dao.IGraphDAO;
import db.dao.impl.GraphDAOImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HierarchyGraph {
    public static final int RETRY_TIME = 3;
    static String baseURL = "http://www.dmoz.org";
    static Queue<Category> queue = new LinkedList<>();
    static IGraphDAO graphDAO = new GraphDAOImpl();
    static Log log = LogFactory.getLog(HierarchyGraph.class);

    public static void main(String[] args) {
        log.info("-----------------start------------------");
        log.info("add root node:");
        graphDAO.insertGraphNode(new GraphNode("root"));
        Category cat = new Category("http://www.dmoz.org/World/Chinese_Simplified/", "root");
        queue.add(cat);
        HierarchyGraph hierarchyGraph = new HierarchyGraph();
        hierarchyGraph.buildHierarchyGraph();
        log.info("-----------------end------------------");
    }

    /**
     * build the hierarchy graph
     */
    public void buildHierarchyGraph() {
        Category currentCategory;
        String currentUrl;
        String parentLabel;
        while(!queue.isEmpty()) {
            //to obtain and remove the first element of the queue
            currentCategory = queue.poll();
            currentUrl = currentCategory.getUrl();

            parentLabel = currentCategory.getLabel();
            GraphNode graphNode= graphDAO.getGraphNodeByLabel(parentLabel);

            Document doc = getDocument(currentUrl);
            Elements elements = parseDucument(doc);
            parseElements(elements, graphNode);
        }
    }

    /**
     * get the web pages' source code based on the url
     * @param url web url
     * @return web pages' source code
     */
    public Document getDocument(String url) {
        //count the times of reconnection
        int reConnect = 0;
        Document doc = null;
        do {
            try {
                doc = Jsoup.connect(url).get();
                return doc;
            } catch (IOException e) {
                reConnect ++;
                if(reConnect < RETRY_TIME) {
                    log.info("Request timed out, try to reconnect... " + url + " for the " + reConnect + " time(s)");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        continue;
                    }
                }
            }
        } while(reConnect < RETRY_TIME);
        return null;
    }

    /**
     * parse the web page and get the tag<li> under tag<ul> which meet the demand
     * @param doc assemble of the web pages
     * @return the assemble of the tag<li>
     */
    public Elements parseDucument(Document doc) {
        if(null != doc) {
            //select tag<ul class="directory dir-col">
            Elements ulElements = doc.select("ul").select(".directory").select(".dir-col");
            //select tag<a> which is belong to the <li>
            if (null != ulElements && !ulElements.isEmpty()) {
                Elements aElements = ulElements.select("li").select("a");
                if (null != aElements && !aElements.isEmpty())
                    return aElements;
            }
        }
        return null;
    }

    /**
     * parse the tag<li> and get the Chinese words(category labels) and the
     * url
     * @param elements tag<li> elements
     */
    public void parseElements(Elements elements, GraphNode parentGraphNode) {
        if(elements != null) {

            String label;
            String url;
            Category category;
            for (Element e : elements) {
                label = parseLabel(e);
                if(!updateDB(parentGraphNode, label)) {
                    url = parseUrl(e);
                    category = new Category(url, label);
                    queue.add(category);
                }
            }
        }
    }

    /**
     * update the database bases on the hierarchical relationship
     * @param parentGraphNode parent node
     * @param label child node's label
     * @return true when child node is in db, else false
     */
    public boolean updateDB(GraphNode parentGraphNode, String label) {
        GraphNode graphNode = graphDAO.getGraphNodeByLabel(label);
        boolean childExist = false;
        GraphNode childGraphNode = null;
        if(null == graphNode) {
            childGraphNode = new GraphNode(label);
            graphDAO.insertGraphNode(childGraphNode);
        } else {
            childExist = true;
            childGraphNode = graphNode;
        }
        int parentId = parentGraphNode.getId();
        int childId = childGraphNode.getId();

        updateParentNode(parentGraphNode, childId);
        updateChildNode(childGraphNode, parentId);

        return childExist;
    }

    /**
     * update parent node's children list
     * @param parentGraphNode parent node
     * @param childId child node's id
     */
    public void updateParentNode(GraphNode parentGraphNode, int childId) {
        List parentChildrenList = updateList(parentGraphNode.getChildren(), childId);
        parentGraphNode.setChildren(parentChildrenList);
        graphDAO.updateGraphNode(parentGraphNode);
    }

    /**
     * update child node's parents list
     * @param childGraphNode child node
     * @param parentId parent node's id
     */
    public void updateChildNode(GraphNode childGraphNode, int parentId) {
        List childParentsList = updateList(childGraphNode.getParents(), parentId);
        childGraphNode.setParents(childParentsList);
        graphDAO.updateGraphNode(childGraphNode);
    }

    /**
     * add the id into the list
     * @param list source list
     * @param id number to add
     * @return the list had added the id
     */
    public List updateList(List<String> list, int id) {
        if(null != list) {
            list.add(String.valueOf(id));
        } else {
            list = new ArrayList<>();
            list.add(String.valueOf(id));
        }
        return list;
    }

    /**
     * parse tag<a ...>...</a> for every Element to get the url
     * @param element one Element in Elements
     * @return url
     */
    public String parseUrl(Element element) {
        StringBuilder stringBuilder = new StringBuilder(baseURL);
        String result = element.toString();
        String[] arr = result.split("\"");
        try {
            stringBuilder.append(URLDecoder.decode(arr[1].trim(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("decode \"" + result + "\" failed...unsupported encoding!");
            e.printStackTrace();
        }
        //add the url to the log file
        log.info("url: "+stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * parse tag<a ...>...</a> for every Element to get the label
     * @param element one Element in Elements
     * @return label name
     */
    public String parseLabel(Element element) {
        String label = element.toString().replaceAll("<[^>]*>", "").trim();
        //add the label to the log file
        log.info("label: "+label);
        return label;
    }

    static class Category {
        String url;
        String label;

        Category() {

        }

        Category(String url, String label) {
            this.url = url;
            this.label = label;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}