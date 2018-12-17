/**
 * 
 */
package tavant.twms.web.admin.groups;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.catalog.ItemSchemeRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class ListItemSchemes extends SummaryTableAction{
    protected static Logger logger = LogManager.getLogger(ListItemSchemes.class);

    private ItemSchemeRepository itemSchemeRepository;

    @Override
	protected PageResult<?> getBody() {
    	return itemSchemeRepository.findPage("from ItemScheme itemScheme", 
        		getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.manageGroup.scheme",
        		"name", 100, "string", true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
        		"id", 0, "string", false, true, true, false));

		return tableHeadData;
	}

    public void setItemSchemeRepository(ItemSchemeRepository itemSchemeRepository) {
        this.itemSchemeRepository = itemSchemeRepository;
    }
}