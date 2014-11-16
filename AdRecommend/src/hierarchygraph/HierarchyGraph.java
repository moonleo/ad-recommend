package hierarchygraph;

import db.bean.GraphNode;
import db.bean.UrlItem;
import db.dao.IGraphDAO;
import db.dao.IUrlDAO;
import db.dao.impl.GraphDAOImpl;
import db.dao.impl.UrlDAOImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class HierarchyGraph {
    public static final int RETRY_TIME = 5;
    static String baseURL = "http://www.dmoz.org";
    static Queue<Category> queue = new LinkedList<>();
    static IGraphDAO graphDAO = new GraphDAOImpl();
    static IUrlDAO urlDAO = new UrlDAOImpl();
    static Log log = LogFactory.getLog(HierarchyGraph.class);

    public static void main(String[] args) {
        log.info("clean table: hierarchygraph/urllist");
        graphDAO.cleanTable();
        urlDAO.cleanTable();
        log.info("finished clean the table, add initial data...");
        Category category = new Category("http://www.dmoz.org/World/Chinese_Simplified/", "Root");
        GraphNode rootNode = new GraphNode("Root");
        graphDAO.insertGraphNode(rootNode);
        queue.add(category);
        log.info("----------------begin-----------------");
        HierarchyGraph hierarchyGraph = new HierarchyGraph();
        hierarchyGraph.buildHierarchyGraph();
        log.info("-----------------end------------------");
    }

    public static void addInQueue() {
        Category xiuxian = new Category("http://www.dmoz.org/World/Chinese_Simplified/休闲/", "休闲");
        GraphNode xiuxianNode = new GraphNode("休闲"); graphDAO.insertGraphNode(xiuxianNode);
        Category tiyu = new Category("http://www.dmoz.org/World/Chinese_Simplified/体育/", "体育");
        GraphNode tiyuNode = new GraphNode("体育"); graphDAO.insertGraphNode(tiyuNode);
        Category jiankang = new Category("http://www.dmoz.org/World/Chinese_Simplified/健康/", "健康");
        GraphNode jiankangNode = new GraphNode("健康"); graphDAO.insertGraphNode(jiankangNode);
        Category ertongyuqingshaonian = new Category("http://www.dmoz.org/Kids_and_Teens/International/Chinese_Simplified/", "儿童与青少年");
        GraphNode ertongNode = new GraphNode("儿童与青少年"); graphDAO.insertGraphNode(ertongNode);
        Category cankao = new Category("http://www.dmoz.org/World/Chinese_Simplified/参考/", "参考");
        GraphNode cankaoNode = new GraphNode("参考"); graphDAO.insertGraphNode(cankaoNode);
        Category shangye = new Category("http://www.dmoz.org/World/Chinese_Simplified/商业/", "商业");
        GraphNode shangyeNode = new GraphNode("商业"); graphDAO.insertGraphNode(shangyeNode);
        Category jiating = new Category("http://www.dmoz.org/World/Chinese_Simplified/家庭/", "家庭");
        GraphNode jiatingNode = new GraphNode("家庭"); graphDAO.insertGraphNode(jiatingNode);
        Category xinwen = new Category("http://www.dmoz.org/World/Chinese_Simplified/新闻", "新闻");
        GraphNode xinwenNode = new GraphNode("新闻"); graphDAO.insertGraphNode(xinwenNode);
        Category youxi = new Category("http://www.dmoz.org/World/Chinese_Simplified/游戏/", "游戏");
        GraphNode youxiNode = new GraphNode("游戏"); graphDAO.insertGraphNode(youxiNode);
        Category shehui = new Category("http://www.dmoz.org/World/Chinese_Simplified/社会/", "社会");
        GraphNode shehuiNode = new GraphNode("社会"); graphDAO.insertGraphNode(shehuiNode);
        Category kexue = new Category("http://www.dmoz.org/World/Chinese_Simplified/科学/", "科学");
        GraphNode kexueNode = new GraphNode("科学"); graphDAO.insertGraphNode(kexueNode);
        Category yishu = new Category("http://www.dmoz.org/World/Chinese_Simplified/艺术/", "艺术");
        GraphNode yishuNode = new GraphNode("艺术"); graphDAO.insertGraphNode(yishuNode);
        Category jisuanji = new Category("http://www.dmoz.org/World/Chinese_Simplified/计算机/", "计算机");
        GraphNode jisuanjiNode = new GraphNode("计算机"); graphDAO.insertGraphNode(jisuanjiNode);
        Category gouwu = new Category("http://www.dmoz.org/World/Chinese_Simplified/购物/", "购物");
        GraphNode gouwuNode = new GraphNode("购物"); graphDAO.insertGraphNode(gouwuNode);
        queue.add(xiuxian);
        queue.add(tiyu);
        queue.add(jiankang);
        queue.add(ertongyuqingshaonian);
        queue.add(cankao);
        queue.add(shangye);
        queue.add(jiating);
        queue.add(xinwen);
        queue.add(youxi);
        queue.add(shehui);
        queue.add(kexue);
        queue.add(yishu);
        queue.add(jisuanji);
        queue.add(gouwu);
    }

    /**
     * build the hierarchy graph
     */
    public void buildHierarchyGraph() {
        Category currentCategory;
        String currentUrl;
        String parentLabel;
        while(!queue.isEmpty()) {
            //acquire and remove the first element of the queue
            currentCategory = queue.poll();
            currentUrl = currentCategory.getUrl();

            parentLabel = currentCategory.getLabel();
            GraphNode graphNode= graphDAO.getGraphNodeByLabel(parentLabel);

            Document doc = getDocument(currentUrl);
            Elements elements = parseDucument(doc);

            List websites = getWebsitesLink(doc);
            if(null != websites) {
                List list = graphNode.getWebsites();
                if(null == list)
                    list = new ArrayList();
                list.addAll(websites);
                graphNode.setWebsites(list);
                graphDAO.updateGraphNode(graphNode);
            }

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
            UrlItem urlItem;
            for (Element e : elements) {
                label = parseLabel(e);
                if(!label.equals("地区")) {
                    updateDB(parentGraphNode, label);
                    url = parseUrl(e);
                    if(null == urlDAO.getItemByUrl(url)) {
                        urlItem = new UrlItem(url);
                        urlDAO.insertUrl(urlItem);
                        category = new Category(url, label);
                        queue.add(category);
                    }
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
    public void updateDB(GraphNode parentGraphNode, String label) {
        GraphNode graphNode = graphDAO.getGraphNodeByLabel(label);
        GraphNode childGraphNode = null;
        if(null == graphNode) {
            childGraphNode = new GraphNode(label);
            graphDAO.insertGraphNode(childGraphNode);
        } else {
            childGraphNode = graphNode;
        }
        int parentId = parentGraphNode.getId();
        int childId = childGraphNode.getId();

        updateParentNode(parentGraphNode, childId);
        updateChildNode(childGraphNode, parentId);
    }

    /**
     * update parent node's children List
     * @param parentGraphNode parent node
     * @param childId child node's id
     */
    public void updateParentNode(GraphNode parentGraphNode, int childId) {
        List parentChildrenList = updateList(parentGraphNode.getChildren(), childId);
        parentGraphNode.setChildren(parentChildrenList);
        graphDAO.updateGraphNode(parentGraphNode);
    }

    /**
     * update child node's parents List
     * @param childGraphNode child node
     * @param parentId parent node's id
     */
    public void updateChildNode(GraphNode childGraphNode, int parentId) {
        List childParentsList = updateList(childGraphNode.getParents(), parentId);
        childGraphNode.setParents(childParentsList);
        graphDAO.updateGraphNode(childGraphNode);
    }

    /**
     * add the id into the List
     * @param list source List
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

    public List getWebsitesLink(Document doc) {
        List websites = null;
        if(null != doc) {
            Elements ulElements = doc.select("ul").select(".directory-url");
            if(!ulElements.isEmpty()) {
                websites = new ArrayList();
                Elements liElements = ulElements.select("li");
                Elements aElements = liElements.select("a").select(".listinglink");
                for (Element e : aElements) {
                    String eStr = e.toString();
                    String[] arr = eStr.split("\"");
                    String websiteUrl = arr[1];
                    String websiteTitle = eStr.replaceAll("<[^>]*>", "").trim();
                    String website = websiteTitle + "-" + websiteUrl;
                    websites.add(website);
                }
            }
        }
        return websites;
    }

    static class Category {
        String url;
        String label;

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