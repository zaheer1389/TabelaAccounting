package com.tabela.accounting.view;

import java.time.LocalDate;

import com.tabela.accounting.controllers.AddMilkCustomerController.CustomerInvoiceType;
import com.tabela.accounting.controls.FXOptionPane;
import com.tabela.accounting.util.AppUtil;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class DateRangeLayoutWithCustomerInvoiceType extends VBox {
	public Button btnReport;
	public DatePicker fromDate;
	public DatePicker toDate;
	private ComboBox<CustomerInvoiceType> cmbCustomerType;
	
	
	public DateRangeLayoutWithCustomerInvoiceType() {
		setAlignment(Pos.TOP_CENTER);
		setSpacing(20.0D);
		setPadding(new Insets(10.0D));
		setPrefSize(400.0D, 200.0D);

		this.fromDate = new DatePicker();
		this.fromDate.setConverter(AppUtil.getDatePickerFormatter());
		this.fromDate.setPromptText("From date");
		this.fromDate.setEditable(true);

		this.toDate = new DatePicker();
		this.toDate.setConverter(AppUtil.getDatePickerFormatter());
		this.toDate.setPromptText("To Date");
		this.toDate.setEditable(true);
		
		cmbCustomerType = new ComboBox();
		cmbCustomerType.setPromptText("Report Type ");
		cmbCustomerType.setItems(FXCollections.observableArrayList(CustomerInvoiceType.values()));
    	cmbCustomerType.setConverter(new StringConverter<CustomerInvoiceType>() {
			
			@Override
			public String toString(CustomerInvoiceType object) {
				// TODO Auto-generated method stub
				return object.toString();
			}
			
			@Override
			public CustomerInvoiceType fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		this.btnReport = new Button("View");

		EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.ENTER) {
					if (DateRangeLayoutWithCustomerInvoiceType.this.fromDate.getValue() == null) {
						FXOptionPane.showErrorDialog(null, "Please enter from date", "Error");
						DateRangeLayoutWithCustomerInvoiceType.this.fromDate.requestFocus();
					} else if (DateRangeLayoutWithCustomerInvoiceType.this.fromDate.getValue() == null) {
						FXOptionPane.showErrorDialog(null, "Please enter to date", "Error");
						DateRangeLayoutWithCustomerInvoiceType.this.toDate.requestFocus();
					} else if(DateRangeLayoutWithCustomerInvoiceType.this.cmbCustomerType.getValue() == null){
						FXOptionPane.showErrorDialog(null, "Please select report type", "Error");
						DateRangeLayoutWithCustomerInvoiceType.this.cmbCustomerType.requestFocus();
					} else {
						DateRangeLayoutWithCustomerInvoiceType.this.btnReport.fire();
					}
				}
			}
		};
		setOnKeyPressed(handler);

		getChildren().addAll(new Node[] { this.fromDate, this.toDate, this.cmbCustomerType, this.btnReport });
		
		fromDate.setValue(LocalDate.now().minusDays(7L));
		toDate.setValue(LocalDate.now());
	}

	public Button getBtnReport() {
		return this.btnReport;
	}

	public void setBtnReport(Button btnReport) {
		this.btnReport = btnReport;
	}

	public DatePicker getFromDate() {
		return this.fromDate;
	}

	public void setFromDate(DatePicker fromDate) {
		this.fromDate = fromDate;
	}

	public DatePicker getToDate() {
		return this.toDate;
	}

	public void setToDate(DatePicker toDate) {
		this.toDate = toDate;
	}

	public ComboBox<CustomerInvoiceType> getCmbCustomerType() {
		return cmbCustomerType;
	}

	public void setCmbCustomerType(ComboBox<CustomerInvoiceType> cmbCustomerType) {
		this.cmbCustomerType = cmbCustomerType;
	}
	
	
}
