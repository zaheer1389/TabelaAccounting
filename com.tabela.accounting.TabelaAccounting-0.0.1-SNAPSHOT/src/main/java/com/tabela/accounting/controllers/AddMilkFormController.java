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

public class AddMilkFormController implements Initializable {

	@FXML
    private VBox root;

    @FXML
    private ComboBox<MilkCustomer> cmbCustomer;

    @FXML
    private DatePicker txtMilkDate;

    @FXML
    private TextField txtMorningMilk;

    @FXML
    private TextField txtEveningMilk;

    @FXML
    private TextField txtMilkRate;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnReset;
    
    Stage stage;
    
    CustomerMilk customerMilk;
    
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
    	if(txtMilkDate.getValue() == null){
    		DialogFactory.showErrorDialog("Please select milk date", stage);
    		return;
    	}
    	
    	if(txtMorningMilk.getText() != null && txtMorningMilk.getText().length() > 0){
    		if(!isDouble(txtMorningMilk.getText())){
    			DialogFactory.showErrorDialog("Morning milk value must be numeric", stage);
    			return;
    		}
    	}
    	
    	if(txtEveningMilk.getText() != null && txtEveningMilk.getText().length() > 0){
    		if(!isDouble(txtEveningMilk.getText())){
    			DialogFactory.showErrorDialog("Evening milk value must be numeric", stage);
    			return;
    		}
    	}
    	
    	if(txtMilkRate.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter milk rate", stage);
    		return;
    	}
    	
    	if(!isDouble(txtMilkRate.getText())){
    		DialogFactory.showErrorDialog("Milk rate value must be numeric", stage);
    		return;
    	}
    	
    	if(customerMilk.getId() == null){
    		customerMilk = getCustomerMilk(txtMilkDate.getValue(), cmbCustomer.getValue());
    		if(customerMilk == null){
    			customerMilk = new CustomerMilk();
    		}
    		customerMilk.setCustomer(cmbCustomer.getValue());
    		customerMilk.setMilkDate(new Timestamp(AppUtil.toUtilDate(txtMilkDate.getValue()).getTime()));
    	}
    	
    	if(txtMorningMilk.getText() != null && txtMorningMilk.getText().length() > 0){
    		customerMilk.setMorningMilk(Double.parseDouble(txtMorningMilk.getText()));
    	}
    	
    	if(txtEveningMilk.getText() != null && txtEveningMilk.getText().length() > 0){
    		customerMilk.setEveningMilk(Double.parseDouble(txtEveningMilk.getText()));
    	}
    	
    	customerMilk.setMilkRate(Double.parseDouble(txtMilkRate.getText()));
    	
    	FacadeFactory.getFacade().store(customerMilk);
    	
    	DialogFactory.showInformationDialog("Customer milk successfully", TabelaAccounting.stage);
    	
    	stage.close();
    }
    
    public void setData(){
    	if(customerMilk == null || customerMilk.getId() == null){
	    	cmbCustomer.setValue(null);
	    	txtMilkDate.setValue(null);
	    	txtMorningMilk.setText("");
	    	txtEveningMilk.setText("");
	    	txtMilkRate.setText("");
    	}
    	else{
    		cmbCustomer.setValue(customerMilk.getCustomer());
	    	txtMilkDate.setValue(AppUtil.toLocalDate(customerMilk.getMilkDate()));
	    	txtMorningMilk.setText(customerMilk.getMorningMilk()+"");
	    	txtEveningMilk.setText(customerMilk.getEveningMilk()+"");
	    	txtMilkRate.setText(customerMilk.getMilkRate()+"");
	    	
	    	cmbCustomer.setDisable(true);
	    	txtMilkDate.setDisable(true);
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

	public CustomerMilk getCustomerMilk() {
		return customerMilk;
	}

	public void setCustomerMilk(CustomerMilk customerMilk) {
		this.customerMilk = customerMilk;
	}
    
	public CustomerMilk getCustomerMilk(LocalDate dt, MilkCustomer customer) {
		String queryStr = "Select c from CustomerMilk as c where c.customer = :customer and c.milkDate = :milkDate";
		Map<String, Object> parameters = new HashMap();
		parameters.put("customer", customer);
		parameters.put("milkDate",new Timestamp(AppUtil.toUtilDate(dt).getTime()));
		return FacadeFactory.getFacade().find(queryStr, parameters);
	}

}