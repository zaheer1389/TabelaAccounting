package com.tabela.accounting.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.model.MilkCustomer;

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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
	private TableColumn<?, ?> colCustomerName;

	@FXML
	private TableColumn<?, ?> colCreatedDate;

	@FXML
	private TableColumn<?, ?> colCustomerAddress;

	@FXML
	private TableColumn<?, ?> colMilkRate;

	@FXML
	private TableColumn<?, ?> colPendingBill;

	@FXML
	private TableColumn<?, ?> colActive;

	@FXML
	private TableColumn<?, ?> btnEdit;

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

	private class ActiveStatus extends TableCell<MilkCustomer, Boolean> {
		final CheckBox cb = new CheckBox();

		public ActiveStatus() {
			this.cb.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					
				}
			});
		}

		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if (!empty) {
				
			}
		}
	}

	private class EditButton extends TableCell<MilkCustomer, Boolean> {
		final Hyperlink cellButton = new Hyperlink();

		EditButton() {
			this.cellButton.setPrefWidth(100.0D);
			this.cellButton.setPrefHeight(22.0D);
			this.cellButton.getStyleClass().add("btnEdit");
			this.cellButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					
				}
			});
		}

		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if (!empty) {
				setGraphic(this.cellButton);
			}
		}
	}

}