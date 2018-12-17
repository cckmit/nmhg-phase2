/**
 * 
 */
package tavant.twms.web.admin.lov;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 *
 */
@SuppressWarnings("serial")
public class ListLOV extends SummaryTableAction {
	protected static Logger logger = LogManager.getLogger(ListLOV.class);
	
	private String lovTypeName;
    
    private LovRepository lovRepository;
	
	@Override
    public PageResult<?> getBody() {       
        ListCriteria criteria = getCriteria();
        if (getCapitalisedLOVName().equals("DocumentType")) {			
			criteria.addFilterCriteria("type", "DOCUMENTTYPE");
			PageResult<ListOfValues> pageResultForDocumentType = lovRepository
					.findAll(getCapitalisedLOVName(), criteria);
			return pageResultForDocumentType;
		} else {
			PageResult<ListOfValues> pageResult = lovRepository.findAll(
					getCapitalisedLOVName(), getCriteria());
			return pageResult;
		}
    }
    
	@Override
    public List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.listOfValues.code",
        		"code", 30, "string", true, true, true, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.description",
        		"description", 50, "string"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.status",
        		"state", 20, "string"));
        return tableHeadData;
    }
    
    
    public String detail() throws Exception {
        return SUCCESS;
    }
         
    public String preview() {
        return SUCCESS;
    }

	@Required
	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}
	
	public String getCapitalisedLOVName() {
		return StringUtils.capitalize(lovTypeName);
	}

	public void setLovTypeName(String lovName) {
		this.lovTypeName = lovName;
	}

	public String getLovTypeName() {
		return lovTypeName;
	}
}
