package tavant.twms.web.admin.payment;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.claim.payment.rates.PartPricesRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

@SuppressWarnings("serial")
public class ListPartPrices extends SummaryTableAction {
	private PartPricesRepository partPricesRepository;
	
	public void setPartPricesRepository(PartPricesRepository partPricesRepository) {
		this.partPricesRepository = partPricesRepository;
	}

	@Override
	protected PageResult<?> getBody() {
		return partPricesRepository.findPage("from PartPrices " + getAlias(), getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
				"id", 0, "String", "id", true, true, true, false));
 		
 		tableHeadData.add(new SummaryTableColumn("label.nmhg.part.number", "nmhg_part_number.number",
 	                15, SummaryTableColumn.STRING, "nmhg_part_number.number",false,false,false,false));
 		tableHeadData.add(new SummaryTableColumn("label.miscellaneous.partDescription", "nmhg_part_number.description",
                15, SummaryTableColumn.STRING, "nmhg_part_number.description",false,false,false,false));
		tableHeadData.add(new SummaryTableColumn("label.common.comments", "comments",
	                15, SummaryTableColumn.STRING, "comments",false,false,false,false));
		tableHeadData.add(new SummaryTableColumn("label.part.price.lastUpdatedBy", "syncType",
                15, SummaryTableColumn.STRING, "d.lastUpdatedBy.getName()",false,false,false,false));
		return tableHeadData;
	}

	@Override
	protected String getAlias() {
		return "config";
	}
	
	
}
