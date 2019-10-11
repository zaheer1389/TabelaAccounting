package com.tabela.accounting.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.controls.FXOptionPane;
import com.tabela.accounting.enums.DialogType;
import com.tabela.accounting.model.Merchant;
import com.tabela.accounting.model.Merchant;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.util.DialogFactory;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class MerchantController implements Initializable {

	@FXML
	private TextField txtSearchText;

	@FXML
	private Button btnSearch;

	@FXML
	private Button btnClearSearch;

	@FXML
	private Button btnAddCustomer;

	@FXML
	private VBox content;

	@FXML
	private TableView<Merchant> merchantTable;

	@FXML
	private TableColumn colMerchantName;

	@FXML
	private TableColumn btnEdit;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Rectangle2D screen = TabelaAccounting.getScreen();
		merchantTable.setMinHeight(screen.getHeight() - 150);
		
		setTable(getMerchants());
	}

	@FXML
	void addCustomer(ActionEvent event) {
		// New window (Stage)
		showForm(new Merchant());
	}

	@FXML
	void clearSearch(ActionEvent event) {
		txtSearchText.setText("");
		setTable(getMerchants());
	}

	@FXML
	void search(ActionEvent event) {
		if(txtSearchText.getText().length() == 0){
			DialogFactory.showErrorDialog("Please enter search text", TabelaAccounting.stage);
			return;
		}
		
		setTable(getMerchants(txtSearchText.getText()));
	}

	public void setTable(List<Merchant> customers) {
		colMerchantName.setCellValueFactory(new PropertyValueFactory("merchantName"));
        
        Callback<TableColumn<Merchant, Void>, TableCell<Merchant, Void>> cellFactoryEditButton = new Callback<TableColumn<Merchant, Void>, TableCell<Merchant, Void>>() {
            @Override
            public TableCell<Merchant, Void> call(final TableColumn<Merchant, Void> param) {
                final TableCell<Merchant, Void> cell = new TableCell<Merchant, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		Merchant milkCustomer = getTableView().getItems().get(getIndex());
                            showForm(milkCustomer);
                        });
                    	button.setMinWidth(50);
                    	button.getStyleClass().setAll("btnEdit");
                    	
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };
        
        btnEdit.setCellFactory(cellFactoryEditButton);
        
        merchantTable.setItems(FXCollections.observableArrayList(customers));
	}
	
	public void showForm(Merchant merchant){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddMerchantForm.fxml"));
			Parent root = (Parent)loader.load();
			

	        AddMerchantController controller = (AddMerchantController)loader.getController();
			controller.setMerchant(merchant);
	        
			Scene scene = new Scene(root, 500, 400);
	        
			Stage stage = new Stage();
	        stage.setTitle("Add Merchant");
	        stage.initStyle(StageStyle.UTILITY);
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.initOwner(TabelaAccounting.stage);
	        stage.setResizable(false);
	        stage.setScene(scene);
	        //stage.setX(150);
	        //stage.setY(150);
	        stage.show();
	       
	        controller.setStage(stage);
		       
	        stage.setOnHidden(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					setTable(getMerchants());
				}
			});
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static List<Merchant> getMerchants() {
		return FacadeFactory.getFacade().list(Merchant.class);
	}

	public static List<Merchant> getMerchants(String name) {
		String queryStr = "Select c from Merchant as c where c.merchantName like :cname ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cname", "%" + name + "%");
		List<Merchant> l = FacadeFactory.getFacade().list(queryStr, parameters);
		return l;
	}

}