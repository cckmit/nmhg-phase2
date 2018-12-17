package tavant.twms.web.admin.supplier;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

@SuppressWarnings("serial")
public class ListSuppliers extends SummaryTableAction {

    SupplierService supplierService;

    @Override
    protected PageResult<?> getBody() {
        return supplierService.findAllSuppliers(getCriteria());
    }

    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("id", "id", 0, "String", "id", true, true, true, false));
        tableHeadData.add(new SummaryTableColumn("Supplier Number", "supplierNumber", 15, "String", "supplierNumber",
                true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.listContracts.supplier_name", "name", 20, "String"));
        /**
         * Commenting below line as contact person name has been removed
         */
        /*tableHeadData.add(new SummaryTableColumn("Contact Person Name", "supplierAddress.contactPersonName", 20,
               "String")); */
        tableHeadData.add(new SummaryTableColumn("Address", "displayAddress", 45, "String"));
        return tableHeadData;
    }

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

}
