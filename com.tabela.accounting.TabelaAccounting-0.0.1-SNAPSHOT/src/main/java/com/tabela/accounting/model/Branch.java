package com.tabela.accounting.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tabela.accounting.persistence.model.AbstractPojo;

@Entity
@Table(name = "Branch")
public class Branch extends AbstractPojo implements Serializable {
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "addedDate")
	private Timestamp addedDate;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getAddedDate() {
		return this.addedDate;
	}

	public void setAddedDate(Timestamp addedDate) {
		this.addedDate = addedDate;
	}
}
