package com.tabela.accounting;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.persistence.EntityManager;

import com.tabela.accounting.controllers.MainController;
import com.tabela.accounting.model.Branch;
import com.tabela.accounting.persistence.FacadeFactory;
import com.tabela.accounting.persistence.JPAFacade;
import com.tabela.accounting.util.DialogFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TabelaAccounting extends Application {

	public static Stage stage = null;

	@Override
	public void start(Stage stage) throws Exception {
		
		TabelaAccounting.stage = stage;
		
		System.err.println(getProperties());
		
		EntityManager em = JPAFacade.getEntityManager();
		setData();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
		Parent root = loader.load();
		root.getStyleClass().add("panel-primary");
		
		MainController controller = loader.getController();
	    controller.setHostServices(getHostServices());
	    
		Scene scene = new Scene(root);
		//scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
		scene.getStylesheets().add("themes/style.css");
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNIFIED);
		stage.setTitle("Tabela Accounting");
		stage.show();

	}
	
	public static Stage getPrimaryStage(){
		return stage;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Rectangle2D getScreen()
	{
		Screen screen = Screen.getPrimary();
		return screen.getBounds();
	}

	public static Properties getProperties() {
		try {
			Properties configProperties = new Properties();
		    InputStream inputStream = TabelaAccounting.class.getClassLoader().getResourceAsStream("application.properties");
		    configProperties.load(inputStream);
			return configProperties;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DialogFactory.showExceptionDialog(e, stage);
			return null;
		}
	}

	public static String getException(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	public void setData(){
		Branch branch = FacadeFactory.getFacade().find(Branch.class, 1L);
		if(branch == null){
			branch = new Branch();
			branch.setId(1L);
			branch.setName("Aarey Tabela");
			FacadeFactory.getFacade().store(branch);
		}
	}
	
	public static Branch getBranch(){
		return FacadeFactory.getFacade().find(Branch.class, 1L);
	}
	
	public static Connection getConnection() {
		//Connection connection = null;
		try {
			Properties properties = getProperties();
			Class.forName(properties.getProperty("jdbc.driver"));
			return DriverManager.getConnection(properties.getProperty("jdbc.url"),
					properties.getProperty("jdbc.username"), properties.getProperty("jdbc.password"));
		} catch (Exception e) {
			DialogFactory.showExceptionDialog(e, null);
		}
		return null;
	}

}
