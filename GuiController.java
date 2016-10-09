package ir_5_ss16;

import static ir_5_ss16.IR_5_ss16.sammlung;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Fabian Kopp, Maximilian Mühle
 */
public class GuiController implements Initializable {

    Dokument.Anfrage anfrageTyp = Dokument.Anfrage.NORMAL;
    Retrieval.Konzept suchType = Retrieval.Konzept.BOOL;
    Retrieval.Linking linking = Retrieval.Linking.AND;
    Retrieval.Implementierung imp = Retrieval.Implementierung.NORMAL;
    PandRCalculator pr = new PandRCalculator();

    @FXML
    Text pValue;
    @FXML
    Text rValue;

    @FXML
    TextField searchField;

    @FXML
    ListView<Dokument> listView;

    @FXML
    SplitMenuButton searchBtn;

    @FXML
    RadioButton rbNormal;
    @FXML
    RadioButton rbOhneST;
    @FXML
    RadioButton rbStamm;

    @FXML
    RadioButton rbNormalI;
    @FXML
    RadioButton rbSigI;
    @FXML
    RadioButton rbInvListI;

    @FXML
    RadioMenuItem rmiLin;
    @FXML
    RadioMenuItem rmiVek;

    @FXML
    RadioButton and;
    @FXML
    RadioButton or;
    @FXML
    RadioButton neg;

    @FXML
    Label searchTime;

    @FXML
    /**
     * Initialisiert das Einlesen der Dokumentenkollektion bzw Erstellung der Dokumente und der Dokumentsammlung
     */

    protected void readData() {
        Stage stage = new Stage();
        DialogPane text = new DialogPane();
        File data = new File("./data/");
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Öffne Dokumentenkollektion");
        try {
            File input = chooser.showOpenDialog(new Stage());
            Checker.einlesen(input, data);
            listView.setItems(FXCollections.observableArrayList(sammlung.getAll()));
            text.setContentText("Sammlung erfolgreich erstellt!");
        } catch (IOException ex) {
            //Logger.getLogger(IR_5_ss16.class.getName()).log(Level.SEVERE, null, ex);
            text.setContentText("Fehler beim Einlesen aufgetreten.");
        }
        stage.setScene(new Scene(text));
        stage.show();
    }

