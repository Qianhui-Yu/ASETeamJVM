package com.jvm.coms4156.columbia.wehealth.dao;

import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by emagi on 7/31/2019.
 */
@Repository
public class AppUserDao extends BaseDao {

  @PersistenceContext
  private EntityManager entityManager;

  public DBUser findByUserId(Long id) {
    return single(entityManager.createQuery("from user where user_id = :id", DBUser.class).setParameter("id", id));
  }

  public DBUser findByUsername(String username) {
    return single(entityManager.createQuery("from user where username = :u", DBUser.class).setParameter("u", username));
  }

  public DBUser findByLookupToken(String lookupToken) {
    return single(entityManager.createQuery("from user where lookup_token = :lookupToken", DBUser.class).setParameter("lookupToken", lookupToken));
  }

  public void save(DBUser in) {
    entityManager.persist(in);
  }

  public void delete(DBUser remove) {
    entityManager.remove(remove);
  }

}
