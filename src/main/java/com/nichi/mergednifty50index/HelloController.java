package com.nichi.mergednifty50index;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class HelloController {
    @FXML
    private Button buttonTL;
    @FXML
    private Button buttonTradeEntry;

    @FXML
    private AnchorPane contentArea;

    @FXML
    public void initialize() {
        handleTLClick();
    }

    @FXML
    private void handleTLClick() {
        loadView("/com/nichi/mergednifty50index/TLView.fxml");
        selectedButton(buttonTL, buttonTradeEntry);
    }

    @FXML
    private void handleTradeEntry() {
        loadView("/com/nichi/mergednifty50index/TradeEntryView.fxml");
        selectedButton(buttonTradeEntry, buttonTL);
    }


    private void selectedButton(Button selected, Button unSelected) {
        selected.getStyleClass().removeAll("un-selected");
        selected.getStyleClass().add("selected");

        unSelected.getStyleClass().removeAll("selected");
        unSelected.getStyleClass().add("un-selected");
    }

    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(node);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}