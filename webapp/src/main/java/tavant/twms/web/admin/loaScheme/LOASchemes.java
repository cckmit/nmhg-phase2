package tavant.twms.web.admin.loaScheme;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.loa.LimitOfAuthoritySchemeService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

public class LOASchemes extends SummaryTableAction {

	public static final Logger logger = Logger.getLogger(LOASchemes.class);

	private LimitOfAuthoritySchemeService loaService;
	
	private List<LimitOfAuthorityScheme> loaSchemes = new ArrayList<LimitOfAuthorityScheme>();


    @Override
	protected PageResult<?> getBody() {
		return loaService.findAllLOASchemes(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn(
				"columnTitle.manageLOAScheme.loaSchemeCode", "code", 15,
				"string", true, false, false, false));
		header.add(new SummaryTableColumn(
				"columnTitle.manageLOAScheme.loaSchemeName", "name",
				35, "string", true, false, false, false));
		header.add(new SummaryTableColumn(
				"columnTitle.manageLOAScheme.loaSchemeDes", "description",
				25, "string", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
        
        header.add(new SummaryTableColumn("columnTitle.common.id", "id", 0,
				"string", false, true, true, false));
		return header;
	}

	public void setLoaService(LimitOfAuthoritySchemeService loaService) {
		this.loaService = loaService;
	}

	public List<LimitOfAuthorityScheme> getLoaSchemes() {
		return loaSchemes;
	}

	public void setLoaSchemes(List<LimitOfAuthorityScheme> loaSchemes) {
		this.loaSchemes = loaSchemes;
	}
	
}
