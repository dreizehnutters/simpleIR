package ir_5_ss16;

import static ir_5_ss16.IR_5_ss16.sammlung;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Klasse für Such- und Bewertungsmethoden
 *
 * @author Fabian Kopp, Maximilian Mühle
 */
public class Retrieval {

    public enum Konzept {

        BOOL, VEKTOR
    }

    public enum Implementierung {

        NORMAL, SIGNATUR, INVERL
    }

    public enum Linking {

        AND, OR, XOR
    }

    /**
     * Funktion zur linearen Suche in allen Dokumenten. Dabei werden alle Suchterme, die mit Leertaste von einander getrennt
     * werden,
     * mit einer UND-Verknüpfung in den Dokumenten gesucht.
     *
     * @param anfrage Suchterme
     * @param typ Angabe, welchen Text zu Suche verwendet werden soll.
     * @param link Angabe, wie verschiedene Suchterme verküpft werden sollen.
     *
     * @return ArrayList von Dokumenten, die Suchterme enthalten
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static ArrayList<Dokument> linSearch(String anfrage, Dokument.Anfrage typ, Linking link) throws FileNotFoundException,
            IOException {
        ArrayList<Dokument> docs = new ArrayList();
        if (link != Linking.OR) {
            docs.addAll(sammlung.getAll());
        }
        String[] keys = anfrage.trim().split(" ");
        for (String key : keys) {
            if (!key.isEmpty()) {
                switch (link) {
                    case AND:
                        //Entfernt alle Dokumente, die Wörter, die mit dem Suchterm beginnen, nicht enthalten
                        docs.removeIf(doc -> !doc.contains(" ".concat(key.trim().toLowerCase()), typ));
                        break;
                    case OR:
                        // Fügt der Rückgabeliste fehlende Dokumente für alle Suchterme hinzu
                        ArrayList<Dokument> docs2 = new ArrayList();
                        docs2.addAll(sammlung.getAll());
                        docs2.removeIf(doc -> !doc.contains(" ".concat(key.trim().toLowerCase()), typ));
                        for (Dokument doc : docs2) {
                            if (!docs.contains(doc)) {
                                docs.add(doc);
                            }
                        }
                        break;
                    case XOR:
                        //Entfernt alle Dokumente, die Suchterme enthalten
                        docs.removeIf(doc -> doc.contains(" ".concat(key.trim().toLowerCase()), typ));
                        break;
                }
            }
        }
        return docs;
    }

    /**
     *
     * @param anfrage String, der die Suchterme enthält
     * @param link Angabe, wie mehrere Suchterme behandelt werden sollen
     *
     * @return ArrayList mit vermutlich relevanten Dokumenten
     *
     * @throws java.io.IOException
     */
    public static ArrayList<Dokument> invSearch(String anfrage, Linking link) throws IOException {
        HashSet docIDs = new HashSet();
        if (link != Linking.OR) {
            for (int i = 0; i < sammlung.getAll().size(); i++) {
                docIDs.add(i);
            }
        }
        String[] keys = anfrage.trim().split(" ");
        //PorterStemmer ps = new PorterStemmer();
        for (String key : keys) {
            //key = ps.stemThis(key);
            switch (link) {
                case AND:
                    //Behält nur Dokumenten IDs, die auch in der hinterlegten Liste zum Suchterm vorkommen
                    docIDs.retainAll(sammlung.invList.get(key));
                    break;
                case OR:
                    //Fügt hinterlegte IDs hinzu
                    docIDs.addAll(sammlung.invList.get(key));
                    break;
                case XOR:
                    //Entfernt alle IDs, die in den Listen vorkommen
                    docIDs.removeAll(sammlung.invList.get(key));
                    break;
            }
        }
        return sammlung.getAll(docIDs);
    }

    public static ArrayList<Dokument> sigSearch(String anfrage, Linking link) throws IOException {
        HashSet drops = new HashSet();
        ArrayList<BitSet> anfrageS = new ArrayList<>();
        for (String s : anfrage.trim().split(" ")) {
            anfrageS.add(new Signature(s, -1).getBlockSig());
        }
        switch (link) {
            case AND: {
                for (Dokument doc : sammlung.getAll()) {
                    ArrayList<Signature> sigs = doc.getDocSig();
                    for (Signature sig : sigs) {
                        BitSet bs = sig.getBlockSig();
                        for (BitSet anfS : anfrageS) {
                            bs.and(anfS);
                            if (anfS.equals(bs)) {
                                //retainAll
                                drops.add(sig.getDocId());
                            }
                        }
                    }
                }
                break;
            }

            case OR: {
                for (Dokument doc : sammlung.getAll()) {
                    ArrayList<Signature> sigs = doc.getDocSig();
                    for (Signature sig : sigs) {
                        BitSet bs = sig.getBlockSig();
                        for (BitSet anfS : anfrageS) {
                            bs.and(anfS);
                            if (anfS.equals(bs)) {
                                //addAll
                                drops.add(sig.getDocId());
                            }
                        }
                    }
                }
                break;
            }
            case XOR: {
                for (int i = 0; i < sammlung.getAll().size(); i++) {
                    drops.add(i);
                }
                for (Dokument doc : sammlung.getAll()) {
                    ArrayList<Signature> sigs = doc.getDocSig();
                    for (Signature sig : sigs) {
                        BitSet bs = sig.getBlockSig();
                        for (BitSet anfS : anfrageS) {
                            bs.and(anfS);
                            if (anfS.equals(bs)) {
                                //reamoveAll
                                drops.remove(sig.getDocId());
                            }
                        }
                    }
                }
                break;
            }
            default:
                return null;
        }
        return checkFalseDrops(drops, anfrage, link);
    }

