/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir_5_ss16;

import static java.lang.Integer.max;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author Maximilian
 */
public class Vektormodell {

    //                    <Wort, Liste von Dokument-IDs mit ihren Trennschärfewerten>
    private static HashMap<String, ArrayList<Tupel<Integer, Double>>> wdkList = new HashMap();
    //                    <Wort, Anzahl der Dokumente, in denen das Wort auftritt>
    private static HashMap<String, Integer> nkList = new HashMap();
    //                    <Dokument-ID, Worte und ihre Anzahl des Auftretens>
    private static HashMap<Integer, HashMap<String, Integer>> tfdksList = new HashMap();
    private int size = 0;

    public Vektormodell(ArrayList<Dokument> docs) {
        size = 0;
        for (Dokument doc : docs) {
            add(doc);
            size++;
        }
        calculateWdks();
    }

    public void add(Dokument doc) {
        HashMap<String, Integer> termCounter = new HashMap();
        ArrayList<Tupel<Integer, Double>> wdks;
        String[] words = doc.getStoppwortfrei().split(" ");
        Tupel<Integer, Double> neu = new Tupel(doc.getID(), 0);
        for (String word : words) {
            termCounter.put(word, termCounter.getOrDefault(word, 0) + 1);
        }
        tfdksList.put(doc.getID(), termCounter);
        for (String key : termCounter.keySet()) {
            wdks = wdkList.getOrDefault(key, new ArrayList());
            wdks.add(neu);
            wdkList.put(key, wdks);
            nkList.put(key, nkList.getOrDefault(key, 0) + 1);
        }
    }

    public ArrayList<Tupel<Integer, Double>> get(String key) {
        return wdkList.getOrDefault(key, new ArrayList());
    }

    public void calculateWdks() {
        int j;
        int tfdk;
        double i;
        double idf;
        double sum;
        double wdk;
        HashMap<Integer, Double> divs = new HashMap();
        HashMap<String, Integer> tfdks;
        for (int id : tfdksList.keySet()) {
            sum = 0;
            tfdks = tfdksList.getOrDefault(id, new HashMap());
            //Berechnung der Summe, die unter dem Bruchstrich steht.
            for (String k : tfdks.keySet()) {
                i = (tfdks.getOrDefault(k, 1) * log(calcIdf(k)));
                sum += i * i;
            }
            //Merken des Wertes unter dem Bruchstrich
            divs.put(id, sqrt(sum));
        }
        for (String key : wdkList.keySet()) {
            idf = calcIdf(key);
            wdk = 0;
            for (Tupel<Integer, Double> t : wdkList.get(key)) {
                j = t.getFst();
                tfdk = tfdksList.getOrDefault(j, new HashMap<String, Integer>()).getOrDefault(key, 0);
                wdk = (tfdk * log(idf)) / divs.get(j);
                t.setSnd(wdk);
            }
            sort(wdkList.get(key));
        }
    }

    public HashMap<String, Double> calcVector(String[] keys) {
        int i = 0;
        int maxTf = 0;
        double wdq;
        HashMap<String, Integer> counter = new HashMap();
        HashMap<String, Double> wdqs = new HashMap();
        for (String key : keys) {
            i = counter.getOrDefault(key, 0) + 1;
            counter.put(key, i);
            if (i > maxTf) {
                maxTf = i;
            }
        }
        for (String k : counter.keySet()) {
            wdq = 0;
            i = counter.get(k);
            if (i > 0) {
                wdq = (0.5 + ((0.5 * i) / maxTf)) * calcIdf(k);
            }
            wdqs.put(k, wdq);
        }
        return wdqs;
    }

    private double calcIdf(String key) {
        int n = size;
        return (n / max(nkList.getOrDefault(key, 1), 1));
    }

    public static void sort(ArrayList<Tupel<Integer, Double>> list) {
        Collections.sort(list, new Comparator<Tupel<Integer, Double>>() {
            @Override
            public int compare(Tupel<Integer, Double> t, Tupel<Integer, Double> t1) {
                return (-1) * Double.compare(t.getSnd(), t1.getSnd());
            }
        });
    }
}
