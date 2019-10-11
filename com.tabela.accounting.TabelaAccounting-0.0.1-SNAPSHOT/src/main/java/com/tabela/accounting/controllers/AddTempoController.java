package com.tabela.accounting.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.model.Merchant;
import com.tabela.accounting.model.Tempo;
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

public class AddTempoController implements Initializable{

	@FXML
	private VBox root;
	 
    @FXML
    private TextField txtTempoName;
    
    @FXML
    private TextField txtTempoNumber;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnResetForm;
    
    Stage stage;
    
    Tempo tempo;

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
    	
    	if(txtTempoName.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter tempo name", stage);
    		return;
    	}
    	else if(txtTempoNumber.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter tempo number", stage);
    		return;
    	}
    	
    	
    	
    	if(tempo == null){
    		tempo = new Tempo();
    	}
    	tempo.setBranch(TabelaAccounting.getBranch());
    	tempo.setTempoName(txtTempoName.getText());
    	tempo.setTempoNumber(txtTempoNumber.getText());
    	FacadeFactory.getFacade().store(tempo);
    	
    	DialogFactory.showInformationDialog("Tempo saved successfully", TabelaAccounting.stage);
    	
    	stage.close();
    }
    
    public void setData(){
    	if(tempo == null){
    		txtTempoName.setText("");
	    	txtTempoNumber.setText("");
	    }
    	else{
    		txtTempoName.setText(tempo.getTempoName());
    		txtTempoNumber.setText(tempo.getTempoNumber());
	    	
    	}
    }

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Tempo getTempo() {
		return tempo;
	}

	public void setTempo(Tempo tempo) {
		this.tempo = tempo;
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
