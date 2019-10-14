package com.tabela.accounting.controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.model.Expense;
import com.tabela.accounting.model.MilkPayment;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.util.AppUtil;
import com.tabela.accounting.util.DialogFactory;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddExpenseFormController implements Initializable {

	@FXML
    private VBox root;

    @FXML
    private DatePicker txtExpenseDate;

    @FXML
    private TextField txtAmount;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnReset;

    @FXML
    private ComboBox<String> cmbExpenseType;

    @FXML
    private TextArea txtRemarks;
    
    Stage stage;
    Expense expense;
    boolean readOnly;
    
    private String expenseType [] = {"Tabela General", "Salvage Exp", "Medical Exp", "Construction Work", 
    		"Electricity Exp", "Society Payment", "Buffalo Purchased", "Freight Paid", "Heil Charges", "Dairy Exp", "Conveyance/Travelling", 
    		"Printing/Stationary", "Telephone/Mobile Exp", "Transportation Exp", "Travelling Exp", "Diwali", "Other"};
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		cmbExpenseType.getItems().addAll(expenseType);
		
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
    	if(txtExpenseDate.getValue() == null){
    		DialogFactory.showErrorDialog("Please select expense date", stage);
    		return;
    	}
    	
    	if(txtAmount.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter expense amount", stage);
    		return;
    	}
    	
    	if(!isDouble(txtAmount.getText())){
    		DialogFactory.showErrorDialog("Expense amount value must be numeric", stage);
    		return;
    	}
    	
    	if(cmbExpenseType.getValue() == null){
    		DialogFactory.showErrorDialog("Please select expense type", stage);
    		return;
    	}
    	
    	if(txtAmount.getText().length() == 0){
    		DialogFactory.showErrorDialog("Please enter expense remarks", stage);
    		return;
    	}
    	
    	if(expense.getId() == null){
    		expense.setExpenseDate(new Timestamp(AppUtil.toUtilDate(txtExpenseDate.getValue()).getTime()));
    	}
    	
    	expense.setAmount(Double.parseDouble(txtAmount.getText()));
    	expense.setExpenseType(cmbExpenseType.getValue());
    	expense.setDescription(txtRemarks.getText());
    	
    	FacadeFactory.getFacade().store(expense);
    	
    	DialogFactory.showInformationDialog("Expense saved successfully", TabelaAccounting.stage);
    	
    	stage.close();
    }
    
    public void setData(){
    	if(expense == null || expense.getId() == null){
	    	txtExpenseDate.setValue(LocalDate.now());
	    	txtAmount.setText("");
	    	cmbExpenseType.setValue(null);
	    	txtRemarks.setText("");
    	}
    	else{
    		txtExpenseDate.setValue(AppUtil.toLocalDate(expense.getExpenseDate()));
	    	txtAmount.setText(expense.getAmount()+"");
	    	cmbExpenseType.setValue(expense.getExpenseType());
	    	txtRemarks.setText(expense.getDescription());
    	}
    	
    	btnSave.setVisible(!readOnly);
    	btnReset.setVisible(!readOnly);
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

	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
    
	

}