package hierarchygraph;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HierarchyGraph {
    public static final int RETRY_TIME = 3;
    static String initURL = "http://www.dmoz.org/World/Chinese_Simplified/";
    static Queue<String> queue = new LinkedList<String>();
    static FileWriter fileWriter = null;
    static BufferedWriter bufferedWriter = null;
    public static void main(String[] args) {
        try {
            fileWriter = new FileWriter("E:\\IdeaProjects\\Test\\dmoz.txt");
            bufferedWriter = new BufferedWriter(fileWriter);
            System.out.println("-----------start-----------");
            queue.add(initURL);
            HierarchyGraph testJsoup = new HierarchyGraph();
            testJsoup.buildHirarchyGraph();
        } catch (IOException e) {
            System.out.println("open file error!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("close file error");
                e.printStackTrace();
            }
        }
    }

    public void buildHirarchyGraph() {
        String currentUrl;
        while(!queue.isEmpty()) {
            //to obtain and remove the first element of the queue
            currentUrl = queue.poll();
            Document doc = getDocument(currentUrl);
            Elements elements = parseDucument(doc);
            parseElements(elements, currentUrl);
        }
    }

    public Document getDocument(String url) {
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
    /*
    parse the tag<li> and get the Chinese words
     */
    public void parseElements(Elements elements, String currentURL) {
        if(elements != null) {
            StringBuilder sb;
            //match Chinese words
            Pattern patternChinese = Pattern.compile("[\\u4e00-\\u9fa5]");
            Matcher matcher;
            for (Element e : elements) {
                sb = new StringBuilder(currentURL);
                matcher = patternChinese.matcher(e.toString());
                while (matcher.find()) {
                    sb.append(matcher.group());
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
                }
                // System.out.println(sb.toString());
                queue.add(sb.toString());
            }
        }
    }
}