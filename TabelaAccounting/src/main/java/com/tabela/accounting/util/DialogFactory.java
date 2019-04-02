package com.tabela.accounting.util;

import com.tabela.accounting.controls.FXOptionPane;
import com.tabela.accounting.enums.DialogType;

import javafx.stage.Stage;

public class DialogFactory {
	public static void showInformationDialog(String message, Stage stage) {
		FXOptionPane.showInformationDialog(null, message, "Tabelas");
	}

	public static void showWarningDialog(String message, Stage stage) {
		FXOptionPane.showInformationDialog(stage, message, "Tabelas");
	}

	public static void showErrorDialog(String message, Stage stage) {
		FXOptionPane.showErrorDialog(stage, message, "Tabelas");
	}

	public static FXOptionPane.Response showConfirmationDialog(String message, DialogType dialogType, Stage stage) {
		FXOptionPane.Response response = null;
		if (dialogType == DialogType.OKCANCEL) {
			response = FXOptionPane.showConfirmDialog(stage, message, "Please confirm!");
		} else if (dialogType == DialogType.YESNOCANCEL) {
			response = FXOptionPane.showConfirmDialog(stage, message, "Please confirm!");
		}
		return response;
	}

	public static void showExceptionDialog(Exception e, Stage stage) {
		FXOptionPane.showExceptionDialog(stage, e, "Ooops, there was an exception!");
	}
}
