/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir_5_ss16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InvertList {

    private static HashMap<String, HashSet<Integer>> invList = new HashMap();

    public InvertList() {
    }

    public void add(String key, Integer id) {
        HashSet<Integer> docs;
        docs = this.get(key);
        docs.add(id);
        invList.put(key, docs);
    }

    public HashSet<Integer> get(String key) {
        if (key.startsWith("!")) {
            key = key.substring(1);
            HashSet docs = new HashSet();
            for (String k : invList.keySet()) {
                docs.addAll(get(k));
            }
            docs.removeAll(get(key));
            return docs;
        } else {
            return invList.getOrDefault(key, new HashSet());
        }
    }

    public void clear() {
        invList.clear();
    }

}
