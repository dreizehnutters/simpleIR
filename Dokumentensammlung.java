package ir_5_ss16;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Containerklasse, die die Dokumente speichert und verwaltet
 *
 * @author Fabian Kopp, Maximilian Mühle
 */
public class Dokumentensammlung {

    private String path = "";
    private File dir;
    public InvertList invList = new InvertList();
    private ArrayList<Dokument> documents = new ArrayList();
    public Vektormodell vModell;

    /**
     * Konsturktor für die Dokumentensammlung
     *
     * @param path Speicherpfad der Sammlung
     *
     * @throws java.io.IOException
     */
    public Dokumentensammlung(String path) throws IOException {
        this.path = path;
        dir = new File(path).getParentFile();
        documents = getAllDocs();
        for (int i = 0; i < documents.size(); i++) {
            documents.get(i).setID(i);
        }
        fillInvList();
        vModell = new Vektormodell(documents);
    }

    public Dokumentensammlung() {

    }

    /**
     * Fügt ein neues Dokument der Sammlung hinzu, falls es nicht bereits enthalten ist.
     *
     * @param doc hinzuzufügendes Dokument
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void add(Dokument doc) throws IOException, FileNotFoundException {
        boolean found = false;
        ArrayList<String> names = this.getAllNames();
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (String name : names) {
            writer.write(name);
            writer.newLine();
            found = found || matchTitel(name, doc.getTitel());
        }
        if (!found) {
            writer.write(new File(doc.getPath()).getName());
            addToInvList(doc, documents.size());
            doc.setID(documents.size());
            documents.add(doc);
        }
        writer.close();
    }

    /**
     * Löscht das angegebene Dokument
     *
     * @param doc zulöschendes Dokument
     *
     * @return True, wenn das Dokument gefunden und gelöscht wurde. Sonst, False.
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    public boolean remove(Dokument doc) throws IOException, FileNotFoundException {
        boolean deleted = false;
        ArrayList<String> names = this.getAllNames();
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (String name : names) {
            if (matchTitel(name, doc.getTitel())) {
                doc.delete();
                deleted = true;
            } else {
                writer.write(name);
                writer.newLine();
            }
        }
        writer.close();
        return deleted;
    }

    /**
     * Methode, um alle Dokumente einer Sammlung zu erhalten.
     *
     * @return Liste aller Dokumente in der Sammlung
     */
    public ArrayList<Dokument> getAll() {
        return documents;
    }

    public ArrayList<Dokument> getAll(HashSet<Integer> ids) {
        ArrayList<Dokument> docs = new ArrayList();
        ids.forEach(id -> docs.add(documents.get(id)));
        return docs;
    }

    private ArrayList<Dokument> getAllDocs() throws IOException {
        ArrayList<Dokument> docs = new ArrayList();
        ArrayList<String> names = getAllNames();
        for (String name : names) {
            if (new File(dir, name).exists()) {
                docs.add(new Dokument(dir.getPath().concat("/").concat(name)));
            }
        }
        return docs;
    }

    /**
     * Methode, um ein einzelnes Dokument der Sammlung zu bekommen.
     *
     * @param title Titel des Dokumentes
     *
     * @return Dokument mit dem Namen, wenn es existiert. Sonst ein Leeres.
     *
     * @throws IOException
     */
    public Dokument get(String title) throws IOException {
        String docname = "";
        boolean gefunden = false;
        BufferedReader reader = new BufferedReader(new FileReader(path));
        while (reader.ready() && !gefunden) {
            docname = reader.readLine();
            gefunden = matchTitel(docname, title);
        }
        reader.close();
        if (!gefunden) {
            docname = "";
        }

        return (new Dokument(new File(dir, docname).getPath()));
    }

    /**
     * Methode, die alle Namen der Sammlung als ArrayList zurück gibt.
     *
     * @return ArrayList mit allen Namen der gespeicherten Dokumente
     *
     * @throws IOException
     */
    public ArrayList<String> getAllNames() throws IOException {
        String name;
        ArrayList<String> names = new ArrayList();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        while (reader.ready()) {
            name = reader.readLine();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }
        reader.close();
        return names;
    }

    public ArrayList<String> getIDs(ArrayList<Dokument> docs) {
        ArrayList<String> ids = new ArrayList();
        for (Dokument doc : docs) {
            ids.add(((Integer) (documents.indexOf(doc) + 1)).toString());
        }
        return ids;
    }

    /**
     * Methode zum Prüfen, ob die Datei der Sammlung existiert
     *
     * @return true, wenn Datei gefunden wurde. false, sonst.
     */
    public boolean exists() {
        return (new File(path)).exists();
    }

    /**
     * Getter-Methode
     *
     * @return Speicherpfad der Dokumentensammlung
     */
    public String getPath() {
        return path;
    }

    /**
     * Setter-Methode
     *
     * @param path neuer Speicherpfad für die Dokumentensammlung
     */
    public void setPath(String path) {
        this.path = path;
        dir = new File(path).getParentFile();
    }

    /**
     * private Methode, die einen Dateinamen mit einem Titel vergleicht
     *
     * @param name Speichername der Datei
     * @param titel Dokumententitel
     *
     * @return true, wenn Dateinname und Titel übereinstimmen. false, sonst.
     */
    private static boolean matchTitel(String name, String titel) {
        String modTitel = titel.replaceAll("[,;.:?!]", "").toLowerCase().replaceAll(" ", "_").concat(".txt");
        return modTitel.equals(name);
    }

    private void fillInvList() {
        invList.clear();
        for (int i = 0; i < documents.size(); i++) {
            addToInvList(documents.get(i), i);
        }
    }

    private void addToInvList(Dokument doc, int id) {
        String[] words = doc.getStoppwortfrei().split(" ");
        for (String word : words) {
            invList.add(word, id);
        }
    }

    private void fillSignatrure() {
        //TODO
    }
}
