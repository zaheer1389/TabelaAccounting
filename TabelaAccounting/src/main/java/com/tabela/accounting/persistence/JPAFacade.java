package com.tabela.accounting.persistence;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;

import com.tabela.accounting.TabelaAccounting;
import com.tabela.accounting.persistence.model.AbstractPojo;
import com.tabela.accounting.util.DialogFactory;

public class JPAFacade implements IFacade, Serializable {
	private static final String PERSISTENCE_UNIT_NAME = "testPU";
	private static EntityManagerFactory emf = null;

	public static EntityManagerFactory getEntityManagerFactory() {
		Properties props = TabelaAccounting.getProperties();
		
		Map<String, String> properties = new HashMap();
		properties.put("javax.persistence.jdbc.driver", props.getProperty("jdbc.driver"));
		properties.put("javax.persistence.jdbc.url", props.getProperty("jdbc.url"));
		properties.put("javax.persistence.jdbc.user", props.getProperty("jdbc.username"));
		properties.put("javax.persistence.jdbc.password", props.getProperty("jdbc.password"));

		emf = Persistence.createEntityManagerFactory("testPU", properties);
		
		return emf;
	}

	public static EntityManager getEntityManager() {
		EntityManagerFactory emf = getEntityManagerFactory();
		if (emf != null) {
			try {
				return emf.createEntityManager();
			} catch (Exception e) {
				DialogFactory.showExceptionDialog(e, null);
				return null;
			}
		}
		return null;
	}

	public <A extends AbstractPojo> A find(Class<A> clazz, Long id) {
		EntityManager em = getEntityManager();

		return (A) em.find(clazz, id);
	}

	public <A extends AbstractPojo> List<A> list(Class<A> clazz) {
		EntityManager em = getEntityManager();

		Query query = generateQuery(clazz, em);

		return query.getResultList();
	}

	private <A extends AbstractPojo> Query generateQuery(Class<A> entityClass, EntityManager em) {
		ExpressionBuilder builder = new ExpressionBuilder();
		JpaEntityManager jpaEm = JpaHelper.getEntityManager(em);

		Query query = jpaEm.createQuery(builder, entityClass);
		return query;
	}

	public <A extends AbstractPojo> List<A> list(String queryStr, Map<String, Object> parameters) {
		EntityManager em = getEntityManager();

		Query query = generateQuery(queryStr, parameters, em);

		return query.getResultList();
	}

