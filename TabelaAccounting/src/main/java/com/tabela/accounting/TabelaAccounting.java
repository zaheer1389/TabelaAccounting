package com.tabela.accounting;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.persistence.EntityManager;

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
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNIFIED);
		stage.setTitle("Tabela Accounting");
		stage.show();
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

}
