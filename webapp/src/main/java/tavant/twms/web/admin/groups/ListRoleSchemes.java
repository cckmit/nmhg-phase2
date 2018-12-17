package tavant.twms.web.admin.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.orgmodel.RoleSchemeRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

public class ListRoleSchemes extends SummaryTableAction {

	private static final long serialVersionUID = 1L;

	protected static Logger logger = LogManager
			.getLogger(ListRoleSchemes.class);

	private RoleSchemeRepository roleSchemeRepository;

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.manageGroup.scheme", "name", 100, "string", true,
				false, false, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.id", "id",
				0, "String", false, true, true, false));
		return tableHeadData;
	}

	@Override
	protected PageResult<?> getBody() {
		return roleSchemeRepository.findPage("from RoleScheme roleScheme",
				getCriteria());
	}

	public RoleSchemeRepository getRoleSchemeRepository() {
		return roleSchemeRepository;
	}

	public void setRoleSchemeRepository(
			RoleSchemeRepository roleSchemeRepository) {
		this.roleSchemeRepository = roleSchemeRepository;
	}

}
