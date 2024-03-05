package com.zcunsoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    <S extends T> Iterable<S> batchInsert(Iterable<S> var1);
    <S extends T> Iterable<S> batchUpdate(Iterable<S> var1);

    int delById(String tableName, Long id);
}
