package com.tabela.accounting.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.model.MilkCustomer;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class MilkCustomerController implements Initializable {

	@FXML
	private TextField txtSearchText;

	@FXML
	private Button btnSearch;

	@FXML
	private Button btnClearSearch;

	@FXML
	private Button btnAddCustomer;

	@FXML
	private VBox content;

	@FXML
	private TableView<MilkCustomer> customerTable;

	@FXML
	private TableColumn colCustomerName;

	@FXML
	private TableColumn colCreatedDate;

	@FXML
	private TableColumn colCustomerAddress;

	@FXML
	private TableColumn colMilkRate;

	@FXML
	private TableColumn colPendingBill;

	@FXML
	private TableColumn colActive;

	@FXML
	private TableColumn btnEdit;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Rectangle2D screen = TabelaAccounting.getScreen();
		customerTable.setMinHeight(screen.getHeight() - 150);
	}

	@FXML
	void addCustomer(ActionEvent event) {

	}

	@FXML
	void clearSearch(ActionEvent event) {

	}

	@FXML
	void search(ActionEvent event) {

	}

	public void setTable() {
		colCustomerName.setCellValueFactory(new PropertyValueFactory("customerName"));
		colCreatedDate.setCellValueFactory(new PropertyValueFactory("strJoinDate"));
		colCustomerAddress.setCellValueFactory(new PropertyValueFactory("customerAddress"));
		colMilkRate.setCellValueFactory(new PropertyValueFactory("milkRate"));
		colPendingBill.setCellValueFactory(new PropertyValueFactory("billAmount"));
		
        Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>> cellFactory = new Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>>() {
            @Override
            public TableCell<MilkCustomer, Void> call(final TableColumn<MilkCustomer, Void> param) {
                final TableCell<MilkCustomer, Void> cell = new TableCell<MilkCustomer, Void>() {

                    private final CheckBox cb = new CheckBox();
                    {
                    	cb.setOnAction((ActionEvent event) -> {
                        	MilkCustomer data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(cb);
                        }
                    }
                };
                return cell;
            }
        };

        colActive.setCellFactory(cellFactory);
	}


}