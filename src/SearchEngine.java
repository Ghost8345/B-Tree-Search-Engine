import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.util.*;

import javax.xml.parsers.*;
import java.io.*;

public class SearchEngine implements ISearchEngine{

    private BTree indexedGlobalTree = new BTree(8);
    List<ISearchResult> search= new ArrayList<>();


    @Override
    public void indexWebPage(String filePath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            NodeList nodeList = document.getElementsByTagName("doc");
            int nodeListLength = nodeList.getLength();
            for(int i = 0; i < nodeListLength; i++){
                Node docNode = nodeList.item(i);
                Element docElement = (Element) docNode;
                this.indexedGlobalTree.insert(docElement.getAttribute("id"), docElement.getTextContent());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void indexDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        FileFilter xmlFilter = new FileFilter() {

            public boolean accept(File f)
            {
                return (f.getName().endsWith("xml") || f.getName().endsWith("XML")) ;
            }
        };
        File[] listOfXmlFiles = directory.listFiles(xmlFilter);
        if(listOfXmlFiles.length == 0){
            System.out.println("There is no Xml files in this directory.");
            return;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        for (int i = 0; i < listOfXmlFiles.length; i++) {
            try {
                builder = factory.newDocumentBuilder();
                Document document = builder.parse(listOfXmlFiles[i]);
                NodeList nodeList = document.getElementsByTagName("doc");
                int nodeListLength = nodeList.getLength();
                for(int j = 0; j < nodeListLength; j++){
                    Node docNode = nodeList.item(j);
                    Element docElement = (Element) docNode;
                    this.indexedGlobalTree.insert(docElement.getAttribute("id"), docElement.getTextContent());
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void deleteWebPage(String filePath) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            NodeList nodeList = document.getElementsByTagName("doc");
            int nodeListLength = nodeList.getLength();
            for(int i = 0; i < nodeListLength; i++){
                Node docNode = nodeList.item(i);
                Element docElement = (Element) docNode;
                this.indexedGlobalTree.delete(docElement.getAttribute("id"));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        search.clear();
        traverse_btree((BTreeNode) indexedGlobalTree.getRoot(),word);
        Collections.sort(search, new Comparator<ISearchResult>() {
            public int compare(ISearchResult p1, ISearchResult p2) {
                return p2.getRank() - p1.getRank();
            }
        });
        return search;
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        search.clear();
        traverse_btree((BTreeNode) indexedGlobalTree.getRoot(),sentence);
        Collections.sort(search, new Comparator<ISearchResult>() {
            public int compare(ISearchResult p1, ISearchResult p2) {
                return p2.getRank() - p1.getRank();
            }
        });
        return search;
    }

    public BTree getBTree(){
        return this.indexedGlobalTree;
    }

    private void traverse_btree(BTreeNode x,String word) {
        int rank;
        assert (x == null);
        for (int i = 0; i < x.getNumOfKeys(); i++) {
            rank = Count_Occurrences(word, x.getValue(i).toString());
            if (rank != 0) {
                SearchResult I = new SearchResult(x.getKey(i).toString(), rank);
                search.add(I);
            }
        }
        if (!x.isLeaf()) {
            for (int i = 0; i < x.getNumOfKeys() + 1; i++) {
                traverse_btree(x.getChild(i), word);
            }
        }
    }


    private static int Count_Occurrences(String searchString, String textContext){
        String[] word = searchString.split(" ");
        int[] counts = new int[word.length];
        for (int i = 0; i < word.length; i++){
            int count = 0;
            int index_from = 0;
            while ((index_from = textContext.indexOf(word[i], index_from)) != -1 ){
                count++;
                index_from++;
            }
            counts[i] = count;
        }
        Arrays.sort(counts);
        return counts[0];
    }

}
