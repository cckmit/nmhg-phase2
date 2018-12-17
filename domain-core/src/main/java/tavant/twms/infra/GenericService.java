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

import org.springframework.transaction.annotation.Transactional;


/**
 * @author radhakrishnan.j
 *
 */
@Transactional(readOnly=true)
public interface GenericService<T, ID extends Serializable, EX extends Exception> {
    @Transactional(readOnly=false)
    public void save(T t);

    @Transactional(readOnly=false)
    public void update(T t);

    @Transactional(readOnly=false)
    public void delete(T t);

    public T findById(ID id);

    public List<T> findByIds(Collection<ID> collectionOfIds);

    public List<T> findAll();

    public PageResult<T> findAll(PageSpecification pageSpecification);

    @Transactional(readOnly=false)
    public void deleteAll(List<T> entitiesToDelete);
}