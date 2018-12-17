/**
 * 
 */
package tavant.twms.domain.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author aniruddha.chaturvedi
 *
 */
public interface LovRepository {
	public void setRepoMap(Map<String, GenericRepository<ListOfValues, Long>> map);
	
	public void setClassMap(Map<String, Class> map);
	
	public Class getClassFromClassName(String className);
	
	@Transactional(readOnly=false)    
	public void save(ListOfValues listOfValues);
	
	@Transactional(readOnly=false)    
	public void update(ListOfValues listOfValues);

	@Transactional(readOnly=false)    
    public void delete(ListOfValues listOfValues);

    public ListOfValues findById(String className, Long id);

    public List<ListOfValues> findByIds(String className, Collection<Long> collectionOfIds);

    public List<ListOfValues> findAll(String className);

    public PageResult<ListOfValues> findAll(String className, ListCriteria criteria);
    
    public List<ListOfValues> findAllActive(String className);
    
    public ListOfValues findByCode(String className, String code);
    
    public ListOfValues findByCodeWithoutBU(final String className, final String code);
    
    public ListOfValues findActiveValuesByCode(String className, String code);
    
    public ListOfValues findByCode(String className, String code, String bu);
    
    public List<ListOfValues> findByCodes(String className, Collection<String> collectionOfIds);
    
    public ListOfValues findByDescription(final String className, final String descr , final String locale ,final String bu);

}
