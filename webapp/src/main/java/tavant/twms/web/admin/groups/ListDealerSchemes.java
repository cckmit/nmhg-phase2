/**
 * 
 */
package tavant.twms.web.admin.groups;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.orgmodel.DealerScheme;
import tavant.twms.domain.orgmodel.DealerSchemeRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 *
 */
@SuppressWarnings("serial")
public class ListDealerSchemes extends SummaryTableAction {
    
    protected static Logger logger = LogManager.getLogger(ListDealerSchemes.class);

    private DealerSchemeRepository dealerSchemeRepository;
    
    @Override
	protected PageResult<?> getBody() {               
        PageResult<DealerScheme> pageResult = dealerSchemeRepository.findPage(
        										"from DealerScheme dealerScheme", getCriteria());
        return pageResult;
    }

    @Override
	protected List<SummaryTableColumn> getHeader(){
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.manageGroup.scheme",
        		"name", 100, "string", true , false, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
        		"id",0, "String", false, true, true, false));
        return tableHeadData;
    }
      

    public void setDealerSchemeRepository(DealerSchemeRepository dealerSchemeRepository) {
        this.dealerSchemeRepository = dealerSchemeRepository;
    }


}
