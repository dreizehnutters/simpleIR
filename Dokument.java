package ir_5_ss16;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Klasse zur Repräsentation der Dokumente
 *
 * @author Fabian Kopp, Maximilian Mühle
 */
public class Dokument {

    private String path = "";
    private String titel = "";
    private String inhalt = "";
    private String modInhalt = "";
    private String stoppwortfrei = "";
    private String reduziert = " ";
    private Integer id = -1;
    protected ArrayList<Signature> docSig = new ArrayList<>();

    public enum Anfrage {

        NORMAL, STOPP, REDUZ
    }

    /**
     * Konstruktor, wenn das Dokument neu erstellt wird
     *
     * @param path Speicherpfad des Dokuments
     * @param titel Titel des Dokuments
     * @param inhalt normaler Text des Dokuments
     *
     * @throws IOException
     */
    public Dokument(String path, String titel, String inhalt) throws IOException {
        this.path = path;
        this.titel = titel;
        this.inhalt = inhalt;
        modInhalt = (vereinfache(inhalt)); //vereinfache(titel).concat(" ").concat

        eliminieren();

        reduzieren();

        this.safe();
    }

    /**
     * Konsturktor, um ein existierendes Dokument zu erhalten. Sonst, alle Strings leer.
     *
     * @param docpath Speicherpfad des Dokuments
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Dokument(String docpath) throws FileNotFoundException, IOException {
        if (new File(docpath).exists()) {
            StringBuilder inhaltBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(docpath));
            path = docpath;
            titel = reader.readLine();
            modInhalt = reader.readLine();
            stoppwortfrei = reader.readLine();
            reduziert = reader.readLine();
            while (reader.ready()) {
                inhaltBuilder.append(reader.readLine()).append("\n");
            }
            inhalt = inhaltBuilder.toString().trim();
            reader.close();
        }
    }

    /**
     * Methode, um ein Dokument mit den aktuellen Werten zu überschreiben bzw abzuspeichern.
     *
     * @throws IOException
     */
    public void safe() throws IOException {

        File doc = new File(path);
        if (!doc.exists()) {
            doc.createNewFile();
        }
        String[] lines = inhalt.split("\n");
        BufferedWriter writer = new BufferedWriter(new FileWriter(doc));
        writer.write(titel);
        writer.newLine();
        writer.write(modInhalt);
        writer.newLine();
        writer.write(stoppwortfrei);
        writer.newLine();
        writer.write(reduziert);
        writer.newLine();
        for (int i = 0; i < (lines.length - 1); i++) {
            writer.write(lines[i]);
            writer.newLine();
        }
        writer.write(lines[lines.length - 1]);
        writer.close();

    }

    /**
     * Methode zum Löschen des Dokuments
     *
     * @return true, wenn Löschung erfolgreich. false, sonst.
     *
     * @throws FileNotFoundException
     */
    public boolean delete() throws FileNotFoundException {
        return (new File(path)).delete();
    }

    /**
     * Methode zur Suche eines Suchterms im Dokument
     *
     * @param key einzelner Suchterm
     * @param anfrageTyp Angabe in welchem Text gesucht werden soll
     *
     * @return true, wenn Suchterm im Text gefunden wird. false, sonst.
     */
    public boolean contains(String key, Anfrage anfrageTyp) {
        if ((anfrageTyp == Anfrage.NORMAL && modInhalt.isEmpty())
                || (anfrageTyp == Anfrage.STOPP && stoppwortfrei.isEmpty())
                || (anfrageTyp == Anfrage.REDUZ && reduziert.isEmpty())) {
            return false;
        }
        switch (anfrageTyp) {
            case NORMAL:
                return modInhalt.contains(key);
            case STOPP:
                return stoppwortfrei.contains(key);
            case REDUZ:
                return reduziert.contains(new PorterStemmer().stemThis(key));
            default:
                return false;
        }

    }

    /**
     * Methode, um die Stoppworteliminierung aufzurufen.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void eliminieren() throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();

        String[] pairs = this.modInhalt.split(" ");
        for (String pair : pairs) {
            if (!Checker.stWords.contains(pair)) {
                sb.append(pair.toLowerCase()).append(" ");
            }
        }
        this.stoppwortfrei = sb.toString().trim();
    }

    private static String vereinfache(String str) {
        return str.toLowerCase().replaceAll("[.:,;?!\"]", "").replace("\n", " ");
    }

    /**
     * Methode, um die Stammwortreduktion aufzurufen. (Schema: reduzierter,Titel;wort,wort,wort,...)
     */
    public void reduzieren() {
        StringBuilder sb = new StringBuilder();

        PorterStemmer ps = new PorterStemmer();

        sb.append(ps.stemThis(stoppwortfrei));

        this.reduziert = sb.toString().trim();
    }

