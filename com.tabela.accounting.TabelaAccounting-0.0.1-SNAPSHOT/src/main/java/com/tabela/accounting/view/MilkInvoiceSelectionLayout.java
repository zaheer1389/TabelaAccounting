package com.tabela.accounting.view;

import java.time.LocalDate;
import java.util.List;

import com.tabela.accounting.controllers.MilkCustomerController;
import com.tabela.accounting.controls.FXOptionPane;
import com.tabela.accounting.enums.CustomerInvoiceType;
import com.tabela.accounting.enums.CustomerType;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.util.AppUtil;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

public class MilkInvoiceSelectionLayout extends VBox {
	public Button btnReport;
	public DatePicker fromDate;
	public DatePicker toDate;
	private ComboBox<CustomerInvoiceType> cmbBillingType;
	private ComboBox<CustomerType> cmbCustomerType;
	private ListView<MilkCustomer> listView;
	
	public MilkInvoiceSelectionLayout(boolean billSheet) {
		setAlignment(Pos.TOP_LEFT);
		//setSpacing(10.0D);
		//setPadding(new Insets(10.0D));
		setPrefSize(400.0D, 400.0D);
		
		GridPane gridPane = getGrid();
		getChildren().add(gridPane);
		
		Label headerLabel = new Label("Milk Invoice Form");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gridPane.add(headerLabel, 0,0,2,1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));
        
		Label label = new Label("From Date : ");
        gridPane.add(label, 0,1);
        
		fromDate = new DatePicker();
		fromDate.setConverter(AppUtil.getDatePickerFormatter());
		fromDate.setPromptText("From date");
		fromDate.setEditable(true);
		gridPane.add(fromDate, 1,1);

		label = new Label("To Date : ");
        gridPane.add(label, 0,2);
        
		toDate = new DatePicker();
		toDate.setConverter(AppUtil.getDatePickerFormatter());
		toDate.setPromptText("To Date");
		toDate.setEditable(true);
		gridPane.add(toDate, 1,2);
		
		label = new Label("Customer Type : ");
        gridPane.add(label, 0, 3);
        
		cmbCustomerType = new ComboBox<>();
		cmbCustomerType.setItems(FXCollections.observableArrayList(CustomerType.values()));
		cmbCustomerType.setValue(CustomerType.SALE);
		cmbCustomerType.setConverter(new StringConverter<CustomerType>() {
			
			@Override
			public String toString(CustomerType object) {
				// TODO Auto-generated method stub
				return object.toString();
			}
			
			@Override
			public CustomerType fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		gridPane.add(cmbCustomerType, 1, 3);
		
		label = new Label("Billing Type : ");
        gridPane.add(label, 0, 4);
        
		cmbBillingType = new ComboBox();
		cmbBillingType.setPromptText("Report Type ");
		cmbBillingType.setItems(FXCollections.observableArrayList(CustomerInvoiceType.values()));
		cmbBillingType.setValue(CustomerInvoiceType.weekly);
    	cmbBillingType.setConverter(new StringConverter<CustomerInvoiceType>() {
			
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
    	gridPane.add(cmbBillingType, 1, 4);
    	
    	label = new Label("Customers : ");
       
        listView = new ListView();
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listView.setMinHeight(100);
		listView.setMaxWidth(200);
		listView.setCellFactory(customer -> new ListCell<MilkCustomer>() {
		    @Override
		    protected void updateItem(MilkCustomer item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null || item.getCustomerName() == null) {
		            setText(null);
		        } else {
		            setText(item.getCustomerName());
		        }
		    }
		});
		
		
        if(!billSheet){
        	gridPane.add(label, 0, 5); 
        	gridPane.add(listView, 1, 5);
        }
    	
		
		btnReport = new Button("Submit");
		btnReport.setPrefHeight(40);
		btnReport.setDefaultButton(true);
		btnReport.setPrefWidth(100);
        gridPane.add(btnReport, 0, 6, 2, 1);
        GridPane.setHalignment(btnReport, HPos.CENTER);
        GridPane.setMargin(btnReport, new Insets(20, 0,20,0));

		cmbBillingType.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				ComboBox<CustomerInvoiceType> box = (ComboBox<CustomerInvoiceType>)event.getSource();
				if(cmbCustomerType.getValue() != null){
					listView.getItems().clear();
					List<MilkCustomer> milkCustomers = MilkCustomerController.getCustomers(false, cmbCustomerType.getValue(), cmbBillingType.getValue());
					listView.getItems().addAll(FXCollections.observableArrayList(milkCustomers));
				}
			}
		});
		
