/**
 * 
 */
package tavant.twms.web.admin.groups.user;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.orgmodel.UserScheme;
import tavant.twms.domain.orgmodel.UserSchemeRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class ListUserSchemes extends SummaryTableAction {
    
    protected static Logger logger = LogManager.getLogger(ListUserSchemes.class);

    private UserSchemeRepository userSchemeRepository;
    
    @Override
	protected PageResult<?> getBody() {        
        
        PageResult<UserScheme> pageResult = userSchemeRepository.findPage(
        										"from UserScheme userScheme", getCriteria());
        return pageResult;
    }

    @Override
	protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.manageGroup.scheme",
        		"name", 100, "string", true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
        		"id",0, "String", false, true, true, false));
        return tableHeadData;
    }
    
	public void setUserSchemeRepository(UserSchemeRepository userSchemeRepository) {
		this.userSchemeRepository = userSchemeRepository;
	}

    
}
