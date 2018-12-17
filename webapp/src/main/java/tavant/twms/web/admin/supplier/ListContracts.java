package tavant.twms.web.admin.supplier;

import tavant.twms.domain.supplier.contract.ContractRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;
import tavant.twms.infra.ListCriteria;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class ListContracts extends SummaryTableAction {

	private ContractRepository contractRepository;

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
//                Fix for TWMS4.3-708
		tableHeadData.add(new SummaryTableColumn("columnTitle.listContracts.contract_code",
				"id", 15, SummaryTableColumn.NUMBER, "id", true, true, false, false));
		tableHeadData
				.add(new SummaryTableColumn(
						"columnTitle.listContracts.contract_name", "name", 21,
						"String"));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.listContracts.supplier_name", "supplier.name", 21,
				"String"));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.listContracts.validity_from",
				"validityPeriod.fromDate", 17, "String"));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.listContracts.validity_to",
				"validityPeriod.tillDate", 20, "String"));
		return tableHeadData;
	}
//  Fix for TWMS4.3-708
    @Override
        protected ListCriteria getListCriteria(){
            return new ListCriteria(){
            @Override
                protected boolean isNumberProperty(String propertyName){
                    return "contract.id".equals(propertyName);
                }
            };
        }

	@Override
	protected PageResult<?> getBody() {
		return contractRepository.findPage("from Contract " + getAlias(),
				getCriteria());
	}

	@Override
	protected String getAlias() {
		return "contract";
	}

	public void setContractRepository(ContractRepository contractRepository) {
		this.contractRepository = contractRepository;
	}
}
