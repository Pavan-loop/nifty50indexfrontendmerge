package com.nichi.mergednifty50index.controller;

import com.nichi.mergednifty50index.database.tanush.TradeEntry;
import com.nichi.mergednifty50index.database.tanush.dao.Pricedto;
import com.nichi.mergednifty50index.database.tanush.service.TradeService;
import com.nichi.mergednifty50index.model.TableTL;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TLViewController {

    @FXML
    private TableView<TableTL> tableTL;
    @FXML
    private TableColumn<TableTL, String> colStocks;
    @FXML
    private TableColumn<TableTL, String> colName;
    @FXML
    private TableColumn<TableTL, Integer> colPosition;
    @FXML
    private TableColumn<TableTL, Integer> colPrePosition;
    @FXML
    private TableColumn<TableTL, Double> colTradePrice;
    @FXML
    private TableColumn<TableTL, Integer> colCashflow;
    @FXML
    private TableColumn<TableTL, Integer> colPrice;
    @FXML
    private TableColumn<TableTL, Integer> colPrePrice;
    @FXML
    private TableColumn<TableTL, Double> colPerChange;
    @FXML
    private TableColumn<TableTL, Integer> colPL;
    @FXML
    private TableColumn<TableTL, Integer> colPricePL;
    @FXML
    private TableColumn<TableTL, Integer> colTradePL;
    @FXML
    private DatePicker tradeDatePicker;

    private final TradeService trade = new TradeService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");


    @FXML
    public void initialize() {
        tableTL.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colStocks.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStocks()));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colPosition.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPosition()).asObject());
        colPrePosition.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPrePosition()).asObject());
        colTradePrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTradePrice()).asObject());
        colCashflow.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCashFlow()).asObject());
        colPrice.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPrice()).asObject());
        colPrePrice.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPrePrice()).asObject());
        colPerChange.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPerChange()).asObject());
        colPL.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPl()).asObject());
        colPricePL.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPricepl()).asObject());
        colTradePL.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTradepl()).asObject());
    }

    @FXML
    private void onLoadButtonClick() {
        LocalDate currentDate = tradeDatePicker.getValue();
        if (currentDate == null) {
            showAlert("Input Error", "Please select a trade date.");
            return;
        }

        List<TradeEntry> allTrades = trade.getDataTradeUntil(java.sql.Date.valueOf(currentDate));
        List<Pricedto> priceList = trade.price();

        Map<String, TreeMap<LocalDate, Double>> priceMap = new HashMap<>();
        for (Pricedto p : priceList) {
            try {
                LocalDate d = LocalDate.parse(p.getDt(), formatter);
                priceMap.computeIfAbsent(p.getCode(), k -> new TreeMap<>()).put(d, p.getPrice());
            } catch (Exception ex) {
                System.err.println("Bad price date: " + p.getDt());
            }
        }

        Map<String, Integer> tradePLMap = new HashMap<>();
        for (TradeEntry t : allTrades) {
            int signTradePL = t.getSide().equalsIgnoreCase("b") ? -1 : +1;
            tradePLMap.merge(
                    t.getCode(),
                    t.getTradeprice() * t.getQuantity() * signTradePL,
                    Integer::sum
            );
        }

        LocalDate prevDate = currentDate.minusDays(1);
        List<TradeEntry> before = trade.getDataTradeUntil(java.sql.Date.valueOf(prevDate));
        Map<String, Integer> posTm1 = new HashMap<>();
        for (TradeEntry t : before) {
            int sign = t.getSide().equalsIgnoreCase("b") ? +1 : -1;
            posTm1.merge(t.getCode(), t.getQuantity() * sign, Integer::sum);
        }

        Map<String, List<TradeEntry>> todayMap = allTrades.stream()
                .filter(t -> {
                    try {
                        return LocalDate.parse(t.getTradedate(), formatter).equals(currentDate);
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .collect(Collectors.groupingBy(TradeEntry::getCode));

        List<TableTL> rows = new ArrayList<>();
        for (var en : todayMap.entrySet()) {
            String code = en.getKey();
            List<TradeEntry> trades = en.getValue();
            String name = trades.get(0).getName();

            int netQty = 0, cashflow = 0;
            double sumSignedValue = 0, sumSignedQty = 0;

            for (TradeEntry t : trades) {
                int qty = t.getQuantity();
                int price = t.getTradeprice();
                int signQty = t.getSide().equalsIgnoreCase("b") ? +1 : -1;   // for position
                int signCash = t.getSide().equalsIgnoreCase("b") ? -1 : +1;  // for cashflow

                netQty += signQty * qty;
                cashflow += signCash * price * qty;

                sumSignedValue += signQty * price * qty;
                sumSignedQty += signQty * qty;
            }

            double rawTP = sumSignedQty != 0 ? sumSignedValue / sumSignedQty : 0.0;
            double tradePrice = Math.round(rawTP * 100.0) / 100.0;

            int positionTm1 = posTm1.getOrDefault(code, 0);

            var m = priceMap.getOrDefault(code, new TreeMap<>());
            var eT = m.floorEntry(currentDate);
            var eM1 = eT != null ? m.lowerEntry(eT.getKey()) : null;
            double priceT = eT != null ? eT.getValue() : 0.0;
            double priceTm1 = eM1 != null ? eM1.getValue() : 0.0;

            double rawPct = priceTm1 != 0.0 ? (priceT - priceTm1) * 100.0 / priceTm1 : 0.0;
            double pct = Math.round(rawPct * 100.0) / 100.0;

            int pricePL = (int) Math.round(netQty * priceT - positionTm1 * priceTm1);
            int PL = pricePL + cashflow;
            int tradePL = cashflow;

            TableTL dto = new TableTL(
                    code, name,
                    netQty, positionTm1,
                    tradePrice, cashflow,
                    (int) priceT, (int) priceTm1,
                    pct, PL, pricePL, tradePL
            );
            System.out.println(dto);
            rows.add(dto);
        }

        tableTL.setItems(FXCollections.observableArrayList(rows));
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setContentText(message);
        a.showAndWait();
    }


}
