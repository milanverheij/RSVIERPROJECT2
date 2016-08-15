package com.adm.database.daos;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.adm.database.interfaces.ArtikelGeneriekeDAOInterface;
import com.adm.domain.Artikel;

public class ArtikelDAONietGeneriek implements ArtikelGeneriekeDAOInterface<Artikel, Long> {
	
	private static EntityManagerFactory entityManagerFactory = null;
	
	private EntityManager createEntityManager() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("rsvierproject2PU");
		}
		return entityManagerFactory.createEntityManager();
	}
	
	public ArtikelDAONietGeneriek() {
	}

	@Override
	public void persist(Artikel entity) {
		EntityManager entityManager = createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(entity);
		entityManager.getTransaction().commit();
		entityManager.close();
		
	}
	
	@Override
	public void update(Artikel entity) {
		EntityManager entityManager = createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.merge(entity);
		entityManager.getTransaction().commit();
		entityManager.close();
		
	}
	@Override
	public Artikel findById(Long id) {
		EntityManager entityManager = createEntityManager();
		entityManager.getTransaction().begin();
		Artikel artikel = (Artikel) entityManager.find(Artikel.class, id);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return artikel;
	}

	@Override
	public void delete(Artikel entity) {
		EntityManager entityManager = createEntityManager();
		entityManager.getTransaction().begin();
		Artikel artikel = entityManager.find(Artikel.class, entity.getId());
		entityManager.remove(entity);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public Set<Artikel> findAll() {
		EntityManager entityManager = createEntityManager();
		entityManager.getTransaction().begin();
		TypedQuery<Artikel> query = entityManager.createQuery("SELECT a FROM ARTIKEL a ", Artikel.class);
		Set<Artikel> resultaten = (Set<Artikel>)query.getResultList();
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return resultaten;
	}

	@Override
	public void deleteALl() {
		Set<Artikel> artikelLijst = findAll();
		for (Artikel artikel : artikelLijst) {
			delete(artikel);
		}
		
	}
}