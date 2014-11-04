package wordHandle;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import db.util.MongoDBUtil;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 2014/10/30.
 */
public class Resources {
    public static final String TABLE = "test";
    public static final String TABLE_COLUMN = "title";
    public static final String FILE_NAME = "";//"src/baidu.dic";

    public List<List<String>> documentList = new ArrayList<List<String>>();

    public List getDocumentList() throws IOException {
        DB db= MongoDBUtil.getConn();
        DBCollection collection = db.getCollection(TABLE);
        DBCursor cursor = collection.find();
        List<String> wordList = null;
        while(cursor.hasNext()) {
            wordList = new ArrayList<String>();
            Object titles = cursor.next().get(TABLE_COLUMN);
            if (titles == null)
                continue;
            StringReader sr = new StringReader(titles.toString());
            IKSegmenter ms = new IKSegmenter(sr, true);
            Lexeme ml = ms.next();
            while (ml != null) {
                wordList.add(ml.getLexemeText());
                ml = ms.next();
            }
            if(wordList.size() > 0)
                documentList.add(wordList);
        }
        return documentList;
    }

    public static void main(String[] args) throws IOException {
        Resources resources = new Resources();
        List list = resources.getDocumentList();
        for (int i = 0; i < list.size(); i++) {
            List list2 = (List)list.get(i);
            for (int j = 0; j < list2.size(); j++) {
                System.out.print(list2.get(j) + "\\");
            }
            System.out.println();
        }
    }

    /*public List<String> getStopwords() {
        List<String> list = new ArrayList<String>();
        File file = new File(FILE_NAME);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
            String line = "";
            while((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("file:"+FILE_NAME+" not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("can't read file:"+FILE_NAME);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
    }*/
}
