package com.tabela.accounting.controls;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.enums.DialogType;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXOptionPane {
	public static enum Response {
		NO, YES, CANCEL;
	}

	private static Response buttonSelected = Response.CANCEL;
	private static ImageView icon = new ImageView();

	static class Dialog extends Stage {
		public Dialog(String title, Stage owner, Scene scene, String iconFile) {
			setTitle(title);
			initStyle(StageStyle.UTILITY);
			initModality(Modality.APPLICATION_MODAL);
			initOwner(owner);
			setResizable(false);
			setScene(scene);
			FXOptionPane.icon.setImage(new Image(getClass().getResourceAsStream(iconFile)));
		}

		public void showDialog() {
			sizeToScene();
			centerOnScreen();
			showAndWait();
		}
	}

	static class Message extends TextArea {
		public Message(String msg) {
			super();
			setText(msg);
			setWrapText(true);
			setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
			setId("textArea");
			setEditable(false);
		}
	}

	public static void showInformationDialog(Stage owner, String message, String title) {
		showMessageDialog(owner, new Message(message), title, DialogType.INFORMATION);
	}

	public static void showErrorDialog(Stage owner, String message, String title) {
		showMessageDialog(owner, new Message(message), title, DialogType.ERROR);
	}

	public static void showWarningDialog(Stage owner, String message, String title) {
		showMessageDialog(owner, new Message(message), title, DialogType.WARNING);
	}

	public static void showExceptionDialog(Stage owner, Throwable e, String title) {
		showExceptionDialog(owner, new Message(TabelaAccounting.getException(e)), title, DialogType.ERROR);
	}

	private static void showMessageDialog(Stage owner, Node message, String title, DialogType type) {
		VBox vb = new VBox();
		vb.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vb.setSpacing(10.0D);
		vb.getStylesheets().add("/themes/style.css");
		vb.getStyleClass().add("reportform");

		Scene scene = new Scene(vb, 400.0D, 130.0D);

		String image = "";
		if (type == DialogType.INFORMATION) {
			image = "/images/dialog-information.png";
		} else if (type == DialogType.ERROR) {
			image = "/images/dialog-error.png";
		} else if (type == DialogType.WARNING) {
			image = "/images/dialog-warning.png";
		}
		Dialog dial = new Dialog(title, owner, scene, image);

		Button okButton = new Button("OK");
		okButton.setAlignment(Pos.CENTER);
		//okButton.setPadding(new Insets(0.0D, 20.0D, 0.0D, 0.0D));
		//okButton.setPrefHeight(25.0D);
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				dial.close();
			}
		});
		
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER_RIGHT);
		buttons.setSpacing(10.0D);
		buttons.getChildren().addAll(new Node[] { okButton });
		buttons.setPadding(new Insets(0.0D, 20.0D, 0.0D, 0.0D));
		buttons.setPrefHeight(35.0D);

		HBox msg = new HBox();
		msg.setSpacing(5.0D);
		msg.getChildren().addAll(new Node[] { icon, message });
		vb.getChildren().addAll(new Node[] { msg, buttons });

		EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.ENTER) {
					dial.close();
				}
			}
		};
		
		vb.setOnKeyPressed(handler);
		vb.requestFocus();

		dial.showDialog();
	}

	private static void showExceptionDialog(Stage owner, Node message, String title, DialogType type) {
		VBox vb = new VBox();
		vb.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vb.setSpacing(10.0D);
		vb.getStylesheets().add("/themes/style.css");
		vb.getStyleClass().add("reportform");

		Scene scene = new Scene(vb, 500.0D, 200.0D);

		String image = "";
		if (type == DialogType.INFORMATION) {
			image = "/images/dialog-information.png";
		} else if (type == DialogType.ERROR) {
			image = "/images/dialog-error.png";
		} else if (type == DialogType.WARNING) {
			image = "/images/dialog-warning.png";
		}
		Dialog dial = new Dialog(title, owner, scene, image);

		Button okButton = new Button("OK");
		okButton.setAlignment(Pos.CENTER);
		//okButton.setPadding(new Insets(0.0D, 20.0D, 0.0D, 0.0D));
		//okButton.setPrefHeight(25.0D);
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				dial.close();
			}
		});
		
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER_RIGHT);
		buttons.setSpacing(10.0D);
		buttons.getChildren().addAll(new Node[] { okButton });
		buttons.setPadding(new Insets(0.0D, 20.0D, 0.0D, 0.0D));
		buttons.setPrefHeight(35.0D);

		HBox msg = new HBox();
		msg.setSpacing(5.0D);
		msg.getChildren().addAll(new Node[] { icon, message });
		vb.getChildren().addAll(new Node[] { msg, buttons });

		EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.ENTER) {
					dial.close();
				}
			}
		};
		vb.setOnKeyPressed(handler);
		vb.requestFocus();

		dial.setResizable(true);
		dial.showDialog();
	}

	public static Response showConfirmDialog(Stage owner, String message, String title) {
		VBox vb = new VBox();
		vb.getStylesheets().add("/themes/style.css");
		vb.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vb.setSpacing(10.0D);
		vb.getStyleClass().add("reportform");

		Scene scene = new Scene(vb, 400.0D, 130.0D);

		Dialog dial = new Dialog(title, owner, scene, "/images/dialog-information.png");

		Button yesButton = new Button("Yes");
		yesButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				dial.close();
				FXOptionPane.buttonSelected = FXOptionPane.Response.YES;
			}
		});
		Button noButton = new Button("No");
		noButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				dial.close();
				FXOptionPane.buttonSelected = FXOptionPane.Response.NO;
			}
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				dial.close();
				FXOptionPane.buttonSelected = FXOptionPane.Response.CANCEL;
			}
		});
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER_RIGHT);
		buttons.setSpacing(10.0D);
		buttons.getChildren().addAll(new Node[] { yesButton, noButton, cancelButton });
		buttons.setPadding(new Insets(0.0D, 20.0D, 0.0D, 0.0D));
		buttons.setPrefHeight(35.0D);

		HBox msg = new HBox();
		msg.setSpacing(5.0D);
		msg.getChildren().addAll(new Node[] { icon, new Message(message) });
		vb.getChildren().addAll(new Node[] { msg, buttons });

		EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.ENTER) {
					dial.close();
					FXOptionPane.buttonSelected = FXOptionPane.Response.YES;
				}
				if (arg0.getCode() == KeyCode.ESCAPE) {
					dial.close();
					FXOptionPane.buttonSelected = FXOptionPane.Response.CANCEL;
				}
			}
		};
		vb.setOnKeyPressed(handler);
		vb.requestFocus();

		dial.showDialog();

		return buttonSelected;
	}
}
