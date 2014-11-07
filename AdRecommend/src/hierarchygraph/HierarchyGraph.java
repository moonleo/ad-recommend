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
    IGraphDAO graphDAO = new GraphDAOImpl();
    static Log log = LogFactory.getLog(HierarchyGraph.class);

    public static void main(String[] args) {
        log.info("-----------------start------------------");
        Category cat = new Category("http://www.dmoz.org/World/Chinese_Simplified/", "Root");
        queue.add(cat);
        HierarchyGraph hierarchyGraph = new HierarchyGraph();
        hierarchyGraph.buildHierarchyGraph();
        log.info("-----------------end------------------");
    }

    /**
     * build the hierarchy graph
     */
    public void buildHierarchyGraph() {
        Category currentCatefory;
        String currentUrl;
        String parentLabel;
        while(!queue.isEmpty()) {
            //to obtain and remove the first element of the queue
            currentCatefory = queue.poll();
            currentUrl = currentCatefory.getUrl();

            parentLabel = currentCatefory.getLabel();
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
                    System.out.println("Request timed out, try to reconnect... "+url);
                    System.out.println("----the "+reConnect+" time(s)----");
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
            //select tag<li> which is belong to the <ul>
            if (null != ulElements && !ulElements.isEmpty()) {
                Elements liElements = ulElements.select("li");
                if (null != liElements && !liElements.isEmpty())
                    return liElements;
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
            //regular expression to match the url
            Pattern patternURL = Pattern.compile("href=\"(?<url>[\\s\\S]*?)\"");
            //regular expression to match the Chinese words
            Pattern patternChinese = Pattern.compile("[\\u4e00-\\u9fa5]");

            String label;
            String url;
            Category category;
            GraphNode graphNode;
            for (Element e : elements) {
                label = parseLabel(e, patternChinese);
                if(!updateDB(parentGraphNode, label)) {
                    url = parseUrl(e, patternURL);
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
     * use the regular expression to parse one Element in Elements to get the url
     * @param element one Element in Elements
     * @param pattern regular expression pattern
     * @return url
     */
    public String parseUrl(Element element, Pattern pattern) {
        StringBuilder stringBuilder = new StringBuilder(baseURL);
        Matcher matcher = pattern.matcher(element.toString());
        String result;
        String[] arr;
        while(matcher.find()) {
            result = matcher.group();
            arr = result.split("\"");
            try {
                stringBuilder.append(URLDecoder.decode(arr[1], "utf-8"));
            } catch (UnsupportedEncodingException e) {
                System.out.println("decode \""+result+"\" failed...unsupported encoding!");
                e.printStackTrace();
            }
        }
        //add the url to the log file
        log.info(stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * use the regular expression to parse one Element in Elements to get the label
     * @param element one Element in Elements
     * @param pattern regular expression pattern
     * @return label name
     */
    public String parseLabel(Element element, Pattern pattern) {
        StringBuilder stringBuilder = new StringBuilder();
        Matcher matcher = pattern.matcher(element.toString());
        while(matcher.find()) {
            stringBuilder.append(matcher.group());
        }
        //add the label to the log file
        log.info(stringBuilder.toString()+"---");
        return stringBuilder.toString();
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