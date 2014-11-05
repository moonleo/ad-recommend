package hierarchygraph;

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
    static String initURL = "http://www.dmoz.org/World/Chinese_Simplified/";
    static String baseURL = "http://www.dmoz.org";
    static Queue<String> queue = new LinkedList<String>();
    static FileWriter fileWriter = null;
    static BufferedWriter bufferedWriter = null;
    static String filePath = "E:/IdeaProjects/drunken-bear/dmoz.txt";

    public static void main(String[] args) {
        try {
            fileWriter = new FileWriter(filePath);
            bufferedWriter = new BufferedWriter(fileWriter);
            System.out.println("-----------start-----------");
            queue.add(initURL);
            HierarchyGraph testJsoup = new HierarchyGraph();
            testJsoup.buildHierarchyGraph();
        } catch (IOException e) {
            System.out.println("open file error!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
                System.out.println("-----------end-----------");
            } catch (IOException e) {
                System.out.println("close file error");
                e.printStackTrace();
            }
        }
    }

    /**
     * build the hierarchy graph
     */
    public void buildHierarchyGraph() {
        String currentUrl;
        while(!queue.isEmpty()) {
            //to obtain and remove the first element of the queue
            currentUrl = queue.poll();
            Document doc = getDocument(currentUrl);
            Elements elements = parseDucument(doc);
            parseElements(elements);
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
    public void parseElements(Elements elements) {
        if(elements != null) {
            StringBuilder sb;
            //regular expression to match the Chinese words
            //Pattern patternChinese = Pattern.compile("[\\u4e00-\\u9fa5]");
            //regular expression to match the url
            Pattern patternURL = Pattern.compile("href=\"(?<url>[\\s\\S]*?)\"");
            Matcher matcherChinese;
            Matcher matcherUrl;
            String hrefStr;
            String[] arr;
            String appendURL;
            for (Element e : elements) {
                sb = new StringBuilder(baseURL);
                //match the url
                matcherUrl = patternURL.matcher(e.toString());
                if(matcherUrl.find()) {
                    hrefStr = matcherUrl.group();
                    arr = hrefStr.split("\"");
                    try {
                        //get the url and decode it
                        appendURL = URLDecoder.decode(arr[1], "utf-8");
                        sb.append(appendURL);
                        //sb.append("/");
                    } catch (UnsupportedEncodingException e1) {
                        System.out.println("Unsupported encoding...");
                        System.out.println("fail to  decode: "+arr[1]);
                        e1.printStackTrace();
                    }
                    queue.add(sb.toString());
                    try {
                        bufferedWriter.write(sb.toString());
                        bufferedWriter.write("\n\r");
                    } catch (IOException e1) {
                        System.out.println("write into file failed...");
                        e1.printStackTrace();
                    }
                }

                /*matcherChinese = patternChinese.matcher(e.toString());
                while (matcherChinese.find()) {
                    sb.append(matcherChinese.group());
                }
                sb.append("/");
                try {
                    //write the url into the file
                    bufferedWriter.write(sb.toString());
                    //write the line separator
                    bufferedWriter.write("\n\r");
                } catch (IOException e1) {
                    System.out.println("write into file error!");
                    e1.printStackTrace();
                }*/

            }
        }
    }
}