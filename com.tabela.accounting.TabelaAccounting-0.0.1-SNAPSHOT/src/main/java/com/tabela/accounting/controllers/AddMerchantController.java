package com.tabela.accounting.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.model.Merchant;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.util.DialogFactory;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddMerchantController implements Initializable{

	@FXML
	private VBox root;
	 
    @FXML
    private TextField txtMerchantName;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnResetForm;
    
    Stage stage;
    
    Merchant merchant;

    @Override
	public void initialize(URL url, ResourceBundle rb) {
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
    	
    	if(txtMerchantName.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter Merchant name", stage);
    		return;
    	}
    	
    	
    	
    	if(merchant == null){
    		merchant = new Merchant();
    	}
    	merchant.setBranch(TabelaAccounting.getBranch());
    	merchant.setMerchantName(txtMerchantName.getText());
    	FacadeFactory.getFacade().store(merchant);
    	
    	DialogFactory.showInformationDialog("Merchant saved successfully", TabelaAccounting.stage);
    	
    	stage.close();
    }
    
    public void setData(){
    	if(merchant == null){
	    	txtMerchantName.setText("");
	    	
    	}
    	else{
    		txtMerchantName.setText(merchant.getMerchantName());
	    	
    	}
    }

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

    public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant customer) {
		this.merchant = customer;
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
