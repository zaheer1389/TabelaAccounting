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
import com.tabela.accounting.model.MilkCustomer;
import com.tabela.accounting.model.Tempo;
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

public class TempoController implements Initializable {

	@FXML
	private TextField txtSearchText;

	@FXML
	private Button btnSearch;

	@FXML
	private Button btnClearSearch;

	@FXML
	private Button btnAddTempo;

	@FXML
	private VBox content;

	@FXML
	private TableView<Tempo> tempoTable;

	@FXML
	private TableColumn colTempoName;
	
	@FXML
	private TableColumn colTempoNumber;

	@FXML
	private TableColumn btnEdit;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Rectangle2D screen = TabelaAccounting.getScreen();
		tempoTable.setMinHeight(screen.getHeight() - 150);
		
		setTable(getTempos());
	}

	@FXML
	void addCustomer(ActionEvent event) {
		// New window (Stage)
		showForm(new Tempo());
	}

	@FXML
	void clearSearch(ActionEvent event) {
		txtSearchText.setText("");
		setTable(getTempos());
	}

	@FXML
	void search(ActionEvent event) {
		if(txtSearchText.getText().length() == 0){
			DialogFactory.showErrorDialog("Please enter search text", TabelaAccounting.stage);
			return;
		}
		
		setTable(getTempos(txtSearchText.getText()));
	}

	public void setTable(List<Tempo> customers) {
		colTempoName.setCellValueFactory(new PropertyValueFactory("tempoName"));
		colTempoNumber.setCellValueFactory(new PropertyValueFactory("tempoNumber"));
        
        Callback<TableColumn<Tempo, Void>, TableCell<Tempo, Void>> cellFactoryEditButton = new Callback<TableColumn<Tempo, Void>, TableCell<Tempo, Void>>() {
            @Override
            public TableCell<Tempo, Void> call(final TableColumn<Tempo, Void> param) {
                final TableCell<Tempo, Void> cell = new TableCell<Tempo, Void>() {

                    private final Button button = new Button("");
                    {
                    	button.setOnAction((ActionEvent event) -> {
                    		Tempo milkCustomer = getTableView().getItems().get(getIndex());
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
        
        tempoTable.setItems(FXCollections.observableArrayList(customers));
	}
	
	public void showForm(Tempo tempo){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddTempoForm.fxml"));
			Parent root = (Parent)loader.load();
			

	        AddTempoController controller = (AddTempoController)loader.getController();
			controller.setTempo(tempo);
	        
			Scene scene = new Scene(root, 500, 400);
	        
			Stage stage = new Stage();
	        stage.setTitle("Add Tempo");
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
					setTable(getTempos());
				}
			});
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static List<Tempo> getTempos() {
		return FacadeFactory.getFacade().list(Tempo.class);
	}

	public static List<Tempo> getTempos(String name) {
		String queryStr = "Select c from Tempo as c where c.tempoName like :cname ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cname", "%" + name + "%");
		List<Tempo> l = FacadeFactory.getFacade().list(queryStr, parameters);
		return l;
	}
	
	public static List<MilkCustomer> getTempoCustomers(Tempo tempo){
		String queryStr = "Select c From MilkCustomer as c where c.tempo = :tempo ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tempo", tempo);
		List<MilkCustomer> l = FacadeFactory.getFacade().list(queryStr, parameters);
		return l;
	}

}