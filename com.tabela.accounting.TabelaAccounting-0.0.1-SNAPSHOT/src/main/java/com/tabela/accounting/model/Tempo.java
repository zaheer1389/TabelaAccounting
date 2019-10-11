package com.tabela.accounting.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tabela.accounting.enums.CustomerType;
import com.tabela.accounting.persistence.model.AbstractPojo;

@Entity
@Table(name = "Tempo")
public class Tempo extends AbstractPojo implements Serializable {
	
	@Column(name = "TempoName")
	private String tempoName;
	
	@Column(name = "TempoNumber")
	private String tempoNumber;
	
	@JoinColumn(name = "BranchId")
	private Branch branch;

	public String getTempoName() {
		return tempoName;
	}

	public void setTempoName(String tempoName) {
		this.tempoName = tempoName;
	}

	public String getTempoNumber() {
		return tempoNumber;
	}

	public void setTempoNumber(String tempoNumber) {
		this.tempoNumber = tempoNumber;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	
	
}
