package com.tabela.accounting.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.enums.CustomerInvoiceType;
import com.tabela.accounting.enums.CustomerType;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.model.Tempo;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.util.DialogFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class AddMilkCustomerController implements Initializable{
	
	@FXML
	private VBox root;
	 
    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtCustomerAddress;

    @FXML
    private TextField txtMilkRate;

    @FXML
    private TextField txtPendingBillAmount;
    
    @FXML
    private ComboBox<Tempo> cmbTempo;
    
    @FXML
    private ComboBox<CustomerInvoiceType> cmbCustomerBillType;
    
    @FXML
    private ComboBox<CustomerType> cmbCustomerType;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnResetForm;
    
    Stage stage;
    
    MilkCustomer customer;

    @Override
	public void initialize(URL url, ResourceBundle rb) {
    	
    	cmbTempo.setItems(FXCollections.observableArrayList(TempoController.getTempos()));
    	cmbTempo.setConverter(new StringConverter<Tempo>() {
			
			@Override
			public String toString(Tempo object) {
				// TODO Auto-generated method stub
				return object.getTempoName();
			}
			
			@Override
			public Tempo fromString(String string) {
				// TODO Auto-generated method stub
				return cmbTempo.getItems().stream().filter(customer -> 
								customer.getTempoName().equals(string)).findFirst().orElse(null);
			}
		});
    	
    	cmbCustomerBillType.setItems(FXCollections.observableArrayList(CustomerInvoiceType.values()));
    	cmbCustomerBillType.setConverter(new StringConverter<CustomerInvoiceType>() {
			
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
    	
    	cmbCustomerType.setItems(FXCollections.observableArrayList(CustomerType.values()));
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
		
		
    	Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				setData();
			}
		});
	}
    
    @FXML
    void resetForm(ActionEvent event) {
    	setData();
    }

    @FXML
    void saveCustomer(ActionEvent event) {
    	
    	if(txtCustomerName.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter customer name", stage);
    		return;
    	}
    	
    	if(txtCustomerAddress.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter customer address", stage);
    		return;
    	}
    	
    	if(txtMilkRate.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter milk rate", stage);
    		return;
    	}
    	
    	if(!isDouble(txtMilkRate.getText())){
    		DialogFactory.showErrorDialog("Milk rate must be numeric", stage);
    		return;
    	}
    	
    	if(txtPendingBillAmount.getText().length() > 0 && !isDouble(txtPendingBillAmount.getText())){
    		DialogFactory.showErrorDialog("Pending bill must be numeric", stage);
    		return;
    	}
    	
    	if(cmbCustomerType.getValue() == null){
    		DialogFactory.showErrorDialog("Please select customer type", stage);
    		return;
    	}
    	
    	if(cmbCustomerType.getValue() == CustomerType.SALE){
	    	if(cmbTempo.getValue() == null){
	    		DialogFactory.showErrorDialog("Please select tempo", stage);
	    		return;
	    	}
	    	
	    	if(cmbCustomerBillType.getValue() == null){
	    		DialogFactory.showErrorDialog("Please select customer type", stage);
	    		return;
	    	}
    	}
    	
    	
    	
    	if(customer == null){
    		customer = new MilkCustomer();
    	}
    	customer.setBranch(TabelaAccounting.getBranch());
    	customer.setCustomerName(txtCustomerName.getText());
    	customer.setCustomerAddress(txtCustomerAddress.getText());
    	customer.setMilkRate(Double.parseDouble(txtMilkRate.getText()));
    	customer.setBillAmount(Double.parseDouble(txtPendingBillAmount.getText()));
    	customer.setCustomerType(cmbCustomerType.getValue());
    	customer.setTempo(cmbTempo.getValue());
    	customer.setCustomerInvoiceType(cmbCustomerBillType.getValue());
    	FacadeFactory.getFacade().store(customer);
    	
    	DialogFactory.showInformationDialog("Customer saved successfully", TabelaAccounting.stage);
    	
    	stage.close();
    }
    
    public void setData(){
    	if(customer == null){
	    	txtCustomerName.setText("");
	    	txtCustomerAddress.setText("");
	    	txtMilkRate.setText("");
	    	txtPendingBillAmount.setText("");
	    	cmbCustomerType.setValue(null);
	    	cmbTempo.setValue(null);
	    	cmbCustomerBillType.setValue(null);
    	}
    	else{
    		txtCustomerName.setText(customer.getCustomerName());
	    	txtCustomerAddress.setText(customer.getCustomerAddress());
	    	txtMilkRate.setText(customer.getMilkRate()+"");
	    	txtPendingBillAmount.setText(customer.getBillAmount()+"");
	    	cmbCustomerType.setValue(customer.getCustomerType());
	    	cmbTempo.setValue(customer.getTempo());
	    	cmbCustomerBillType.setValue(customer.getCustomerInvoiceType());
    	}
    }

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

    public MilkCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(MilkCustomer customer) {
		this.customer = customer;
	}

	public boolean isDouble(String value){
    	try {
			Double.parseDouble(value);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
    }

}