	private Query generateQuery(String queryStr, Map<String, Object> parameters, EntityManager em) {
		Query query = em.createQuery(queryStr);
		if (parameters != null) {
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter((String) entry.getKey(), entry.getValue());
			}
		}
		return query;
	}

	public <A extends AbstractPojo> A find(String queryStr, Map<String, Object> parameters) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createQuery(queryStr);
			if (parameters != null) {
				for (Map.Entry<String, Object> entry : parameters.entrySet()) {
					query.setParameter((String) entry.getKey(), entry.getValue());
				}
			}
			return (A) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public void store(AbstractPojo pojo) {
		EntityManager em = getEntityManager();
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		if (pojo.getId() != null) {
			em.merge(pojo);
		} else {
			em.persist(pojo);
		}
		em.getTransaction().commit();
	}

	public <A extends AbstractPojo> void storeAll(Collection<A> pojos) {
		EntityManager em = getEntityManager();

		em.getTransaction().begin();
		for (AbstractPojo pojo : pojos) {
			if (pojo.getId() != null) {
				em.merge(pojo);
			} else {
				em.persist(pojo);
			}
		}
		em.getTransaction().commit();
	}

	public void delete(AbstractPojo pojo) {
		if (pojo == null) {
			throw new IllegalArgumentException("Null values are not accepted");
		}
		if (pojo.getId() == null) {
			return;
		}
		EntityManager em = getEntityManager();

		em.getTransaction().begin();

		Object entity = em.find(pojo.getClass(), pojo.getId());

		em.remove(entity);

		em.getTransaction().commit();
	}

	public <A extends AbstractPojo> void deleteAll(Collection<A> pojos) {
		if (pojos == null) {
			throw new IllegalArgumentException("Null values are not accepted");
		}
		EntityManager em = getEntityManager();

		em.getTransaction().begin();
		for (A pojo : pojos) {
			if (pojo.getId() != null) {
				Object entity = em.find(pojo.getClass(), pojo.getId());
				if (entity != null) {
					em.remove(entity);
				}
			}
		}
		em.getTransaction().commit();
	}

	public <A extends AbstractPojo> List<A> list(Class<A> clazz, int startIndex, int amount) {
		EntityManager em = getEntityManager();

		Query query = generateQuery(clazz, em);
		query.setFirstResult(startIndex).setMaxResults(amount);

		return query.getResultList();
	}

	public <A extends AbstractPojo> List<A> list(String queryStr, Map<String, Object> parameters, int startIndex,
			int amount) {
		EntityManager em = getEntityManager();

		Query query = generateQuery(queryStr, parameters, em);

		query.setFirstResult(startIndex).setMaxResults(amount);

		return query.getResultList();
	}

	public Long count(Class<? extends AbstractPojo> c) {
		if (c == null) {
			throw new IllegalArgumentException("Class may not be null");
		}
		EntityManager em = getEntityManager();
		try {
			String queryStr = "SELECT COUNT(p.id) FROM " + c.getSimpleName() + " p";

			Query query = em.createQuery(queryStr);

			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return Long.valueOf(-1L);
	}

	public Long count(Class<? extends AbstractPojo> c, String whereClause, Map<String, Object> parameters) {
		if (c == null) {
			throw new IllegalArgumentException("Class may not be null");
		}
		if (whereClause == null) {
			throw new IllegalArgumentException("Where clause may not be null");
		}
		EntityManager em = getEntityManager();
		try {
			String queryStr = "SELECT COUNT(p.id) FROM " + c.getSimpleName() + " p WHERE " + whereClause;

			Query query = generateQuery(queryStr, parameters, em);

			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return Long.valueOf(-1L);
	}

	public List<?> getFieldValues(Class<? extends AbstractPojo> c, String field, String whereConditions,
			Map<String, Object> parameters) {
		String queryStr = createSelectFieldQuery(c, field, whereConditions);
		EntityManager em = getEntityManager();
		Query query = generateQuery(queryStr, parameters, em);

		return query.getResultList();
	}

	private String createSelectFieldQuery(Class<? extends AbstractPojo> c, String field, String whereConditions) {
		String queryStr = "SELECT p." + field + " FROM " + c.getSimpleName() + " p";
		if (whereConditions != null) {
			queryStr = queryStr + " WHERE " + whereConditions;
		}
		return queryStr;
	}

	public Long count(String queryStr, Map<String, Object> parameters) {
		EntityManager em = getEntityManager();
		Query query = generateQuery(queryStr, parameters, em);

		List list = query.getResultList();
		if (list.size() == 0) {
			return Long.valueOf(0L);
		}
		return (Long) list.get(0);
	}
	
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <A extends AbstractPojo> void refresh(A pojo) {
        // A boolean which defines if we should close the EntityManager after
        // the refresh is done.
        boolean closeEm = false;

        // If this method is called independently, in other words, not from
        // within this facade, then we should close the entity manager after the
        // refresh is done. However, if it is called from within this facade,
        // then we should have and open entity manager and the calling method
        // will take care of closing the em.
        //if (em.get() == null || !em.get().isOpen()) {
        //    closeEm = true;
       // }
        // Get the EntityManager
        EntityManager em = getEntityManager();

        // Get a fresh instance of the object.
        A pojo2 = (A) em.find(pojo.getClass(), pojo.getId());
        // Make sure its state is up-to-date
        em.refresh(pojo2);

        // Now copy all fields' values from pojo2 back to pojo
        copyFieldsRecursively(pojo, pojo2, pojo.getClass());

        // Close the em if necessary
        if (closeEm) {
            em.close();
        }
    }


    /**
     * Copies all field values recursively from pojo2 to pojo
     * 
     * @param pojo
     *            The object to which we are copying
     * @param pojo2
     *            The object from which we are copying
     * @param c
     *            The class we are currently processing
     */
    private <A extends AbstractPojo> void copyFieldsRecursively(A pojo,
            A pojo2, Class<?> c) {
        // if class is null, then there is nothing left to copy
        if (c != null) {
            // Get all the fields in this class. NOTE! getDeclaredFields() will
            // return all fields whether they are private or not. However,
            // getDeclaredFields() will only return the fields defined in
            // Class<?> c, and not the fields in its superclass(es). Hence, we
            // need to perform this method recursively to be able to get the
            // superclasses' field values too.
            Field[] fields = c.getDeclaredFields();

            // Loop through all fields
            for (Field field : fields) {
                // If the field is transient, static or final, then we do not
                // want to copy its value
                if (!field.isAnnotationPresent(Transient.class)
                        && !Modifier.isStatic(field.getModifiers())
                        && !Modifier.isFinal(field.getModifiers())) {

                    // The field might be inaccessible, so let's force it to be
                    // accessible.
                    field.setAccessible(true);
                    try {
                        // Copy the value from pojo2 to pojo
                        field.set(pojo, field.get(pojo2));
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            // Now perform the same copying of fields to this class's
            // superclass.
            copyFieldsRecursively(pojo, pojo2, c.getSuperclass());
        }
    }
}
