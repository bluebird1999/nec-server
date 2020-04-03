package com.globe_sh.cloudplatform.server.base;

public interface BaseService<T, ID> {

	void insert(T t);
	
    int update(T t);

    int delete(ID id);

    T select(ID id);
}
