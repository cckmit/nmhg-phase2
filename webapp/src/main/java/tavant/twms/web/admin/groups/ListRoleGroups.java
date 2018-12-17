/**
 * 
 */
package tavant.twms.web.admin.groups;

import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.orgmodel.RoleGroupService;
import tavant.twms.domain.orgmodel.RoleScheme;
import tavant.twms.domain.orgmodel.RoleSchemeRepository;
import tavant.twms.domain.orgmodel.RoleSchemeService;
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
public class ListRoleGroups extends SummaryTableAction implements Preparable {

	protected static Logger logger = LogManager.getLogger(ListRoleGroups.class);

	private RoleGroupService roleGroupService;

	private RoleSchemeService roleSchemeService;

	private RoleSchemeRepository roleSchemeRepository;

	private String schemeId;

	private RoleScheme roleScheme;

	public void prepare() throws Exception {
		Long idToBeUsed = getIdToBeUsed();
		roleScheme = (idToBeUsed != null) ? roleSchemeService
				.findById(idToBeUsed) : null;
	}

	private Long getIdToBeUsed() {
		if (schemeId != null) {
			return Long.parseLong(schemeId);
		} else if (roleScheme != null) {
			return roleScheme.getId();
		}
		return null;
	}

	public Long getComputedSchemeId() {
		return (roleScheme != null) ? roleScheme.getId() : null;
	}

	@Override
	protected PageResult<?> getBody() {
		return roleSchemeRepository.findPage("from RoleGroup roleGroup",
				getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.manageGroup.groupName", "name", 20, "string",
				true, false, false, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.common.description", "description", 70, "string"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.id", "id",
				0, "String", false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("", "imageCol", 10, IMAGE,
				"labelsImg", false, false, false, false));
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

	public RoleGroupService getRoleGroupService() {
		return roleGroupService;
	}

	public void setRoleGroupService(RoleGroupService roleGroupService) {
		this.roleGroupService = roleGroupService;
	}

	public RoleScheme getRoleScheme() {
		return roleScheme;
	}

	public void setRoleScheme(RoleScheme roleScheme) {
		this.roleScheme = roleScheme;
	}

	public RoleSchemeService getRoleSchemeService() {
		return roleSchemeService;
	}

	public void setRoleSchemeService(RoleSchemeService roleSchemeService) {
		this.roleSchemeService = roleSchemeService;
	}

	public RoleSchemeRepository getRoleSchemeRepository() {
		return roleSchemeRepository;
	}

	public void setRoleSchemeRepository(RoleSchemeRepository roleSchemeRepository) {
		this.roleSchemeRepository = roleSchemeRepository;
	}

}
