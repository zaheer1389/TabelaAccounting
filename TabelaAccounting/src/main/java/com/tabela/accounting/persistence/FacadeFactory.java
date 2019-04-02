package com.tabela.accounting.persistence;

public class FacadeFactory {
	public static IFacade getFacade() {
		return new JPAFacade();
	}
}
