package tavant.twms.web.thirdparty;

import org.apache.struts2.interceptor.ServletResponseAware;
import tavant.twms.domain.orgmodel.ThirdParty;
import tavant.twms.domain.thirdparty.ThirdPartySearch;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * @author priyank.gupta
 *
 */
@SuppressWarnings("serial")
public class ThirdPartySearchAction extends I18nActionSupport implements ServletResponseAware
{
	private ThirdPartySearch thirdPartySearch;
	private ListCriteria listCriteria;
	private List<ThirdParty> thirdParties=new ArrayList<ThirdParty>();
	private List<Integer> pageNoList = new ArrayList<Integer>();
	private int totalpages;
    private Integer pageNo=new Integer(0);  
    private int nextCounter=0;

    
    public String geThirdParties()
    {
    	//set the list criteria first so that we do not miss on our priorities of sorting the list and 
    	//pagination of course!!
    	setListCriteria();
    	
    	//define the page result
    	PageResult<ThirdParty> pageResult = null;
    	
    	//call the search api to retrieve all 3rd parties or selective parties based on criteria
    	pageResult = this.orgService.findThirdPartyByNumberOrName(this.thirdPartySearch, getListCriteria());
    	
    	//set the page number attribute for the paginated search
    	setTotalpages(pageResult.getNumberOfPagesAvailable());
    	setThirdParties(pageResult.getResult());
    	
    	//set the page number list correctly
    	for (int i=0;i<pageResult.getNumberOfPagesAvailable();i++)
    	{		        	
        	this.pageNoList.add(new Integer(i+1));
        	if(getNextCounter()!=-1 && i==9){
        		break;
        	}
        }
    	
    	//return a success people! We have got with us what we want.
    	return SUCCESS;
    }
    
	/**
	 * This method sets the list criteria which is used in forming the query that needs to be fired to 
	 * fetch all the third parties in a paginated format.
	 */
	public void setListCriteria() 
	{
		ListCriteria criteria = new ListCriteria();
		criteria.addSortCriteria("name", true);
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageNumber(this.pageNo.intValue());
		pageSpecification.setPageSize(50);
		criteria.setPageSpecification(pageSpecification);
		this.listCriteria=criteria;
	}
    
    
	/**
	 * @return
	 */
	public ThirdPartySearch getThirdPartySearch() 
	{
		return thirdPartySearch;
	}

	/**
	 * @param thirdPartySearch
	 */
	public void setThirdPartySearch(ThirdPartySearch thirdPartySearch) 
	{
		this.thirdPartySearch = thirdPartySearch;
	}

	/**
	 * @return
	 */
	public ListCriteria getListCriteria() 
	{
		return listCriteria;
	}

	/**
	 * @param listCriteria
	 */
	public void setListCriteria(ListCriteria listCriteria) 
	{
		this.listCriteria = listCriteria;
	}

	public List<Integer> getPageNoList() 
	{
		return pageNoList;
	}

	public void setPageNoList(List<Integer> pageNoList) 
	{
		this.pageNoList = pageNoList;
	}

	public int getTotalpages() 
	{
		return totalpages;
	}

	public void setTotalpages(int totalpages) 
	{
		this.totalpages = totalpages;
	}

	public Integer getPageNo() 
	{
		return pageNo;
	}

	public void setPageNo(Integer pageNo) 
	{
		this.pageNo = pageNo;
	}

	public List<ThirdParty> getThirdParties() 
	{
		return thirdParties;
	}

	public void setThirdParties(List<ThirdParty> thirdParties) 
	{
		this.thirdParties = thirdParties;
	}

	public int getNextCounter()
	{
		return nextCounter;
	}

	public void setNextCounter(int nextCounter) 
	{
		this.nextCounter = nextCounter;
	}
	
	
	
	
}
