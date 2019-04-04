package com.tabela.accounting.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.view.MilkSpreadSheet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController implements Initializable {

	@FXML
    private VBox root;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu menuFile;

    @FXML
    private Menu menuLanguage;

    @FXML
    private MenuItem menuILanguage_MenuItemEnglish;

    @FXML
    private MenuItem menuILanguage_MenuItemGujarati;

    @FXML
    private Menu menuMilk;

    @FXML
    private MenuItem menuMilk_MenuItemCustomers;

    @FXML
    private Menu menuMilk_MenuIMilk;

    @FXML
    private MenuItem menuMilk_MenuItemMilkSheet;

    @FXML
    private MenuItem menuMilk_MenuItemMilkForm;

    @FXML
    private Menu menuHelp;

    @FXML
    private Label lblPageTitle;

    @FXML
    private Label lblDate;

    @FXML
    private VBox content;
    
    Stage window;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        window = TabelaAccounting.stage;
        root.prefWidthProperty().bind(window.widthProperty().multiply(0.80));
        
        lblDate.setText(new Date()+"");
        lblPageTitle.setText("");
        
    }

    @FXML
    void changeLangToEnglish(ActionEvent event) {

    }

    @FXML
    void changeLangToGujarati(ActionEvent event) {

    }

    @FXML
    void showMilkCustomersView(ActionEvent event) {
    	lblPageTitle.setText("Milk Customers");
    	
    	try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/MilkCustomer.fxml"));
			content.getChildren().clear();
			content.getChildren().add(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void milkForm(ActionEvent event) {

    }

    @FXML
    void milkSheet(ActionEvent event) {
    	lblPageTitle.setText("Milk");
    	MilkSpreadSheet sheet = new MilkSpreadSheet();
    	try {
			content.getChildren().clear();
			content.getChildren().add(sheet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
