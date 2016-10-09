package ir_5_ss16;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author Fabian Kopp
 */
public final class PandRCalculator {

    private double precision;
    private double recall;
    //Sammelliste für die DokumentenID-Arrays der Suchbegriffe
    public ArrayList<HashSet> mm = new ArrayList<>();
    //Namen aller Suchterme
    public ArrayList<String> names = new ArrayList<>();

    public PandRCalculator() {
        this.recall = -1;
        this.precision = -1;
    }

    /**
     * Konstruktor
     *
     * @param data Input Dokument (ground_truth.txt)
     * @throws java.io.IOException
     */
    public PandRCalculator(File data) throws IOException {
        this.recall = 0;
        this.precision = 0;
        String zeile; //Schema: Suchterm - Dokumenten-IDs relevanter Dokumente
        BufferedReader reader = new BufferedReader(new FileReader(data.getPath()));
        while (reader.ready()) { // Solange die Datei noch Zeilen zum Lesen besitzt
            zeile = reader.readLine();
            String[] split = zeile.split("-");
            HashSet docIDs = new HashSet();
            String name = split[0].trim();
            names.add(name);
            if (name.equals("beast") || name.equals("animal") || name.equals("fox")
                    || name.equals("hunters") || name.equals("man") || name.equals("seeing")) {
                docIDs.addAll(Arrays.asList(split[1].trim().split(", ")));
            } else {
                docIDs.clear();
            }
            mm.add(docIDs);
        }
        reader.close();
    }

    /**
     * Berechnung von Precison und Recall
     *
     * @param input Suchterm
     * @param ergebnis List der DokumentenIDs der Ergebnisse
     * @param link
     */
    public void calculatePandR(String input, ArrayList<String> ergebnis, Retrieval.Linking link) {
        HashSet relGesamt = new HashSet();
        if (link != Retrieval.Linking.OR) {
            for (Integer j = 0; j < IR_5_ss16.sammlung.getAll().size(); j++) {
                relGesamt.add(j.toString());
            }
        }
        for (String term : input.split(" ")) {
            int i = getIndex(term);
            if (i >= 0 && !mm.isEmpty()) {
                switch (link) {
                    case AND:
                        relGesamt.retainAll(mm.get(i));
                        break;
                    case OR:
                        relGesamt.addAll(mm.get(i)); //Array mit allen relevanten DokumentenIDs
                        break;
                    case XOR:
                        relGesamt.removeAll(mm.get(i));
                        break;
                }
            } else {
                precision = -1;
                recall = -1;
                return;
            }
        }
        if (relGesamt.size() > 0 && ergebnis.size() > 0) {
            double hits = 0;
            for (String docID : ergebnis) { //Zählen der Treffer
                if (relGesamt.contains(docID)) {
                    hits++;
                }
            }
            double noise = ergebnis.size() - hits; // Anzahl von fälschlich als relevant erkannten Dokumente
            double misses = relGesamt.size() - hits; // Anzahl von verpassten relevanten Dokumenten

            precision = hits / (hits + noise);
            recall = hits / (hits + misses);
        } else {
            this.recall = -1;
            this.precision = -1;
        }
    }

    private int getIndex(String input) {
        switch (input) {
            case "beast":
                return 0;
            case "fox":
                return 1;
            case "man":
                return 2;
            case "seeing":
                return 3;
            case "animal":
                return 4;
            case "hunters":
                return 5;
            default:
                return -1;
        }

    }

    public double getP() {
        return this.precision;
    }

    public double getR() {
        return this.recall;
    }

    public ArrayList<HashSet> getMm() {
        return mm;
    }

    public void setMm(ArrayList<HashSet> mm) {
        this.mm = mm;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

}
