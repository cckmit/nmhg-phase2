/**
 *
 */
package tavant.twms.web.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.dom4j.DocumentException;

import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.bu.DivisionBusinessUnitMapping;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.SupersessionItem;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.opensymphony.xwork2.Preparable;



/**
 * @author mritunjay.kumar
 *
 */
public class ItemSearchAction extends SummaryTableAction implements
		ServletRequestAware, Preparable {

	private final Logger logger = Logger.getLogger(this.getClass());

	private String domainPredicateId;

	private String savedQueryId;

	private String contextName;

	private HttpServletRequest servletRequest;

	private CatalogService catalogService;

	private ItemGroupService itemGroupService;

	private Item item;
	
	private DivisionBusinessUnitMapping divisionBusinessUnitMapping;
	
	private BusinessUnitService businessUnitService;

	private BrandItem brandItem;

	public ItemSearchAction() {
		this.contextName = BusinessObjectModelFactory.ITEM_SEARCHES;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		if(isLoggedInUserADealer())
		{
			List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
			header.add(new SummaryTableColumn("columnTitle.itemSearch.number", "numberForDisplay", 16,
					"String", "item.numberForDisplay", true, false, true, false));
			header.add(new SummaryTableColumn("columnTitle.item.hidden", "id", 0,
					"string", "id", false, true, true, false));	
			header.add(new SummaryTableColumn("columnTitle.itemSearch.number",
					"itemNumber", 25));
			header.add(new SummaryTableColumn("columnTitle.itemSearch.description",
					"item.product.description", 18));
			header.add(new SummaryTableColumn("label.common.model",
					"item.model.description", 25));
			header.add(new SummaryTableColumn("columnTitle.itemSearch.type",
					"item.itemType", 10));
			header.add(new SummaryTableColumn("columnTitle.itemSearch.make",
					"item.make", 12));
			header.add(new SummaryTableColumn("columnTitle.itemSearch.status",
					"item.status", 10));
			return header;	
		}
		else
		{
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("columnTitle.itemSearch.number", "numberForDisplay", 16,
				"String", "numberForDisplay", true, false, true, false));
		header.add(new SummaryTableColumn("columnTitle.item.hidden", "id", 0,
				"string", "id", false, true, true, false));	
		header.add(new SummaryTableColumn("columnTitle.itemSearch.number",
				"number", 25));
		header.add(new SummaryTableColumn("columnTitle.itemSearch.description",
				"product.description", 18));
		header.add(new SummaryTableColumn("label.common.model",
				"model.description", 25));
		header.add(new SummaryTableColumn("columnTitle.itemSearch.type",
				"itemType", 10));
		header.add(new SummaryTableColumn("columnTitle.itemSearch.make",
				"make", 12));
		header.add(new SummaryTableColumn("columnTitle.itemSearch.status",
				"status", 10));
		return header;
		}
	}

	@Override
	protected PageResult<?> getBody() {
		PageResult<?> obj = null;
		if (this.domainPredicateId != null && !("".equals(this.domainPredicateId.trim()))) {
			return this.catalogService.findAllItemsMatchingQuery(Long
					.parseLong(this.domainPredicateId),
					getCriteria(),
					getLoggedInUser().getBelongsToOrganization());
		}
		return obj;
	}

	public String getDomainPredicateId() {
		return this.domainPredicateId;
	}
	
	public boolean isPageReadOnly() {
		return false;
	}

	public void setDomainPredicateId(String domainPredicateId) {
		this.domainPredicateId = domainPredicateId;
	}

	public String getSavedQueryId() {
		return this.savedQueryId;
	}

	public void setSavedQueryId(String savedQueryId) {
		this.savedQueryId = savedQueryId;
	}

	public String getContextName() {
		return this.contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public HttpServletRequest getServletRequest() {
		return this.servletRequest;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public void prepare() throws Exception {
		if (getServletRequest().getAttribute("savedQueryId") != null) {
			this.savedQueryId = getServletRequest().getAttribute("savedQueryId")
					.toString();
		}
		if (getServletRequest().getAttribute("domainPredicateId") != null) {
			this.domainPredicateId = getServletRequest().getAttribute(
					"domainPredicateId").toString();
		}
	}

	public CatalogService getCatalogService() {
		return this.catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public String isPartForReturn(Item item) {
		if (this.itemGroupService.isInPartReturnDefinition(item)) {
			return "Yes";
		} else {
			return "No";
		}
	}

	public Item getItem() {
		return this.item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String preview() throws ServletException, DocumentException,
			IOException {
		if (getId() != null) {
		        if(this.logger.isInfoEnabled()) {
                    this.logger.info("The item id obtained to be viewed is: " + getId());
                }
			this.item = this.catalogService.findById((Long.parseLong(getId())));
			return SUCCESS;
		}
		return displayFieldError("emptyItemId", "The item id cannot be empty");
	}

	public String detail() throws ServletException, DocumentException,
			IOException {
		if (getId() != null) {
			this.logger.info("The item id obtained to be viewed is: " + getId());
			if(isLoggedInUserADealer())
			{
			this.brandItem=	this.catalogService.findBrandItemById((Long.parseLong(getId())));
			this.divisionBusinessUnitMapping = businessUnitService.
			findBusinessUnitForDivisionCode(brandItem.getItem().getDivisionCode());
			}
			else
			{
			this.item = this.catalogService.findById((Long.parseLong(getId())));
			this.divisionBusinessUnitMapping = businessUnitService.
									findBusinessUnitForDivisionCode(item.getDivisionCode());
			}
			return SUCCESS;
		}
		return displayFieldError("emptyItemId", "The item id cannot be empty");

	}

	private String displayFieldError(String errorId, String errorMessage) {
		addFieldError(errorId, errorMessage);
		return INPUT;
	}

	public String isPartForReview(Item item) {
		if (this.itemGroupService.isPartInReviewWatchList(item)) {
			return "Yes";
		} else {
			return "No";
		}
	}
	
	public DivisionBusinessUnitMapping getDivisionBusinessUnitMapping() {
		return divisionBusinessUnitMapping;
	}

	public void setDivisionBusinessUnitMapping(
			DivisionBusinessUnitMapping divisionBusinessUnitMapping) {
		this.divisionBusinessUnitMapping = divisionBusinessUnitMapping;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}

	public BrandItem getBrandItem() {
		return brandItem;
	}

	public void setBrandItem(BrandItem brandItem) {
		this.brandItem = brandItem;
	}
	
	public String getSupersededPartNumber(Item item){
		String supersessionItem = null;
		supersessionItem = this.catalogService.findSupersessionItem(item);
		return supersessionItem;
	}

}
