package com.globe_sh.cloudplatform.server.base;

public interface BaseDAO<T, ID> {

	void insert(T t);

	int delete(ID id);

	int update(T t);

	<V extends T> V select(ID id);
}
