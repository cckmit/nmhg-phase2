/**
 * 
 */
package tavant.twms.web.admin.faultcode;

import com.opensymphony.xwork2.Preparable;
import tavant.twms.domain.failurestruct.AssemblyLevel;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.common.LabelsPropertyResolver;
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
public class ListFaultCodesAction extends SummaryTableAction implements
		Preparable {

	private FailureStructureService failureStructureService;

	private List<AssemblyLevel> assemblyLevels;

	public void prepare() throws Exception {
		assemblyLevels = failureStructureService.findAllAssemblyLevels();
	}

	@Override
	protected PageResult<?> getBody() {
		return failureStructureService
				.findAllFaultCodeDefinitions(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		// Id
		header.add(new SummaryTableColumn("columnTitle.common.Hidden",
				"id", 0, "string", "id", false, true, true, false));
		// Label for Detail Tab
		header.add(new SummaryTableColumn("columnTitle.common.Hidden",
				"label", 0, "string", "faultCode", true, false, true, false));

		// Columns
		header.add(new SummaryTableColumn("columnTitle.common.FullCode",
				"code", 14, "string","code",true,false,false,false));
		int colWidth = 80 / assemblyLevels.size();
		for (int i = 0; i < assemblyLevels.size(); i++) {
            //TODO : please refractor this extremely HACKY code.... i18ing of the title is taken care of by the superclass.
            // TODO : see if this can be i18n'ed in some way or the other.
			header.add(new SummaryTableColumn("title.common."+assemblyLevels.get(i)
					.getName(), "components[" + i  + "].name", colWidth,
					"string")); 
		}
		
		header.add(new SummaryTableColumn("","imageCol", 6, IMAGE, "labelsImg", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));

		return header;
	}
	
	@Override
	protected BeanProvider getBeanProvider() {
		return new LabelsPropertyResolver();
	}

	public String showFaultCode() {
		return SUCCESS;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public List<AssemblyLevel> getAssemblyLevels() {
		return assemblyLevels;
	}

	public void setAssemblyLevels(List<AssemblyLevel> assemblyLevels) {
		this.assemblyLevels = assemblyLevels;
	}
	
}
