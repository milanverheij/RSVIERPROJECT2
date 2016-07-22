package gui.gui;

import java.math.BigDecimal;
import java.util.ArrayList;

import gui.bewerkingen.BestellingGUIBewerkingen;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Artikel;
import model.Bestelling;

public class BestellingGui extends Application {
    private BestellingGUIBewerkingen bestellingGUIBewerkingen = new BestellingGUIBewerkingen();
    private Artikel huidigArtikel;
    private Bestelling bestelling;

    private ErrorBox errorBox = new ErrorBox();

    private Stage bestellingStage;

    private Button voegToeButton;
    private Button haalWegButton;
    private Button okeButton;
    private Button cancelButton;

    private CheckBox bestellingActief;

    private boolean bestellingAanpassen = false;

    private ArrayList<Artikel> artikelArrayList;

    private ListView<Long> bestellingListView;
    private ListView<String> huidigeBestellingListView;
    private ListView<String> artikelenListView;

    private TextField totaalPrijs;
    private TextField aantal;

    private long klantId;

    // Voor aanpassen bestelling
    public void setAndRun(long klantId) throws Exception{
        this.klantId = klantId;
        start(new Stage());
    }

    // Voor nieuwe bestelling
    public void setAndRun(long klantId, ListView<Long> bestellingListView) throws Exception {
        this.klantId = klantId;

        this.bestellingListView = bestellingListView;
        start(new Stage());
    }

    @Override
    public void start(Stage bestellingStage) throws Exception {
        this.bestellingStage = bestellingStage;

        if(bestelling != null){
            bestellingStage.setTitle("Bestelling aanpassen");
            bestellingAanpassen = true;
        }

        if(bestelling == null){
            bestelling = new Bestelling();
            bestellingStage.setTitle("Nieuwe bestelling");
            bestellingAanpassen = false;
        }

        maakButtons();
        maakListViews();
        bestellingGUIBewerkingen.populateArrayListsEnListView(artikelenListView);
        bestellingGUIBewerkingen.populateBestellingListView(huidigeBestellingListView, bestelling);
        maakTextFields();

        GridPane bestelGrid = populateBestelGrid();

        HBox winkelwagenBox = new HBox(); //Bevat knoppen om dingen in/uit de winkelwagen te doen
        winkelwagenBox.getChildren().addAll(voegToeButton, haalWegButton);
        winkelwagenBox.setSpacing(3);

        HBox okeCancelBox = new HBox();
        okeCancelBox.getChildren().addAll(okeButton, cancelButton);
        okeCancelBox.setSpacing(3);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(6));
        vbox.setSpacing(3);
        vbox.getChildren().addAll(bestelGrid, winkelwagenBox, okeCancelBox);

        HBox box = new HBox();
        box.setPadding(new Insets(6));
        box.getChildren().addAll(artikelenListView, vbox, huidigeBestellingListView);

        bestellingStage.setScene(new Scene(box));
        bestellingStage.show();
    }

    private void maakTextFields() {
        totaalPrijs = new TextField();
        totaalPrijs.setEditable(false);

        aantal = new TextField();

        aantal.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty() || Long.parseLong(newValue) < 1) //Wanneer er geen aantal of 0 is opgegeven
                totaalPrijs.setText("0");
            else if(aantal.getText().length() < 10)
                totaalPrijs.setText(huidigArtikel.getArtikelPrijs().multiply(new BigDecimal(Long.parseLong(newValue))).toString());
            else
                errorBox.setMessageAndStart("We kunnen niet meer dan 999.999.999 van een artikel leveren");
        });
    }

    private void maakButtons(){
        bestellingActief = new CheckBox("Bestelling actief");

        okeButton = new Button("Oke");
        okeButton.setOnAction(e -> bestellingGUIBewerkingen.maakbestelling(klantId, bestelling, bestellingActief,
                bestellingListView, bestellingStage));

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> bestellingStage.close());

        if(bestellingAanpassen){
            voegToeButton = new Button("Voeg toe / Update");
            bestellingActief.setSelected(bestelling.getBestellingActief());
        }else{
            voegToeButton = new Button("Voeg toe");
            bestellingActief.setSelected(true);
            bestellingActief.setDisable(true);
        }
        voegToeButton.setOnAction(e -> voegArtikelenAanBestellingToe());

        haalWegButton = new Button("Haal uit winkelwagen");
        haalWegButton.setOnAction(e -> haalUitBestelling());
    }

    private void haalUitBestelling() {
        bestelling.verwijderArtikel(huidigArtikel);
        bestellingGUIBewerkingen.populateBestellingListView(huidigeBestellingListView, bestelling);
    }

    private void voegArtikelenAanBestellingToe(){
        if(!aantal.getText().isEmpty())
            if(!(Long.parseLong(aantal.getText()) < 1)){ //Er moet minimaal 1 besteld worden
                huidigArtikel.setAantalBesteld(Integer.parseInt(aantal.getText()));
                if(!bestelling.getArtikelLijst().contains(huidigArtikel)){ //Als het er niet in staat, voeg het toe
                    bestelling.voegArtikelToe(huidigArtikel);
                }
                bestellingGUIBewerkingen.populateBestellingListView(huidigeBestellingListView, bestelling);
            }else{
                errorBox.setMessageAndStart("Geef een geldig aantal op");
            }
    }

    private GridPane populateBestelGrid(){
        GridPane bestelGrid = new GridPane();
        bestelGrid.setVgap(2);
        bestelGrid.setHgap(5);
        bestelGrid.setPadding(new Insets(4));

        bestelGrid.add(new Label("Aantal"), 0, 0);
        bestelGrid.add(new Label("Prijs"), 0, 1);

        bestelGrid.add(aantal, 1, 0);
        bestelGrid.add(totaalPrijs, 1, 1);

        bestelGrid.add(bestellingActief, 0, 2);

        return bestelGrid;
    }

    private void maakListViews(){
        artikelenListView = new ListView<String>();
        artikelenListView.setOnMouseClicked(e -> getItemVanListView(artikelenListView, artikelArrayList));

        huidigeBestellingListView = new ListView<String>();
        huidigeBestellingListView.setOnMouseClicked(e -> getItemVanListView(huidigeBestellingListView, bestelling.getArtikelLijst()));
    }

    private void getItemVanListView(ListView<String> listView, ArrayList<Artikel> artikelArrayList) {
        try{
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if(selectedIndex != -1){
                huidigArtikel = artikelArrayList.get(selectedIndex);
                aantal.setText("" + huidigArtikel.getAantalBesteld());
            }
        }catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public void setBestelling(Bestelling bestelling){
        this.bestelling = bestelling;
    }
}