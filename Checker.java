package ir_5_ss16;

import static ir_5_ss16.IR_5_ss16.sammlung;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Klasse die Dokumente erstellt und diese einer Dokumentensammlung hinzufügt.
 */
public class Checker {

    private enum Status { //Zustandsmenge für einen kleinen "Zustandsautomat" zum Erfassen des Schemas beim Einlesen

        NICHTS, TITEL, INHALT
    }
    static HashSet<String> stWords = new HashSet();

    /**
     * Funktion, die den Pfad zu einer txt-Datei und den Pfad zu einem Ordner für die aufgesplitteten Dokumente erhält
     * und diese anhand des beschriebenen Schemas, in Aufgabe 1, in kleinere
     * Dokumente aufteilt und ihre Speichernamen in einer Dokumentensammlung ablegt.
     *
     * @param intxt Pfad der einzulesenden Datei
     * @param output Pfad in den die Dokumente gespeichert werden sollen
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void einlesen(File intxt, File output) throws FileNotFoundException, IOException {
        Dokument doc;
        String zeile;
        int leerzeilen = 0;
        Status status = Status.NICHTS;
        String titel = "";
        StringBuilder inhalt = new StringBuilder();
        if (intxt.canRead()) {
            fillSet();
            if (!sammlung.exists()) {
                new File(sammlung.getPath()).createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(intxt));
            while (reader.ready()) { // Solange die Datei noch Zeilen zum Lesen besitzt
                zeile = reader.readLine();
                if (zeile.isEmpty()) { // Leerzeilen sind mögliche Indentifikatoren für die Aufteilung
                    leerzeilen++;
                    if (leerzeilen == 3) { // In der nächsten Zeile ist ein Titel zu erwarten
                        if (status == Status.INHALT) { // Vorheriges Dokument abschließen
                            //Abfangen eines Sonderfalls, da Schema nicht ganz einheitlich umgesetzt
                            if (!titel.equals("AESOP'S FABLES (82 Fables)")) {
                                doc = new Dokument(erstelleDocPath(output, titel), titel, inhalt.toString());
                                sammlung.add(doc);
                            }
                            inhalt.delete(0, inhalt.length());
                        }
                        status = Status.TITEL;
                    } else if (status == Status.TITEL && leerzeilen == 2) { // Nach dem Titel folgt der Inhalt des Gedichts
                        status = Status.INHALT;
                    }
                } else {
                    leerzeilen = 0;
                    if (status == Status.TITEL) { // Die erste nicht leere Zeile, nach drei Leerzeilen enthält den Titel
                        titel = zeile.trim();
                    }
                }
                if (status == Status.INHALT) {
                    inhalt.append(zeile).append("\n");
                }
            }
            if (status == Status.INHALT) { // Speichern des aller letzten Dokuments
                doc = new Dokument(erstelleDocPath(output, titel), titel, inhalt.toString().trim());
                sammlung.add(doc);
            }
            reader.close();
        }
    }

    /**
     * Public Methode, um HashSet zu füllen. Falls die Stoppwortliste nicht im data-Ordner ist, wird nach dieser erst gefragt.
     *
     * @throws java.io.FileNotFoundException
     */
    protected static void fillSet() throws FileNotFoundException, IOException {
        File stwList = new File("./data/englischST.txt");
        if (!stwList.exists()) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Öffne Stoppwortliste");
            File stwList2 = chooser.showOpenDialog(new Stage());
            Files.copy(stwList2.toPath(), stwList.toPath());
        }
        BufferedReader reader = new BufferedReader(new FileReader(stwList));
        while (reader.ready()) {
            stWords.add(reader.readLine());
        }
        reader.close();
    }
    
    /**
     * Private Methode, um den Speicherpfad eines Dokuments zu generieren
     *
     * @param path Pfad bis zum Ordner, in dem das Dokument liegen soll
     * @param titel Titel des Dokuments
     *
     * @return Speicherpfad des Dokuments
     */
    private static String erstelleDocPath(File output, String titel) {
        titel = titel.replaceAll("[,;.:?!]", "").toLowerCase().replaceAll(" ", "_");
        File doc = new File(output, titel.concat(".txt"));
        return doc.getPath();
    }
}
