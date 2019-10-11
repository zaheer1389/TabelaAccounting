package com.tabela.accounting.persistence.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class AbstractPojo implements Serializable {
	
	private static final long serialVersionUID = -7289994339186082141L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	
	@Column(nullable = false)
	@Version
	protected Long consistencyVersion;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getConsistencyVersion() {
		return this.consistencyVersion;
	}

	public void setConsistencyVersion(Long consistencyVersion) {
		this.consistencyVersion = consistencyVersion;
	}
}
