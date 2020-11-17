package com.jvm.coms4156.columbia.wehealth.dao;

import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;


/**
 * Created by emagi on 7/31/2019.
 */
@Repository
public class AppUserDao extends BaseDao {

  @PersistenceContext
  private EntityManager entityManager;

  public DbUser findByUserId(Long id) {
    return single(entityManager.createQuery("from user where user_id = :id", DbUser.class)
            .setParameter("id", id));
  }

  public DbUser findByUsername(String username) {
    return single(entityManager.createQuery("from user where username = :u", DbUser.class)
            .setParameter("u", username));
  }

  public DbUser findByLookupToken(String lookupToken) {
    return single(entityManager.createQuery("from user where lookup_token = :lookupToken",
            DbUser.class).setParameter("lookupToken", lookupToken));
  }

  public void save(DbUser in) {
    entityManager.persist(in);
  }

  public void delete(DbUser remove) {
    entityManager.remove(remove);
  }

}