    @FXML
    /**
     * Lässt eine andere Datei als Dokumentensammlung fungieren
     */
    protected void openData() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Öffne Dokumentensammlung");
        File data = chooser.showOpenDialog(new Stage());
        if (data.exists()) {
            sammlung = new Dokumentensammlung(data.getPath());
            listView.setItems(FXCollections.observableArrayList(sammlung.getAll()));
        }
    }

    @FXML
    protected void openAddTruth() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Öffne 'ground_truth'");
        File data = chooser.showOpenDialog(new Stage());
        if (data.exists()) {
            pr = new PandRCalculator(data);
        }
    }

    @FXML
    /**
     * Ruft die Suchfunktionen (derzeit nur lineare Suche) in Abhängigkeit von den ausgewählten Elemente a
     */
    protected void search() {
        //System.out.println(anfrageTyp);
        final long startTime = System.currentTimeMillis();
        try {
            if (sammlung.exists()) {
                ArrayList<Dokument> docs = new ArrayList();
                switch (suchType) {
                    case BOOL:
                        switch (imp) {
                            case INVERL:
                                docs = Retrieval.invSearch(searchField.getText(), linking);
                                break;
                            case NORMAL:
                                docs = Retrieval.linSearch(searchField.getText(), anfrageTyp, linking);
                                break;
                            case SIGNATUR:
                                docs = Retrieval.sigSearch(searchField.getText(), linking);
                            default:
                                break;
                        }
                        break;
                    case VEKTOR:
                        docs = Retrieval.vektorSearch(searchField.getText(), linking);
                        break;
                }

                listView.setItems(FXCollections.observableArrayList(docs));

                //Wozu? Signature s = new Signature(searchField.getText(), -1);
                //P and R
                pr.calculatePandR(searchField.getText().toLowerCase(), sammlung.getIDs(docs), linking);
                String text;
                Double x; //Variable, um besser Darstellung des Prozentwertes auf zwei Nachkommastellen zu ermöglichen
                if (pr.getRecall() < 0) {
                    text = "nicht bekannt";
                } else {
                    x = pr.getRecall() * 100;
                    text = ((Integer) x.intValue()).toString();
                    x -= ((Integer) x.intValue()).doubleValue();
                    x *= 100;
                    text = text.concat(".").concat(((Integer) x.intValue()).toString()).concat(" %");
                }
                rValue.setText(text);
                if (pr.getPrecision() < 0) {
                    text = "nicht bekannt";
                } else {
                    x = pr.getPrecision() * 100;
                    text = ((Integer) x.intValue()).toString();
                    x -= ((Integer) x.intValue()).doubleValue();
                    x *= 100;
                    text = text.concat(".").concat(((Integer) x.intValue()).toString()).concat(" %");
                }
                pValue.setText(text);
            }
            //System.out.println(stemThis(searchField.getText()));

        } catch (IOException ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        final long endTime = System.currentTimeMillis();
        searchTime.setText(((Long) (endTime - startTime)).toString().concat(" ms"));
    }

    @FXML
    /**
     * Öffnet ein neues Fenster mit dem Inhalt des Dokuments, dass in der Listenansicht doppelt angeklickt wurde.
     */
    protected void openDoc(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2 && !listView.getItems().isEmpty()) {
            Dokument doc = listView.getSelectionModel().getSelectedItem();

            Stage stage = new Stage();
            StackPane root = new StackPane();
            Scene scene = new Scene(root);
            TextArea ta = new TextArea();

            ta.setText(doc.getInhalt());
            ta.setWrapText(true);
            ta.setEditable(true);
            ta.selectRange(doc.getPos(searchField.getText()), searchField.getText().length());
            ta.setEditable(false);

            root.getChildren().add(ta);

            stage.setTitle(doc.getTitel());
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    /**
     * Aktiviert Suche auf dem normalen Text
     */
    protected void toggleNT() {
        rbOhneST.setSelected(false);
        rbStamm.setSelected(false);
        anfrageTyp = Dokument.Anfrage.NORMAL;
    }

    @FXML
    /**
     * Aktiviert Suche auf dem Stoppwortfreiem Text
     */
    protected void toggleST() {
        rbNormal.setSelected(false);
        rbStamm.setSelected(false);
        anfrageTyp = Dokument.Anfrage.STOPP;
    }

    @FXML
    /**
     * Aktiviert Suche auf dem stammwortreduziertem Text
     */
    protected void toggleRT() {
        rbNormal.setSelected(false);
        rbOhneST.setSelected(false);
        anfrageTyp = Dokument.Anfrage.REDUZ;
    }

    @FXML
    /**
     * Aktiviert Suche auf dem stammwortreduziertem Text
     */
    protected void toggleIN() {
        rbSigI.setSelected(false);
        rbInvListI.setSelected(false);
        imp = Retrieval.Implementierung.NORMAL;
    }

    @FXML
    /**
     * Aktiviert Suche auf dem stammwortreduziertem Text
     */
    protected void toggleIS() {
        rbNormalI.setSelected(false);
        rbInvListI.setSelected(false);
        imp = Retrieval.Implementierung.SIGNATUR;
    }

    @FXML
    /**
     * Aktiviert Suche auf dem stammwortreduziertem Text
     */
    protected void toggleII() {
        rbSigI.setSelected(false);
        rbNormalI.setSelected(false);
        imp = Retrieval.Implementierung.INVERL;
    }

    @FXML
    /**
     * Aktiviert Suche auf dem stammwortreduziertem Text
     */
    protected void toggleAnd() {
        or.setSelected(false);
        neg.setSelected(false);
        linking = Retrieval.Linking.AND;
    }

    @FXML
    /**
     * Aktiviert Suche auf dem stammwortreduziertem Text
     */
    protected void toggleOr() {
        and.setSelected(false);
        neg.setSelected(false);
        linking = Retrieval.Linking.OR;
    }

    @FXML
    /**
     * Aktiviert Suche auf dem stammwortreduziertem Text
     */
    protected void toggleNeg() {
        and.setSelected(false);
        or.setSelected(false);
        linking = Retrieval.Linking.XOR;
    }

    @FXML
    /**
     * Aktiviere lineare Suche
     */
    protected void toggleLinSearch() {
        suchType = Retrieval.Konzept.BOOL;
        rmiVek.setSelected(false);

    }

    @FXML
    /**
     * Aktiviere Vektorraumsuche
     */
    protected void toggleVekSearch() {
        suchType = Retrieval.Konzept.VEKTOR;
        rmiLin.setSelected(false);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
