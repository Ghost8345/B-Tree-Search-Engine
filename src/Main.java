import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {

        boolean quit = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to our program please enter 1 to go to our B-Tree driver or enter 2 to go to our Search Engine Driver");
        int program = sc.nextInt();
        switch (program) {
            case 1 -> {
                System.out.println("\nWelcome to our B-Tree program\n");
                System.out.println("Please enter the minimum order of B-Tree (T): ");
                int order = sc.nextInt();
                BTree myBTree = new BTree(order);
                while (!quit){
                    System.out.println("\nHere is a guide to use it: (I-> Insert element in B-Tree, DE-> Delete element in B-Tree, S-> Search for an element in B-Tree, DI -> Display Tree, Q-> Quit Program)\n");
                    String input = sc.next();
                    switch (input.toLowerCase(Locale.ROOT)) {
                        case "i" -> {
                            System.out.println("\nPlease Enter Node Key: ");
                            String key = sc.next();
                            System.out.println("\nPlease Enter Node Value: ");
                            String value = sc.next();
                            myBTree.insert(key, value);
                        }
                        case "de" -> {
                            System.out.println("\nPlease Enter Node Key: ");
                            String key = sc.next();
                            boolean deleted = myBTree.delete(key);
                            System.out.println("Deleted: " + deleted);
                        }
                        case "s" -> {
                            System.out.println("\nPlease Enter Node Key: ");
                            String key = sc.next();
                            if(myBTree.search(key) != null){
                                String value = myBTree.search(key).toString();
                                System.out.println("\nValue of searched node is: " + value);
                            }
                            else{
                                System.out.println("\n Key not found");
                            }

                        }
                        case "di" -> {
                            System.out.println("\nYour Tree is: ");
                            myBTree.display();
                        }
                        case "q" -> {
                            System.out.println("\nBye");
                            quit = true;
                        }
                    }
                }
            }
            case 2 -> {
                SearchEngine mySearchEngine = new SearchEngine();
                System.out.println("Welcome to Our Search Engine.\n");
                while (!quit) {
                    System.out.println("\nHere is a guide to use it: (I-> Index xml file, IM-> Index Multiple xml files, D-> Delete xml file from tree, SW-> search a word in our indexed tree, SMW-> search multiple words, Q-> Quit Program)\n");
                    String input = sc.next();
                    switch (input.toLowerCase(Locale.ROOT)) {
                        case "i" -> {
                            System.out.println("\nPlease Enter File path: ");
                            String filePath = sc.next();
                            mySearchEngine.indexWebPage(filePath);
                            System.out.println("\nIndexed Succesfuly.\nYour Tree: ");
                            mySearchEngine.getBTree().display();
                        }
                        case "im" -> {
                            System.out.println("\nPlease Enter Directory path: ");
                            String directoryPath = sc.next();
                            mySearchEngine.indexDirectory(directoryPath);
                            System.out.println("\nIndexed Succesfuly.\nYour Tree: ");
                            mySearchEngine.getBTree().display();
                        }
                        case "d" -> {
                            System.out.println("\nPlease Enter File path: ");
                            String filePath = sc.next();
                            mySearchEngine.deleteWebPage(filePath);
                            System.out.println("\nDeleted Succesfuly.\nYour Tree: ");
                            mySearchEngine.getBTree().display();
                        }
                        case "sw" -> {
                            System.out.println("\nPlease Enter the word you want to search for: ");
                            String word = sc.next();
                            List<ISearchResult> result = mySearchEngine.searchByWordWithRanking(word);
                            System.out.println("\nYour sorted search results is here: ");
                            for (ISearchResult iSearchResult : result) {
                                System.out.println("Document with id: " + iSearchResult.getId() + " and with rank: " + iSearchResult.getRank());
                            }
                        }
                        case "smw" -> {
                            System.out.println("\nPlease Enter the sentence you want to search for: ");
                            String sentence = sc.next();
                            List<ISearchResult> result = mySearchEngine.searchByMultipleWordWithRanking(sentence);
                            System.out.println("\nYour sorted search results is here: ");
                            for (ISearchResult iSearchResult : result) {
                                System.out.println("Document with id: " + iSearchResult.getId() + " and with rank: " + iSearchResult.getRank());
                            }
                        }
                        case "q" -> {
                            System.out.println("\nBye");
                            quit = true;
                        }
                    }
                }
            }
        }


    }
}

