/**
 * 
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.Preparable;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemScheme;
import tavant.twms.domain.catalog.ItemSchemeService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
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
public class ListItemGroups extends SummaryTableAction implements Preparable{
    
    protected static Logger logger = LogManager.getLogger(ListItemGroups.class);
    private ItemGroupService itemGroupService;
    private ItemSchemeService itemSchemeService;
    private String schemeId;
    private ItemScheme itemScheme;
    
    public void setItemSchemeService(ItemSchemeService itemSchemeService) {
        this.itemSchemeService = itemSchemeService;
    }

	public void prepare() throws Exception {
        Long idToBeUsed = getIdToBeUsed();
        itemScheme = (idToBeUsed != null) ? 
        		itemSchemeService.findById(idToBeUsed) : null;
    }
	
	private Long getIdToBeUsed() {
		if(schemeId != null) {
            return Long.parseLong(schemeId);
        } else if(itemScheme != null) {
            return itemScheme.getId();
        }
		return null;
	}
	
	public Long getComputedSchemeId() {
        return (itemScheme != null) ? 
        		itemScheme.getId() : null;
	}

	@Override
    protected PageResult<?> getBody() {    
        PageResult<ItemGroup> pageResult = itemGroupService.findPage(getCriteria(), itemScheme);
        return pageResult;
    }
    
	@Override
	protected List<SummaryTableColumn> getHeader(){
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.manageGroup.groupName",
        		"name", 20, "string",true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.description",
        		"description", 70, "string"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
        		"id",0, "String", false, true, true, false));
        tableHeadData.add(new SummaryTableColumn("",
				"imageCol", 10, IMAGE, "labelsImg", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        return tableHeadData;
    }
    
	@Override
	protected BeanProvider getBeanProvider() {
		return new GroupsMemberTypeResolver();
	}

    public String detail() throws Exception {
        return SUCCESS;
    }
         
    public String preview() {
        return SUCCESS;
    }
     
    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public ItemScheme getItemScheme() {
        return itemScheme;
    }

    public void setItemScheme(ItemScheme itemScheme) {
        this.itemScheme = itemScheme;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }
    
}
