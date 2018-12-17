package tavant.twms.web.admin.model;

import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;
import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;
import tavant.twms.web.common.LabelsPropertyResolver;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.BeanProvider;
import com.opensymphony.xwork2.Preparable;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;

import org.dom4j.DocumentException;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Apr 29, 2009
 * Time: 12:04:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListModelsAction extends SummaryTableAction implements
        Preparable {

    private List<ItemGroup> models ;
    
    private ItemGroup model;
    
    
	private CatalogService catalogService;

    public void prepare() throws Exception {

    }

    @Override
	protected PageResult<?> getBody() {
		return catalogService
				.findAllModelsWithCriteria(getCriteria());
	}

    @Override
	protected List<SummaryTableColumn> getHeader() {
		tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("columnTitle.model.hidden", "id", 0, "String", "id", false, true, true, false));
        tableHeadData.add(new SummaryTableColumn("label.common.name", "name", 20, "String", "name",true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("label.common.groupCode", "groupCode", 20, "String"));
        tableHeadData.add(new SummaryTableColumn("label.common.product", "isPartOf.name", 20, "String"));
        tableHeadData.add(new SummaryTableColumn("label.common.itemGroupType", "itemGroupType", 30, "String"));
        tableHeadData.add(new SummaryTableColumn("", "imageCol",30, IMAGE, "labelsImg", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        return tableHeadData;
	}

    public ItemGroup getModel() {
		return model;
	}

	public void setModel(ItemGroup model) {
		this.model = model;
	}
	
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public List<ItemGroup> getModels() {
        return models;
    }

    public void setModels(List<ItemGroup> models) {
        this.models = models;
    }

    @Override
	protected BeanProvider getBeanProvider() {
		return new LabelsPropertyResolver();
	}
    public String preview() throws ServletException, DocumentException,
	IOException {
 
	this.model = this.catalogService.findItemGroup((Long.parseLong(getId())));
	return SUCCESS;
}

}

