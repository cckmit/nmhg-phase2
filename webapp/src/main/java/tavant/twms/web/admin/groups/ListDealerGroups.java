/**
 * 
 */
package tavant.twms.web.admin.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.DealerScheme;
import tavant.twms.domain.orgmodel.DealerSchemeService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import com.opensymphony.xwork2.Preparable;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@SuppressWarnings("serial")
public class ListDealerGroups extends SummaryTableAction implements Preparable {

	protected static Logger logger = LogManager
			.getLogger(ListDealerGroups.class);

	private DealerGroupService dealerGroupService;

	private DealerSchemeService dealerSchemeService;

	private String schemeId;

	private DealerScheme dealerScheme;

	private ConfigParamService configParamService;

	private Boolean dealerGroupConfigParam;

	private static final String DEALER_GROUP_CODE = "dealerGroupCode";

	public void prepare() throws Exception {
		Long idToBeUsed = getIdToBeUsed();
		dealerScheme = (idToBeUsed != null) ? dealerSchemeService
				.findById(idToBeUsed) : null;
	    /*if(dealerGroupConfigParam == null)			
		    dealerGroupConfigParam = this.configParamService.getBooleanValue(DEALER_GROUP_CODE);
		*/
	}

	private Long getIdToBeUsed() {
		if (schemeId != null) {
			return Long.parseLong(schemeId);
		} else if (dealerScheme != null) {
			return dealerScheme.getId();
		}
		return null;
	}

	public Long getComputedSchemeId() {
		return (dealerScheme != null) ? dealerScheme.getId() : null;
	}

	@Override
	protected PageResult<?> getBody() {
		PageResult<DealerGroup> pageResult = dealerGroupService.findPage(
				getCriteria(), dealerScheme);
		return pageResult;

	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.manageGroup.groupName", "name", 30, "string",
				true, false, false, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.common.description", "description", 60, "string"));
		//if (dealerGroupConfigParam) {
		//	tableHeadData.add(new SummaryTableColumn("columnTitle.common.code",
		//			"code", 20, "string"));
		//}
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.id", "id",
				0, "String", false, true, true, false));
		/*tableHeadData.add(new SummaryTableColumn("", "imageCol", 10, IMAGE,
				"labelsImg", false, false, false, false));*/
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

	public DealerScheme getDealerScheme() {
		return dealerScheme;
	}

	public void setDealerScheme(DealerScheme dealerScheme) {
		this.dealerScheme = dealerScheme;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public void setDealerSchemeService(DealerSchemeService dealerSchemeService) {
		this.dealerSchemeService = dealerSchemeService;
	}

	public Boolean getDealerGroupConfigParam() {
		return dealerGroupConfigParam;
	}

	public void setDealerGroupConfigParam(Boolean dealerGroupConfigParam) {
		this.dealerGroupConfigParam = dealerGroupConfigParam;
	}

	
}
