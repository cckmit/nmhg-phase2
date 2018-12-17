/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.infra;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author radhakrishnan.j
 *
 */
public abstract class GenericServiceImpl<T,ID extends Serializable,EX extends Exception> implements GenericService<T,ID,EX> {
    public abstract GenericRepository<T,ID> getRepository();

    public void delete(T t) {
        getRepository().delete(t);
    }

    public void deleteAll(List<T> entitiesToDelete) {
	        getRepository().deleteAll(entitiesToDelete);
    }

    public List<T> findAll() {
        return getRepository().findAll();
    }

    public PageResult<T> findAll(PageSpecification pageSpecification) {
        return getRepository().findAll(pageSpecification);
    }

    public T findById(ID id) {
        return getRepository().findById(id);
    }

    public List<T> findByIds(Collection<ID> collectionOfIds) {
        return getRepository().findByIds(collectionOfIds);
    }

    public void save(T t) {
        getRepository().save(t);
    }

    public void update(T t) {
        getRepository().update(t);
    }

}
