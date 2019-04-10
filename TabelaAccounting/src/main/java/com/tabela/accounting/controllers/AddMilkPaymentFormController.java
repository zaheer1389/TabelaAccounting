package com.tabela.accounting.controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.model.CustomerMilk;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.model.MilkPayment;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.util.AppUtil;
import com.tabela.accounting.util.DialogFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class AddMilkPaymentFormController implements Initializable {

	@FXML
    private VBox root;

    @FXML
    private ComboBox<MilkCustomer> cmbCustomer;

    @FXML
    private DatePicker txtPaymentDate;

    @FXML
    private TextField txtAmount;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnReset;
    
    Stage stage;
    
    MilkPayment milkPayment;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		cmbCustomer.setItems(FXCollections.observableArrayList(MilkCustomerController.getCustomers()));
		cmbCustomer.setConverter(new StringConverter<MilkCustomer>() {
			
			@Override
			public String toString(MilkCustomer object) {
				// TODO Auto-generated method stub
				return object.getCustomerName();
			}
			
			@Override
			public MilkCustomer fromString(String string) {
				// TODO Auto-generated method stub
				return cmbCustomer.getItems().stream().filter(customer -> 
								customer.getCustomerName().equals(string)).findFirst().orElse(null);
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
    void reset(ActionEvent event) {
		setData();
    }

    @FXML
    void save(ActionEvent event) {
    	if(cmbCustomer.getValue() == null){
    		DialogFactory.showErrorDialog("Please select customer", stage);
    		return;
    	}
    	if(txtPaymentDate.getValue() == null){
    		DialogFactory.showErrorDialog("Please select milk payment date", stage);
    		return;
    	}
    	
    	if(txtAmount.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter milk payment value", stage);
    		return;
    	}
    	
    	if(!isDouble(txtAmount.getText())){
    		DialogFactory.showErrorDialog("Milk payment amount value must be numeric", stage);
    		return;
    	}
    	
    	if(milkPayment.getId() == null){
    		milkPayment = getMilkPayment(txtPaymentDate.getValue(), cmbCustomer.getValue());
    		if(milkPayment == null){
    			milkPayment = new MilkPayment();
    		}
    		milkPayment.setCustomer(cmbCustomer.getValue());
    		milkPayment.setPaymentDate(new Timestamp(AppUtil.toUtilDate(txtPaymentDate.getValue()).getTime()));
    	}
    	
    	milkPayment.setAmount(Double.parseDouble(txtAmount.getText()));
    	
    	FacadeFactory.getFacade().store(milkPayment);
    	
    	DialogFactory.showInformationDialog("Customer milk payment saved successfully", TabelaAccounting.stage);
    	
    	stage.close();
    }
    
    public void setData(){
    	if(milkPayment == null || milkPayment.getId() == null){
	    	cmbCustomer.setValue(null);
	    	txtPaymentDate.setValue(null);
	    	txtAmount.setText("");
    	}
    	else{
    		cmbCustomer.setValue(milkPayment.getCustomer());
	    	txtPaymentDate.setValue(AppUtil.toLocalDate(milkPayment.getPaymentDate()));
	    	txtAmount.setText(milkPayment.getAmount()+"");
	    	
	    	cmbCustomer.setDisable(true);
	    	txtPaymentDate.setDisable(true);
    	}
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

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
    
	public MilkPayment getMilkPayment() {
		return milkPayment;
	}

	public void setMilkPayment(MilkPayment milkPayment) {
		this.milkPayment = milkPayment;
	}

	public MilkPayment getMilkPayment(LocalDate dt, MilkCustomer customer) {
		String queryStr = "Select m From MilkPayment m where m.customer = :customer and m.paymentDate = :paymentDate";
		Map<String, Object> parameters = new HashMap();
		parameters.put("customer", customer);
		parameters.put("paymentDate", new Timestamp(AppUtil.toUtilDate(dt).getTime()));
		return FacadeFactory.getFacade().find(queryStr, parameters);
	}

}