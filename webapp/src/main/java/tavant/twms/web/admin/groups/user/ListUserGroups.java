/**
 * 
 */
package tavant.twms.web.admin.groups.user;

import com.opensymphony.xwork2.Preparable;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.orgmodel.UserScheme;
import tavant.twms.domain.orgmodel.UserSchemeService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.admin.groups.GroupsMemberTypeResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;
import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 *
 */
@SuppressWarnings("serial")
public class ListUserGroups extends SummaryTableAction implements Preparable{
    
    protected static Logger logger = LogManager.getLogger(ListUserGroups.class);
    private UserClusterService userClusterService;
    private UserSchemeService userSchemeService;
    private String schemeId;
    private UserScheme userScheme;    
    
	public void prepare() throws Exception {
        Long idToBeUsed = getIdToBeUsed();
        userScheme = (idToBeUsed != null) ? 
        		userSchemeService.findById(idToBeUsed) : null;
    }
	
	private Long getIdToBeUsed() {
		if(schemeId != null) {
            return Long.parseLong(schemeId);
        } else if(userScheme != null) {
            return userScheme.getId();
        }
		return null;
	}
	
	public Long getComputedSchemeId() {
        return (userScheme != null) ? 
        		userScheme.getId() : null;
	}
	
    @Override
    protected PageResult<?> getBody() {       
        PageResult<UserCluster> pageResult = userClusterService.findPage(
        												getCriteria(), userScheme);
        return pageResult;
    }
    
    @Override
	protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.manageGroup.groupName",
        		"name", 20, "string", true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.description",
        		"description", 70, "string"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
        		"id",0, "String", false, true, true, false));
        tableHeadData.add(new SummaryTableColumn("",
				"imageCol", 10, IMAGE, "labelsImg", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        return tableHeadData;
    }      
    
    @Override
	protected BeanProvider getBeanProvider() {
		return new GroupsMemberTypeResolver();
	}
    
    public String detail() throws Exception {
        return SUCCESS;
    }
         
    public String preview() {
        return SUCCESS;
    }
     
    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

	public UserScheme getUserScheme() {
		return userScheme;
	}

	public void setUserScheme(UserScheme userScheme) {
		this.userScheme = userScheme;
	}

	public void setUserClusterService(UserClusterService userClusterService) {
		this.userClusterService = userClusterService;
	}

	public void setUserSchemeService(UserSchemeService userSchemeService) {
		this.userSchemeService = userSchemeService;
	}

    
}
