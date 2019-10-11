package com.tabela.accounting.persistence;

import com.tabela.accounting.persistence.model.AbstractPojo;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract interface IFacade {
	public abstract <A extends AbstractPojo> A find(Class<A> paramClass, Long paramLong);

	public abstract <A extends AbstractPojo> List<A> list(Class<A> paramClass);

	public abstract <A extends AbstractPojo> List<A> list(Class<A> paramClass, int paramInt1, int paramInt2);

	public abstract <A extends AbstractPojo> List<A> list(String paramString, Map<String, Object> paramMap);

	public abstract <A extends AbstractPojo> List<A> list(String paramString, Map<String, Object> paramMap,
			int paramInt1, int paramInt2);

	public abstract <A extends AbstractPojo> A find(String paramString, Map<String, Object> paramMap);

	public abstract void store(AbstractPojo paramAbstractPojo);

	public abstract <A extends AbstractPojo> void storeAll(Collection<A> paramCollection);

	public abstract void delete(AbstractPojo paramAbstractPojo);

	public abstract <A extends AbstractPojo> void deleteAll(Collection<A> paramCollection);

	public abstract Long count(Class<? extends AbstractPojo> paramClass);

	public abstract Long count(Class<? extends AbstractPojo> paramClass, String paramString,
			Map<String, Object> paramMap);

	public abstract List<?> getFieldValues(Class<? extends AbstractPojo> paramClass, String paramString1,
			String paramString2, Map<String, Object> paramMap);

	public abstract Long count(String paramString, Map<String, Object> paramMap);
	
	/**
     * Update all the fields in the entity to the most up-to-date version of
     * data found in the database. Any changes made to the entity object before
     * calling this method will be overwritten.
     * 
     * @param pojo
     *            The entity object you wish to refresh
     */
    public <A extends AbstractPojo> void refresh(A pojo);
}
