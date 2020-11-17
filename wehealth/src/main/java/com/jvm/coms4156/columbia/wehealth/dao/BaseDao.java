package com.jvm.coms4156.columbia.wehealth.dao;


import javax.persistence.TypedQuery;

/**
 * Created by Ethan on 11/04/2020.
 */
public abstract class BaseDao {

  <T> T single(TypedQuery<T> in) {
    return in.getResultList().stream().findFirst().orElse(null);
  }

}

