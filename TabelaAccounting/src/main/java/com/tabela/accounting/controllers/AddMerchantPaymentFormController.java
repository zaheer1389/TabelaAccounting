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
import com.tabela.accounting.model.Merchant;
import com.tabela.accounting.model.MerchantPayment;
import com.tabela.accounting.model.Merchant;
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

public class AddMerchantPaymentFormController implements Initializable {

	@FXML
    private VBox root;

    @FXML
    private ComboBox<Merchant> cmbCustomer;

    @FXML
    private DatePicker txtPaymentDate;

    @FXML
    private TextField txtAmount;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnReset;
    
    Stage stage;
    
    MerchantPayment merchantPayment;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		cmbCustomer.setItems(FXCollections.observableArrayList(MerchantController.getMerchants()));
		cmbCustomer.setConverter(new StringConverter<Merchant>() {
			
			@Override
			public String toString(Merchant object) {
				// TODO Auto-generated method stub
				return object.getMerchantName();
			}
			
			@Override
			public Merchant fromString(String string) {
				// TODO Auto-generated method stub
				return cmbCustomer.getItems().stream().filter(customer -> 
								customer.getMerchantName().equals(string)).findFirst().orElse(null);
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
    		DialogFactory.showErrorDialog("Please select merchant", stage);
    		return;
    	}
    	if(txtPaymentDate.getValue() == null){
    		DialogFactory.showErrorDialog("Please select merchant payment date", stage);
    		return;
    	}
    	
    	if(txtAmount.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter merchant payment value", stage);
    		return;
    	}
    	
    	if(!isDouble(txtAmount.getText())){
    		DialogFactory.showErrorDialog("Merchant payment amount value must be numeric", stage);
    		return;
    	}
    	
    	if(merchantPayment.getId() == null){
    		merchantPayment = getMerchantPayment(txtPaymentDate.getValue(), cmbCustomer.getValue());
    		if(merchantPayment == null){
    			merchantPayment = new MerchantPayment();
    		}
    		merchantPayment.setMerchant(cmbCustomer.getValue());
    		merchantPayment.setPaymentDate(new Timestamp(AppUtil.toUtilDate(txtPaymentDate.getValue()).getTime()));
    	}
    	
    	merchantPayment.setAmount(Double.parseDouble(txtAmount.getText()));
    	
    	FacadeFactory.getFacade().store(merchantPayment);
    	
    	DialogFactory.showInformationDialog("Merchant payment saved successfully", TabelaAccounting.stage);
    	
    	stage.close();
    }
    
    public void setData(){
    	if(merchantPayment == null || merchantPayment.getId() == null){
	    	cmbCustomer.setValue(null);
	    	txtPaymentDate.setValue(null);
	    	txtAmount.setText("");
    	}
    	else{
    		cmbCustomer.setValue(merchantPayment.getMerchant());
	    	txtPaymentDate.setValue(AppUtil.toLocalDate(merchantPayment.getPaymentDate()));
	    	txtAmount.setText(merchantPayment.getAmount()+"");
	    	
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
    
	public MerchantPayment getMerchantPayment() {
		return merchantPayment;
	}

	public void setMerchantPayment(MerchantPayment merchantPayment) {
		this.merchantPayment = merchantPayment;
	}

	public MerchantPayment getMerchantPayment(LocalDate dt, Merchant customer) {
		String queryStr = "Select m From MerchantPayment m where m.merchant = :customer and m.paymentDate = :paymentDate";
		Map<String, Object> parameters = new HashMap();
		parameters.put("customer", customer);
		parameters.put("paymentDate", new Timestamp(AppUtil.toUtilDate(dt).getTime()));
		return FacadeFactory.getFacade().find(queryStr, parameters);
	}

}