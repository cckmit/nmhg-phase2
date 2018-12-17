/**
 * 
 */
package tavant.twms.web.admin.warehouse;

import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.partreturn.Warehouse;
import tavant.twms.domain.partreturn.WarehouseRepository;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.web.common.LabelsPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 *
 */
@SuppressWarnings("serial")
public class ListWarehouses extends SummaryTableAction {

	protected static Logger logger = LogManager.getLogger(ListWarehouses.class);
    public static final String CREATE_WAREHOUSE = "create_warehouse";
    
    private WarehouseRepository warehouseRepository;
    
    private WarehouseService warehouseService;
    
    public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	public void setWarehouseRepository(WarehouseRepository warehouseRepository) {
		this.warehouseRepository = warehouseRepository;
	}

    @Override
    protected PageResult<?> getBody() {     
    	PageResult<Warehouse> pageResult = warehouseRepository.findPage(
        										"from Warehouse warehouse", getCriteria());
        return pageResult;
    }
    
    @Override
	protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.manageWarehouse.wareHouseName",
        		"location.code", 15, "string", true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("label.common.address.line1",
        		"location.address.addressLine1", 15, "string"));
        tableHeadData.add(new SummaryTableColumn("label.common.address.line2",
        		"location.address.addressLine2", 15, "string"));
        tableHeadData.add(new SummaryTableColumn("label.common.address.line3",
        		"location.address.addressLine3", 15, "string"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.city",
        		"location.address.city", 10, "string"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.state",
        		"location.address.state",10, "String"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.country",
        		"location.address.country",10, "String"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.labels", "imageCol", 5, IMAGE,
				"labelsImg", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
        		"id",0, "String", false, true, true, false));
        
        return tableHeadData;
    }
      
    
    public String detail() throws Exception {
        return SUCCESS;
    }
         
    public String preview() {
        return SUCCESS;
    }
    
    @Override
	protected BeanProvider getBeanProvider() {
		return new LabelsPropertyResolver() {

			@Override
			public Object getProperty(String propertyPath, Object root) {
				Object object = super.getProperty(propertyPath, root);
				if (propertyPath.equals("inActive")) {
					boolean isInactive = (Boolean) object;
					if (isInactive) {
						object = getText("label.common.inactive");
					} else {
						object = getText("label.common.active");
					}
				}
				return object;
			}
		};
	}
    
}