		cmbCustomerType.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				ComboBox<CustomerInvoiceType> box = (ComboBox<CustomerInvoiceType>)event.getSource();
				if(cmbBillingType.getValue() != null){
					listView.getItems().clear();
					List<MilkCustomer> milkCustomers = MilkCustomerController.getCustomers(false, cmbCustomerType.getValue(), cmbBillingType.getValue());
					listView.getItems().addAll(FXCollections.observableArrayList(milkCustomers));
				}
			}
		});

		EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.ENTER) {
					if (fromDate.getValue() == null) {
						FXOptionPane.showErrorDialog(null, "Please enter from date", "Error");
						fromDate.requestFocus();
					} else if (fromDate.getValue() == null) {
						FXOptionPane.showErrorDialog(null, "Please enter to date", "Error");
						toDate.requestFocus();
					} else if(cmbCustomerType.getValue() == null){
						FXOptionPane.showErrorDialog(null, "Please select customer type", "Error");
						cmbCustomerType.requestFocus();
					} else if(cmbBillingType.getValue() == null){
						FXOptionPane.showErrorDialog(null, "Please select report type", "Error");
						cmbBillingType.requestFocus();
					} else {
						btnReport.fire();
					}
				}
			}
		};
		setOnKeyPressed(handler);
		
		fromDate.setValue(LocalDate.now().minusDays(7L));
		toDate.setValue(LocalDate.now());
		
		if(cmbCustomerType.getValue() != null && cmbBillingType.getValue() != null){
			listView.getItems().clear();
			List<MilkCustomer> milkCustomers = MilkCustomerController.getCustomers(false, cmbCustomerType.getValue(), cmbBillingType.getValue());
			listView.getItems().addAll(FXCollections.observableArrayList(milkCustomers));
		}
	}
	
	public ListView<MilkCustomer> getMilkCustomerListView(CustomerType customerType, CustomerInvoiceType customerInvoiceType) {
		ListView<MilkCustomer> listView = new ListView();
		listView.getItems().addAll(FXCollections.observableArrayList(MilkCustomerController.getCustomers(false, customerType, customerInvoiceType)));
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listView.setCellFactory(customer -> new ListCell<MilkCustomer>() {
		    @Override
		    protected void updateItem(MilkCustomer item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null || item.getCustomerName() == null) {
		            setText(null);
		        } else {
		            setText(item.getCustomerName());
		        }
		    }
		});
		return listView;
	}
	
	private GridPane getGrid(){
		// Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();

        // Position the pane at the center of the screen, both vertically and horizontally
        gridPane.setAlignment(Pos.TOP_LEFT);

        // Set a padding of 20px on each side
        //gridPane.setPadding(new Insets(40, 40, 40, 40));

        // Set the horizontal gap between columns
        gridPane.setHgap(10);

        // Set the vertical gap between rows
        gridPane.setVgap(10);

        // Add Column Constraints

        // columnOneConstraints will be applied to all the nodes placed in column one.
        ColumnConstraints columnOneConstraints = new ColumnConstraints(140, 140, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        // columnTwoConstraints will be applied to all the nodes placed in column two.
        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200,200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        return gridPane;
	}

	public Button getBtnReport() {
		return btnReport;
	}

	public void setBtnReport(Button btnReport) {
		this.btnReport = btnReport;
	}

	public DatePicker getFromDate() {
		return fromDate;
	}

	public void setFromDate(DatePicker fromDate) {
		this.fromDate = fromDate;
	}

	public DatePicker getToDate() {
		return toDate;
	}

	public void setToDate(DatePicker toDate) {
		this.toDate = toDate;
	}

	public ComboBox<CustomerInvoiceType> getCmbCustomerType() {
		return cmbBillingType;
	}

	public void setCmbCustomerType(ComboBox<CustomerInvoiceType> cmbCustomerType) {
		cmbBillingType = cmbCustomerType;
	}

	public ComboBox<CustomerType> getCmbType() {
		return cmbCustomerType;
	}

	public void setCmbType(ComboBox<CustomerType> cmbType) {
		this.cmbCustomerType = cmbType;
	}

	public ListView<MilkCustomer> getListView() {
		return listView;
	}

	public void setListView(ListView<MilkCustomer> listView) {
		listView = listView;
	}
	
	
}