    /**
     * Methode, um ein Text auf sein Signaturen abzubilden
     */
    public void signatuieren() {
        //System.out.println("ID "+this.docId);
        StringBuilder sb = new StringBuilder("");
        //4 word counter
        int c = 0;
        ArrayList<String> words = new ArrayList<String>();
        words.addAll(Arrays.asList(modInhalt.split(" ")));
        //Fallunterscheidung da nicht jeder text gleiche viele wÃ¶rter hat
        switch (countWords(modInhalt) % 4) {
            case 0: {
                //System.out.println("sig 0 " + this.id);
                for (String s : words) {
                    sb.append(s);
                    sb.append(" ");
                    c++;
                    if (c == 4) {
                        c = 0;
                        BitSet blocksig = new BitSet(64);
                        for (String word : sb.toString().split(" ")) {
                            blocksig.or(new Signature(word, this.id).getBlockSig());
                        }
                        docSig.add(new Signature(blocksig, getID()));
                        sb.setLength(0);
                    }
                }
                break;
            }
            case 1: {
                //System.out.println("sig 1 " + this.id);
                for (int i = 0; i < (words.size() - 1); i++) {
                    sb.append(words.get(i));
                    c++;
                    if (c == 4) {
                        c = 0;
                        BitSet blocksig = new BitSet(64);
                        for (String word : sb.toString().split(" ")) {
                            blocksig.or(new Signature(word, this.id).getBlockSig());
                        }
                        docSig.add(new Signature(blocksig, this.id));
                        sb.setLength(0);
                    }
                }
                docSig.add(new Signature(words.get(words.size() - 1), this.id));
                break;
            }
            case 2: {
                //System.out.println("sig 2 " + this.id);
                for (int i = 0; i < (words.size() - 2); i++) {
                    sb.append(words.get(i));
                    c++;
                    if (c == 4) {
                        c = 0;
                        BitSet blocksig = new BitSet(64);
                        for (String word : sb.toString().split(" ")) {
                            blocksig.or(new Signature(word, this.id).getBlockSig());
                        }
                        docSig.add(new Signature(blocksig, this.id));
                        sb.setLength(0);
                    }
                }
                sb.setLength(0);
                sb.append(words.get(words.size() - 1));
                sb.append(" ");
                sb.append(words.get(words.size() - 2));
                BitSet blocksig = new BitSet(64);
                for (String word : sb.toString().split(" ")) {
                    blocksig.or(new Signature(word, this.id).getBlockSig());
                }
                docSig.add(new Signature(blocksig, this.id));
                break;
            }
            case 3: {
                //System.out.println("sig 3 " + this.id);
                for (int i = 0; i < (words.size() - 3); i++) {
                    sb.append(words.get(i));
                    c++;
                    if (c == 4) {
                        c = 0;
                        BitSet blocksig = new BitSet(64);
                        for (String word : sb.toString().split(" ")) {
                            blocksig.or(new Signature(word, this.id).getBlockSig());
                        }
                        docSig.add(new Signature(blocksig, this.id));

                        sb.setLength(0);
                    }
                }
                //Add remainiung words
                sb.setLength(0);
                sb.append(words.get(words.size() - 1));
                sb.append(" ");
                sb.append(words.get(words.size() - 2));
                sb.append(" ");
                sb.append(words.get(words.size() - 3));
                BitSet blocksig = new BitSet(64);
                for (String word : sb.toString().split(" ")) {
                    blocksig.or(new Signature(word, this.id).getBlockSig());
                }
                docSig.add(new Signature(blocksig, this.id));
                break;
            }
        }
    }

    public int countWords(String value) {
        // Split on non-word chars.
        String[] words = value.split("\\W+");
        // Handle an empty string.
        if (words.length == 1 && words[0].length() == 0) {
            return 0;
        }
        // Return array length.
        return words.length;
    }

    //TODO get pos(offset in text)
    public Integer getPos(String key) {
        int i = 0;
        return 0;
    }

    //Getter-Methoden
    public String getPath() {
        return path;
    }

    public String getTitel() {
        return titel;
    }

    public String getInhalt() {
        return inhalt;
    }

    public String getStoppwortfrei() {
        return stoppwortfrei;
    }

    public String getReduziert() {
        return reduziert;
    }

    public Integer getID() {
        return id;
    }

    public ArrayList<Signature> getDocSig() {
        return docSig;
    }

    //Setter-Methoden
    public void setPath(String path) {
        this.path = path;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setInhalt(String inhalt) {
        this.inhalt = inhalt;
    }

    public void setStoppwortfrei(String stoppwortfrei) {
        this.stoppwortfrei = stoppwortfrei;
    }

    public void setReduziert(String reduziert) {
        this.reduziert = reduziert;
    }

    public void setID(int id) {
        this.id = id;
        signatuieren();
    }

    public void setDocSig(ArrayList<Signature> docSig) {
        this.docSig = docSig;
    }

    /**
     * @return Titel des Dokuments
     */
    @Override
    public String toString() {
        return titel;
    }

    public boolean equals(Dokument doc) {
        return titel.equals(doc.getTitel());
    }
}
