package com.globe_sh.cloudplatform.server.base;

public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    public abstract BaseDAO<T, ID> getDAO();

    public void insert(T model) {
    	getDAO().insert(model);
    }

    public int update(T model) {
        return getDAO().update(model);
    }

    public int delete(ID id) {
        return getDAO().delete(id);
    }

    public T select(ID id) {
        return getDAO().select(id);
    }
}