    private static ArrayList<Dokument> checkFalseDrops(HashSet drops, String anfrage, Linking link) throws IOException {
        ArrayList<Dokument> docs = new ArrayList();
        if (link != Linking.OR) {
            docs.addAll(sammlung.getAll(drops));
        }
        String[] keys = anfrage.trim().split(" ");
        for (String key : keys) {
            if (!key.isEmpty()) {
                switch (link) {
                    case AND:
                        //Entfernt alle Dokumente, die WÃ¶rter, die mit dem Suchterm beginnen, nicht enthalten
                        docs.removeIf(doc -> !doc.contains(" ".concat(key.trim().toLowerCase()), Dokument.Anfrage.STOPP));
                        break;
                    case OR:
                        // FÃ¼gt der RÃ¼ckgabeliste fehlende Dokumente fÃ¼r alle Suchterme hinzu
                        ArrayList<Dokument> docs2 = new ArrayList();
                        docs2.addAll(sammlung.getAll());
                        docs2.removeIf(doc -> !doc.contains(" ".concat(key.trim().toLowerCase()), Dokument.Anfrage.STOPP));
                        for (Dokument doc : docs2) {
                            if (!docs.contains(doc)) {
                                docs.add(doc);
                            }
                        }
                        break;
                    case XOR:
                        //Entfernt alle Dokumente, die Suchterme enthalten
                        docs.removeIf(doc -> doc.contains(" ".concat(key.trim().toLowerCase()), Dokument.Anfrage.STOPP));
                        break;
                }
            }
        }
        return docs;
    }

    public static ArrayList<Dokument> vektorSearch(String anfrage, Linking link) {
        boolean found;
        double wqk;
        ArrayList<Tupel<Integer, Double>> docs = new ArrayList();
        ArrayList<Tupel<Integer, Double>> getDocs = new ArrayList();
        ArrayList<Integer> toRemove = new ArrayList();
        HashSet<Integer> docIDs = new HashSet();
        String[] keys = anfrage.trim().split(" ");
        HashMap<String, Double> anfrageVektor = sammlung.vModell.calcVector(keys);
        if (link != Linking.OR) {
            for (int i = 0; i < sammlung.getAll().size(); i++) {
                docs.add(new Tupel(i, 0.0));
            }
        }
        for (String key : keys) {
            wqk = anfrageVektor.get(key);
            getDocs = sammlung.vModell.get(key);
            switch (link) {
                case AND:
                    for (Tupel<Integer, Double> s : docs) {
                        found = false;
                        for (Tupel<Integer, Double> t : getDocs) {
                            if (found = t.getFst().equals(s.getFst())) {
                                //Berechnung der Dokumenten Relevanz
                                s.setSnd(s.getSnd() + (t.getSnd() * wqk));
                                break;
                            }
                        }
                        if (!found) { //Merkt sich Dokumente, die fälschlicher Weise drin enthalten sind
                            toRemove.add(s.getFst());
                        }
                    }
                    break;
                case OR:
                    for (Tupel<Integer, Double> t : getDocs) {
                        found = false;
                        for (Tupel<Integer, Double> s : docs) {
                            if (found = t.getFst().equals(s.getFst())) {
                                //Berechnung der Dokumenten Relevanz
                                s.setSnd(s.getSnd() + (t.getSnd() * wqk));
                                break;
                            }
                        }
                        if (!found) {
                            docs.add(t);
                        }
                    }
                    break;
                case XOR:
                    for (Tupel<Integer, Double> t : getDocs) {
                        docs.removeIf(s -> s.getFst().equals(t.getFst()));
                    }
                    break;
            }
        }
        docs.removeIf(s -> s.getSnd().equals(0.0));
        Vektormodell.sort(docs);
        for (Tupel<Integer, Double> t : docs) {
            docIDs.add(t.getFst());
        }
        docIDs.removeAll(toRemove);
        return sammlung.getAll(docIDs);
    }
}
