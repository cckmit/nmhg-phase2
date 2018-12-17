package tavant.twms.web.uom;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.uom.UomMappingsService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

@SuppressWarnings("serial")
public class ListUoms extends SummaryTableAction {

	private UomMappingsService uomMappingsService;
	
	@Override
	protected PageResult<?> getBody() {
		return uomMappingsService.findPage(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "String", "id",
				false, true, true, false));		
		tableHeadData.add(new SummaryTableColumn("columnTitle.uom.baseUom",
				"baseUom", 40, "String","baseUom",
				true,false,false,false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.uom.mappedUom",
				"mappedUom", 40, "String"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.uom.mappingFraction",
				"mappingFraction", 20, "Number",SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));

		return tableHeadData;
	}

	public void setUomMappingsService(UomMappingsService uomMappingsService) {
		this.uomMappingsService = uomMappingsService;
	}
	

}
