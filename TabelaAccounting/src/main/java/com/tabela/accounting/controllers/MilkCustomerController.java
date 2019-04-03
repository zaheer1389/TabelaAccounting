package com.tabela.accounting.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.persistence.FacadeFactory;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MilkCustomerController implements Initializable {

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
	private TableView<MilkCustomer> customerTable;

	@FXML
	private TableColumn colCustomerName;

	@FXML
	private TableColumn colCreatedDate;

	@FXML
	private TableColumn colCustomerAddress;

	@FXML
	private TableColumn colMilkRate;

	@FXML
	private TableColumn colPendingBill;

	@FXML
	private TableColumn colActive;

	@FXML
	private TableColumn btnEdit;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Rectangle2D screen = TabelaAccounting.getScreen();
		customerTable.setMinHeight(screen.getHeight() - 150);
		
		setTable();
	}

	@FXML
	void addCustomer(ActionEvent event) {
		// New window (Stage)
		System.out.println("btn clicked");
		Label secondLabel = new Label("I'm a Label on new Window");
		 
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);

        Scene secondScene = new Scene(secondaryLayout, 230, 100);
        
        Stage newWindow = new Stage();
        newWindow.setTitle("Second Stage");
        newWindow.setScene(secondScene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(TabelaAccounting.stage);

        // Set position of second window, related to primary window.
        newWindow.setX(TabelaAccounting.stage.getX() + 200);
        newWindow.setY(TabelaAccounting.stage.getY() + 100);
	}

	@FXML
	void clearSearch(ActionEvent event) {

	}

	@FXML
	void search(ActionEvent event) {

	}

	public void setTable() {
		colCustomerName.setCellValueFactory(new PropertyValueFactory("customerName"));
		colCreatedDate.setCellValueFactory(new PropertyValueFactory("strJoinDate"));
		colCustomerAddress.setCellValueFactory(new PropertyValueFactory("customerAddress"));
		colMilkRate.setCellValueFactory(new PropertyValueFactory("milkRate"));
		colPendingBill.setCellValueFactory(new PropertyValueFactory("billAmount"));
		
        Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>> cellFactory = new Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>>() {
            @Override
            public TableCell<MilkCustomer, Void> call(final TableColumn<MilkCustomer, Void> param) {
                final TableCell<MilkCustomer, Void> cell = new TableCell<MilkCustomer, Void>() {

                    private final CheckBox cb = new CheckBox();
                    {
                    	cb.setOnAction((ActionEvent event) -> {
                        	MilkCustomer data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(cb);
                        }
                    }
                };
                return cell;
            }
        };
        
        Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>> cellFactoryEditButton = new Callback<TableColumn<MilkCustomer, Void>, TableCell<MilkCustomer, Void>>() {
            @Override
            public TableCell<MilkCustomer, Void> call(final TableColumn<MilkCustomer, Void> param) {
                final TableCell<MilkCustomer, Void> cell = new TableCell<MilkCustomer, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                        	MilkCustomer data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
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

        colActive.setCellFactory(cellFactory);
        btnEdit.setCellFactory(cellFactoryEditButton);
        
        List<MilkCustomer> milkCustomers = FacadeFactory.getFacade().list(MilkCustomer.class);
        customerTable.setItems(FXCollections.observableArrayList(milkCustomers));
	}


}