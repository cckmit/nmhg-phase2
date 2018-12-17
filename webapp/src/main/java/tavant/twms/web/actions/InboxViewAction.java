/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.web.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.Preparable;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.ClaimFolderNames;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.query.view.ClaimInboxViewFields;
import tavant.twms.domain.query.view.InboxField;
import tavant.twms.domain.query.view.InboxView;
import tavant.twms.domain.query.view.InboxViewFields;
import tavant.twms.domain.query.view.InboxViewFieldsFactory;
import tavant.twms.domain.query.view.InboxViewService;

/**
 * 
 * @author roopali.agrawal
 * 
 */
@SuppressWarnings("serial")
public class InboxViewAction extends TwmsActionSupport implements
		ServletContextAware,ServletRequestAware,Preparable {
	private ServletContext servletContext;
	private InboxViewService inboxViewService;
	private InboxView inboxView;
	private Long id;
	private String context;
	private String folderName;
	private List<InboxField> availableFields = new ArrayList<InboxField>();	
	private List<InboxField> selectedFieldsList = new ArrayList<InboxField>();
	private List<String> selectedFields = new ArrayList<String>();
	private List<InboxView> inboxViews;
	private String parentFrameId;
	private HttpServletRequest request;
	private Map sortOrderMap = new HashMap<Boolean, String>();	
	private List<String> fieldsAvailableForSort = new ArrayList<String>();
	private List<String> fieldsNameAvailableForSort = new ArrayList<String>();
    private List<InboxField> selectedFieldsListForSort = new ArrayList<InboxField>();
    private List<InboxField> defaultSortableFields = new ArrayList<InboxField>();
    private StringBuffer fieldsNotAvailableForSortString=new StringBuffer();
	
	

	public InboxViewAction() {
		inboxView = new InboxView();
		sortOrderMap.put(new Boolean(true), "Ascending");
		sortOrderMap.put(new Boolean(false), "Descending");		
	}	

	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
		
	}

	public void prepare() throws Exception {
		InboxViewFields inboxViewFields = InboxViewFieldsFactory.getInstance().getInboxViewFields(request.getParameter("context"),request.getParameter("folderName"));
        List<InboxField> allAvailableFields = new ArrayList(
				inboxViewFields
						.getInboxFields().values());
        for (InboxField field : allAvailableFields) {
            if (!field.isHidden()) {
                availableFields.add(field);
            }
        }
        sortInboxViewFields(availableFields);
        //Collections.sort(availableFields);
		//fieldsAvailableForSort = inboxViewFields.getFieldsNotAvailableForSort();


        for (InboxField field : allAvailableFields) {
			if(field.isAllowSort()){
				fieldsAvailableForSort.add(field.getId());
				fieldsNameAvailableForSort.add(getText(field.getDisplayName()));
			}
            if (field.isAllowDefaultSort()) {
                defaultSortableFields.add(field);
            }
        }
	}

	private void sortInboxViewFields(List<InboxField> inboxFields) {
		HashMap<String, InboxField> namesMap = new HashMap<String, InboxField>();
		List<String> names = new ArrayList<String>();
		for(InboxField field : inboxFields) {
			names.add(getText(field.getDisplayName()));
			namesMap.put(getText(field.getDisplayName()), field);
		}
		Collections.sort(names);
		inboxFields.clear();
		for(String name : names) {
			//condition for removing rejection reason option from list
				inboxFields.add(namesMap.get(name));
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public InboxViewService getInboxViewService() {
		return inboxViewService;
	}

	public void setInboxViewService(InboxViewService inboxViewService) {
		this.inboxViewService = inboxViewService;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public String execute() throws Exception {
		if (getId() != null) {
			inboxView = inboxViewService.findById(getId());			
			/*for(InboxField field:inboxView.getFields()){
				selectedFields.add(field.getExpression());
			}*/
			selectedFieldsList=inboxView.getFields();
			selectedFieldsList.addAll(defaultSortableFields);
			for(InboxField inboxField:selectedFieldsList)
			{
				if(fieldsAvailableForSort.contains(inboxField.getId()) && inboxField.isAllowSort())
					selectedFieldsListForSort.add(inboxField);
			}
			availableFields=(List<InboxField>)CollectionUtils.subtract(availableFields, selectedFieldsList);
			sortInboxViewFields(availableFields);
			//Collections.sort(availableFields);
		}
		if (BusinessObjectModelFactory.CLAIM_SEARCHES.equals(context)
				&& ClaimFolderNames.DRAFT_CLAIM.equalsIgnoreCase(getFolderName())) {
			addActionMessage(getText("label.inboxView.workOrderNumberWarnMsg"));
		} else if (BusinessObjectModelFactory.CLAIM_SEARCHES.equals(context)) {
			addActionMessage(getText("label.inboxView.claimNumberWarnMsg"));
		}
		if(BusinessObjectModelFactory.INVENTORY_SEARCHES.equals(context)){
			addActionMessage(getText("label.inboxView.serialNumberWarnMsg"));
		}
		if(BusinessObjectModelFactory.PART_RETURN_SEARCHES.equals(context)){
			addActionMessage(getText("label.inboxView.partNumberWarnMsg"));
		}
		if(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS.equals(context)){
			addActionMessage(getText("label.inboxView.recoveryClaimNumberWarnMsg"));
		}
		addActionMessage(getText("label.inboxView.maxFields"));
		addActionMessage(getText("label.inboxView.defaultSortingWarnMsg")+fieldsNameAvailableForSort);
		return SUCCESS;
	}
	
	public String findAllViews() throws Exception {
		inboxViews=inboxViewService.findInboxViewForUser(getLoggedInUser().getId(),context,getFolderName());
		return SUCCESS;
	}

	public String submit() throws Exception {		
		boolean isUpdate=false;
		if (getId() != null) {
			isUpdate=true;
		}
		StringBuffer nameString = new StringBuffer();
		for (String name : selectedFields) {
			nameString.append(name);
			nameString.append(",");
		}
		if (nameString.indexOf(",") != -1) {
			InboxView existingView = inboxViewService
					.findInboxViewByNameAndUser(inboxView.getName(),
							getLoggedInUser().getId(), context);
			//todo add condition for update case also.
			if (!isUpdate && existingView != null) {
				addActionError(getText("error.manageInboxView.nameDuplicateExists"));
				return INPUT;
			}
			if(isUpdate)
			{
				//TODO why create another and than update it?
				
				InboxView updatedInboxView=inboxViewService.findById(getId());
				
				//if name has been changed and there is existing view with the changed name
				if (!inboxView.getName().equalsIgnoreCase(updatedInboxView.getName())){
					if (existingView != null) {
						addActionError(getText("error.manageInboxView.nameDuplicateExists"));
						return INPUT;
					}
				}
				updatedInboxView.setName(inboxView.getName());
				updatedInboxView.setType(context);
				updatedInboxView.setFolderName(folderName);
				//TODO why not updatedBy?
				updatedInboxView.setCreatedBy(getLoggedInUser());
				updatedInboxView.setFieldNames(nameString.substring(0, nameString
						.lastIndexOf(",")));
				updatedInboxView.setSortByField(inboxView.getSortByField());
				updatedInboxView.setSortOrderAscending(inboxView.isSortOrderAscending());
				inboxViewService.update(updatedInboxView);
			}
			else
			{
			inboxView.setType(context);
			inboxView.setFolderName(folderName);
			inboxView.setCreatedBy(getLoggedInUser());
			inboxView.setFieldNames(nameString.substring(0, nameString
						.lastIndexOf(",")));
				inboxViewService.save(inboxView);
			}
			
			inboxView=inboxViewService.findInboxViewByNameAndUser(inboxView.getName(), getLoggedInUser().getId(), context);
		}		
		return SUCCESS;
	}
 
	public String deleteView() throws Exception {
		InboxView currentInboxView = inboxViewService.findById(id);
		inboxViewService.delete(currentInboxView);
		addActionMessage(getText("message.success.viewDeleted"));
		return SUCCESS;

	}
	public InboxView getInboxView() {
		return inboxView;
	}

	public void setInboxView(InboxView inboxView) {
		this.inboxView = inboxView;
	}

	public List<InboxField> getAvailableFields() {
		return availableFields;
	}

	public void setAvailableFields(List<InboxField> availableFields) {
		this.availableFields = availableFields;
	}

	public List<String> getSelectedFields() {
		return selectedFields;
	}

	public void setSelectedFields(List<String> selectedFields) {
		this.selectedFields = selectedFields;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public List<InboxView> getInboxViews() {
		return inboxViews;
	}

	public void setInboxViews(List<InboxView> inboxViews) {
		this.inboxViews = inboxViews;
	}

	public String getParentFrameId() {
		return parentFrameId;
	}

	public void setParentFrameId(String parentFrameId) {
		this.parentFrameId = parentFrameId;
	}





	public Map getSortOrderMap() {
		return sortOrderMap;
	}





	public void setSortOrderMap(Map sortOrderMap) {
		this.sortOrderMap = sortOrderMap;
	}

    public List<InboxField> getSelectedFieldsList() {
    	if ((selectedFieldsList.size() == 0) && (selectedFields.size() > 0)){
    		StringBuffer nameString = new StringBuffer();
    		for (String name : selectedFields) {
    			nameString.append(name);
    			nameString.append(",");
    		}
    		StringTokenizer tokenizer = new StringTokenizer(nameString.substring(0, nameString
					.lastIndexOf(",")), ",");
            InboxViewFields metaData = InboxViewFieldsFactory.getInstance().getInboxViewFields(request.getParameter("context"),request.getParameter("folderName"));
            while (tokenizer.hasMoreTokens()) {
            	
            	InboxField field=metaData.getField(tokenizer.nextToken());
            	selectedFieldsList.add(field);
            	if(field.isAllowSort())
            	{
            	selectedFieldsListForSort.add(field);
            	}
         	
            }
    	}
    	return selectedFieldsList;
	}

	public void setSelectedFieldsList(List<InboxField> selectedFieldsList) {
		this.selectedFieldsList = selectedFieldsList;
	}
	

	public List<InboxField> getSelectedFieldsListForSort() {
		return selectedFieldsListForSort;
	}

	public void setSelectedFieldsListForSort(
			List<InboxField> selectedFieldsListForSort) {
		this.selectedFieldsListForSort = selectedFieldsListForSort;
	}

	public String getFieldsNotAvailableForSortString() {
		return fieldsNotAvailableForSortString.toString();
	}

	public void setFieldsNotAvailableForSortString(
			String fieldsNotAvailableForSortString) {
		this.fieldsNotAvailableForSortString = new StringBuffer(fieldsNotAvailableForSortString);
	}

	public List<String> getFieldsAvailableForSort() {
		return fieldsAvailableForSort;
	}

	public void setFieldsAvailableForSort(List<String> fieldsAvailableForSort) {
		this.fieldsAvailableForSort = fieldsAvailableForSort;
	}

    public List<InboxField> getDefaultSortableFields() {
        return defaultSortableFields;
    }

    public void setDefaultSortableFields(List<InboxField> defaultSortableFields) {
        this.defaultSortableFields = defaultSortableFields;
    }

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
}
