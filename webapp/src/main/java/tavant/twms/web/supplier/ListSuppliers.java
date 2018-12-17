/**
 * 
 */
package tavant.twms.web.supplier;

import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.common.LabelsPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

/**
 * @author mritunjay.kumar
 * 
 */
public class ListSuppliers extends SummaryTableAction {

	private SupplierService supplierService;

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("columnTitle.supplier.name", "name",
				14, "string", "name"));
		header.add(new SummaryTableColumn(
				"columnTitle.supplier.supplierNumber", "supplierNumber", 14,
				"string", "supplierNumber", true, false, false, false));
		header.add(new SummaryTableColumn("columnTitle.supplier.hidden", "id",
				0, "string", "id", false, true, true, false));
		header.add(new SummaryTableColumn("columnTitle.supplier.address",
				"address.addressLine1", 20, "string"));
		header.add(new SummaryTableColumn("columnTitle.supplier.city",
				"address.city", 12, "string"));
		header.add(new SummaryTableColumn("columnTitle.supplier.state",
				"address.state", 12, "string"));
		header.add(new SummaryTableColumn("columnTitle.supplier.country",
				"address.country", 12, "string"));
		header.add(new SummaryTableColumn("columnTitle.supplier.zip",
				"address.zipCode", 10, "string"));
		header.add(new SummaryTableColumn("label.supplier.labelNames", "imageCol", 6, IMAGE,
				"labelsImg", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));

		return header;
	}

	@Override
	protected PageResult<?> getBody() {
		return supplierService.findAllSuppliers(getCriteria());
	}

	@Override
	protected String getAlias() {
		return "supplier";
	}

	@Override
	protected BeanProvider getBeanProvider() {
		return new LabelsPropertyResolver();
	}

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}
	
}
