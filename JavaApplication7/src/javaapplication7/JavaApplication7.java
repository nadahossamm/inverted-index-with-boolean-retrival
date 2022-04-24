/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication7;

import java.io.*;
import java.util.*;

//=====================================================================
class DictEntry2 {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public ArrayList<Integer> postingList;

    DictEntry2() {
        postingList = new ArrayList<Integer>();
    }
}

//=====================================================================
class Index2 {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry2> index; // THe inverted index
    //--------------------------------------------

    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry2 dd = (DictEntry2) pair.getValue();
            ArrayList<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry2());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }
                printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }

    //--------------------------------------------------------------------------
    ArrayList<Integer> intersect(ArrayList<Integer> pL1, ArrayList<Integer> pL2) {
        ArrayList<Integer> answer = new ArrayList<Integer>();;
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();
        int docId1 = 0, docId2 = 0;
        if (itP1.hasNext()) {
            docId1 = itP1.next();
        }
        if (itP2.hasNext()) {
            docId2 = itP2.next();
        }

        while (itP1.hasNext() && itP2.hasNext()) {

            if (docId1 == docId2) {
                answer.add(docId1);
                docId1 = itP1.next();
                docId2 = itP2.next();
            } else if (docId1 < docId2) {
                if (itP1.hasNext()) {
                    docId1 = itP1.next();
                } else {
                    return answer;
                }

            } else {
                if (itP2.hasNext()) {
                    docId2 = itP2.next();
                } else {
                    return answer;
                }

            }

        }
        if (docId1 == docId2) {
            answer.add(docId1);
        }

        return answer;
    }

    //----------------------------------------------------------------------------  
    ArrayList<Integer> intersect2(ArrayList<Integer> newP1, ArrayList<Integer> newP2) {
        ArrayList<Integer> answer = new ArrayList<Integer>();

        int l1 = newP1.size();
        int l2 = newP2.size();

        if (l1 < l2) {
            for (int i = 0; i < l1; i++) {
                if (newP2.contains(newP1.get(i))) {
                    answer.add(newP1.get(i));
                }
            }
        } else {
            for (int i = 0; i < l2; i++) {
                if (newP1.contains(newP2.get(i))) {
                    answer.add(newP2.get(i));
                }
            }
        }

        return answer;
    }

    //----------------------------------------------------------------------- 
    ArrayList<Integer> OR(ArrayList<Integer> pL1, ArrayList<Integer> pL2) {
        ArrayList<Integer> answer = new ArrayList<Integer>();;
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();
        boolean d1 = false, d2 = false;
        int docId1 = 0, docId2 = 0;

        while (itP1.hasNext() || itP2.hasNext()) {
            if (itP1.hasNext()) {
                docId1 = itP1.next();
                d1 = true;
            }
            if (itP2.hasNext()) {
                docId2 = itP2.next();
                d2 = true;
            }
            if (docId1 == docId2) {
                answer.add(docId2);
            } else if (docId1 < docId2) {
                if (d1 == true) {
                    answer.add(docId1);
                } else if (d2 == true) {
                    answer.add(docId2);
                }
            } else if (docId1 > docId2) {
                if (d2 == true) {
                    answer.add(docId2);
                } else if (d1 == true) {
                    answer.add(docId1);
                }
            }
            d1 = false;
            d2 = false;

        }

        return answer;
    }
    //----------------------------------------------------------------------- 

    ArrayList<Integer> OR2(ArrayList<Integer> newP1, ArrayList<Integer> newP2) {
        ArrayList<Integer> answer = new ArrayList<Integer>();
        for (int i = 0; i < newP1.size(); i++) {
            if (!answer.contains(newP1.get(i))) {
                answer.add(newP1.get(i));
            }

        }
        for (int i = 0; i < newP2.size(); i++) {
            if (!answer.contains(newP2.get(i))) {
                answer.add(newP2.get(i));
            }

        }
        Collections.sort(answer);

        return answer;

    }

    //-----------------------------------------------------------------------   
    public ArrayList<Integer> Not(ArrayList<Integer> newP1) {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        for (int i = 0; i < 27; i++) {
            arr.add(i);
        }
        ArrayList<Integer> answer = new ArrayList<Integer>();
        for (int i = 0; i < arr.size(); i++) {
            if (!newP1.contains(arr.get(i))) {
                answer.add(arr.get(i));
            }
        }
        return answer;
    }

    String[] rearrange(String[] words, int[] freq, int len) {
        boolean sorted = false;
        int temp;
        String sTmp;
        for (int i = 0; i < len; i++) {
            freq[i] = index.get(words[i].toLowerCase()).doc_freq;
        }
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < len - 1; i++) {
                if (freq[i] > freq[i + 1]) {
                    temp = freq[i];
                    sTmp = words[i];
                    freq[i] = freq[i + 1];
                    words[i] = words[i + 1];
                    freq[i + 1] = temp;
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

    //-----------------------------------------------------------------------         
    public ArrayList<Integer> find_04(String phrase) { // any mumber of terms optimized search 

        String[] words = phrase.split(" ");
        ArrayList<Integer> res2 = new ArrayList<Integer>();
        ArrayList<Integer> res1 = new ArrayList<Integer>();
        int len = words.length;
        res1 = index.get(words[0].toLowerCase()).postingList;
        for (int i = 1; i < len - 1; i += 2) {
            String word = words[i];
            if (words[i + 1].equalsIgnoreCase("not")) {
                res2 = Not(index.get(words[i + 2].toLowerCase()).postingList);
                i++;

            } else {
                res2 =index.get(words[i+1].toLowerCase()).postingList;

            }
            if (word.equalsIgnoreCase("and")) {
                res1 = intersect2(res1, res2);

            } else if (word.equalsIgnoreCase("or")) {
                res1 = OR2(res1, res2);
            }
        }
        return res1;

    }

//-----------------------------------------------------------------------
}

//=====================================================================
public class JavaApplication7 {

    public static void main(String args[]) throws IOException {

        String[] files;
        files = new String[]{
            "F:/year4 t 2/ir/Assignments/docs/100.txt",
            "F:/year4 t 2/ir/Assignments/docs/101.txt",
            "F:/year4 t 2/ir/Assignments/docs/102.txt",
            "F:/year4 t 2/ir/Assignments/docs/103.txt",
            "F:/year4 t 2/ir/Assignments/docs/104.txt",
            "F:/year4 t 2/ir/Assignments/docs/105.txt",
            "F:/year4 t 2/ir/Assignments/docs/106.txt",
            "F:/year4 t 2/ir/Assignments/docs/107.txt",
            "F:/year4 t 2/ir/Assignments/docs/108.txt",
            "F:/year4 t 2/ir/Assignments/docs/109.txt",
            "F:/year4 t 2/ir/Assignments/docs/300.txt",
            "F:/year4 t 2/ir/Assignments/docs/302.txt",
            "F:/year4 t 2/ir/Assignments/docs/500.txt",
            "F:/year4 t 2/ir/Assignments/docs/501.txt",
            "F:/year4 t 2/ir/Assignments/docs/502.txt",
            "F:/year4 t 2/ir/Assignments/docs/503.txt",
            "F:/year4 t 2/ir/Assignments/docs/504.txt",
            "F:/year4 t 2/ir/Assignments/docs/505.txt",
            "F:/year4 t 2/ir/Assignments/docs/506.txt",
            "F:/year4 t 2/ir/Assignments/docs/507.txt",
            "F:/year4 t 2/ir/Assignments/docs/508.txt",
            "F:/year4 t 2/ir/Assignments/docs/509.txt",
            "F:/year4 t 2/ir/Assignments/docs/510.txt",
            "F:/year4 t 2/ir/Assignments/docs/511.txt",
            "F:/year4 t 2/ir/Assignments/docs/512.txt",
            "F:/year4 t 2/ir/Assignments/docs/513.txt",
            "F:/year4 t 2/ir/Assignments/docs/514.txt",
            "F:/year4 t 2/ir/Assignments/docs/515.txt",
            "F:/year4 t 2/ir/Assignments/docs/516.txt",
            "F:/year4 t 2/ir/Assignments/docs/517.txt",
            "F:/year4 t 2/ir/Assignments/docs/518.txt",
            "F:/year4 t 2/ir/Assignments/docs/519.txt",
            "F:/year4 t 2/ir/Assignments/docs/520.txt",
            "F:/year4 t 2/ir/Assignments/docs/521.txt",
            "F:/year4 t 2/ir/Assignments/docs/522.txt",
            "F:/year4 t 2/ir/Assignments/docs/523.txt",
            "F:/year4 t 2/ir/Assignments/docs/524.txt",
            "F:/year4 t 2/ir/Assignments/docs/525.txt",
            "F:/year4 t 2/ir/Assignments/docs/526.txt",
            "F:/year4 t 2/ir/Assignments/docs/527.txt",};
        Index2 index = new Index2();

        index.buildIndex(files);
        System.out.println("Phrase 1 ");
        ArrayList<Integer> list = new ArrayList<Integer>(index.find_04("weather and not goal or road"));
        for (int i = 0; i < list.size(); i++) {
            System.out.println( list.get(i)+  "    "+files[list.get(i)]);
        }
        System.out.println("Phrase 2 ");
      list = index.find_04("participants and store or not but");
        for (int i = 0; i < list.size(); i++) {
            System.out.println( list.get(i)+  "    "+files[list.get(i)]);
        }
        System.out.println("Phrase 3 ");
       list = index.find_04("Agile and Not introduction");
         for (int i = 0; i < list.size(); i++) {
            System.out.println( list.get(i)+  "    "+files[list.get(i)]);
        }
    }
}
