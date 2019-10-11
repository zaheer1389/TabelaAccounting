package com.tabela.accounting.controls;

import java.util.List;

import com.tabela.accounting.model.MilkCustomer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SpreadSheet extends TableView<GenericModel> {
	List<MilkCustomer> customers;
	TableColumn colCustomer;
	TableColumn col1;
	TableColumn col2;
	TableColumn col3;
	TableColumn col4;
	TableColumn col5;
	TableColumn col6;
	TableColumn col7;
	TableColumn col1_M;
	TableColumn col1_E;
	TableColumn col2_M;
	TableColumn col2_E;
	TableColumn col3_M;
	TableColumn col3_E;
	TableColumn col4_M;
	TableColumn col4_E;
	TableColumn col5_M;
	TableColumn col5_E;
	TableColumn col6_M;
	TableColumn col6_E;
	TableColumn col7_M;
	TableColumn col7_E;

	public SpreadSheet(List<MilkCustomer> customers) {
		this.customers = customers;
		init();
		setEditable(true);
	}

	public void init() {
		this.colCustomer = new TableColumn("Customer");
		this.colCustomer.setCellValueFactory(new PropertyValueFactory("name"));

		this.col1 = new TableColumn("1-Aug-2014");
		this.col2 = new TableColumn("2-Aug-2014");
		this.col3 = new TableColumn("3-Aug-2014");
		this.col4 = new TableColumn("4-Aug-2014");
		this.col5 = new TableColumn("5-Aug-2014");
		this.col6 = new TableColumn("6-Aug-2014");
		this.col7 = new TableColumn("7-Aug-2014");

		this.col1_M = new TableColumn("Morning");
		this.col1_M.setCellValueFactory(new PropertyValueFactory("0"));
		this.col1_E = new TableColumn("Evening");
		this.col1_E.setCellValueFactory(new PropertyValueFactory("1"));
		this.col1.getColumns().addAll(new Object[] { this.col1_M, this.col1_E });

		this.col2_M = new TableColumn("Morning");
		this.col2_M.setCellValueFactory(new PropertyValueFactory("2"));
		this.col2_E = new TableColumn("Evening");
		this.col2_E.setCellValueFactory(new PropertyValueFactory("3"));
		this.col2.getColumns().addAll(new Object[] { this.col2_M, this.col2_E });

		this.col3_M = new TableColumn("Morning");
		this.col3_M.setCellValueFactory(new PropertyValueFactory("4"));
		this.col3_E = new TableColumn("Evening");
		this.col3_E.setCellValueFactory(new PropertyValueFactory("5"));
		this.col3.getColumns().addAll(new Object[] { this.col3_M, this.col3_E });

		this.col4_M = new TableColumn("Morning");
		this.col4_M.setCellValueFactory(new PropertyValueFactory("6"));
		this.col4_E = new TableColumn("Evening");
		this.col4_E.setCellValueFactory(new PropertyValueFactory("7"));
		this.col4.getColumns().addAll(new Object[] { this.col4_M, this.col4_E });

		this.col5_M = new TableColumn("Morning");
		this.col5_M.setCellValueFactory(new PropertyValueFactory("8"));
		this.col5_E = new TableColumn("Evening");
		this.col5_E.setCellValueFactory(new PropertyValueFactory("9"));
		this.col5.getColumns().addAll(new Object[] { this.col5_M, this.col5_E });

		this.col6_M = new TableColumn("Morning");
		this.col6_E = new TableColumn("Evening");
		this.col6.getColumns().addAll(new Object[] { this.col6_M, this.col6_E });

		this.col7_M = new TableColumn("Morning");
		this.col7_E = new TableColumn("Evening");
		this.col7.getColumns().addAll(new Object[] { this.col7_M, this.col7_E });

		getColumns().add(this.colCustomer);
		getColumns().add(this.col1);
		getColumns().add(this.col2);
		getColumns().add(this.col3);
		getColumns().add(this.col4);
		getColumns().add(this.col5);
		getColumns().add(this.col6);
		getColumns().add(this.col7);

		ObservableList<GenericModel> data = FXCollections.observableArrayList();
		for (MilkCustomer customer : this.customers) {
			data.add(new GenericModel(new Object[] { "1_Morning", "1_Evening", "2_Morning", "2_Evening", "3_Morning",
					"3_Evening", "4_Morning", "4_Evening", "5_Morning", "5_Evening", "6_Morning", "6_Evening",
					"7_Morning", "7_Evening", customer.getCustomerName() }));
		}
		setItems(data);
	}
}
