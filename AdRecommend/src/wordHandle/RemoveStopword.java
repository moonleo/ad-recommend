package wordHandle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RemoveStopword {

    public Resources resources = null;

    public RemoveStopword() {
        resources = new Resources();
    }

    /*public List remove() {
        List newWordList = new ArrayList();
        List newDocumentList = new ArrayList();
        List document_list = null;
        List stopwords = null;
        try {
            document_list = resources.getDocumentList();
            stopwords = resources.getStopwords();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < document_list.size(); i++) {
            for (int j = 0; j < ((List)document_list.get(i)).size(); j++) {
                System.out.print(((List) document_list.get(i)).get(j) + "\\");
            }
            System.out.println();
        }
        *//*System.out.println("stopwords:");
        for (int i = 0; i < stopwords.size(); i++) {
            System.out.print(stopwords.get(i) + "\t");
            if(i % 10 == 0)
                System.out.println();
        }*//*


        *//*for (int i = 0; i < document_list.size(); i++) {
            newWordList = remove((List)document_list.get(i), stopwords);
            newDocumentList.add(newWordList);
        }
        System.out.println("document_list:");
        for (int i = 0; i < newDocumentList.size(); i++) {
            List wordList = (List)newDocumentList.get(i);
            for (int j = 0; j < wordList.size(); j++) {
                System.out.print(wordList.get(j) + "\t");
            }
            System.out.println();
        }*//*
        return newDocumentList;
    }*/

}
