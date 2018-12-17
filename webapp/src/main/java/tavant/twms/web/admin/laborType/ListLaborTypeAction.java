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
package tavant.twms.web.admin.laborType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.laborType.LaborType;
import tavant.twms.domain.laborType.LaborTypeService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.claim.ClaimsAction;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

import tavant.twms.web.TWMSWebConstants;

/**
 * @author
 */
public class ListLaborTypeAction extends SummaryTableAction implements Preparable,TWMSWebConstants,Validateable {
    
    private LaborTypeService laborTypeService;
    private static final Logger logger = Logger.getLogger(ClaimsAction.class);
    private LaborType laborType;
    
    private static final String MESSAGE_KEY_CREATE = "message.manage.createLaborTypeSuccess";
    private static final String MESSAGE_KEY_UPDATE = "message.manage.updateLaborTypeSuccess";
    private static final String MESSAGE_KEY_DELETE = "message.manage.deleteLaborTypeSuccess";
    
    public LaborType getLaborType() {
		return laborType;
	}
	public void setLaborType(LaborType laborType) {
		this.laborType = laborType;
	}

	@Override
    public PageResult<?> getBody() {
        PageResult<LaborType> pageResult = laborTypeService.findByActive(getCriteria());
        return pageResult;
    }

	@Override
	public void validate() {
		if(laborType!=null && !StringUtils.hasText(laborType.getLaborType())){
			addActionError("error.laborType.name");
		}
		if(laborType!=null && (null==laborType.getMultiplicationValue() ||
				(laborType.getMultiplicationValue()!=null && !(laborType.getMultiplicationValue().signum()==(1))))){
			addActionError("error.laborType.multiplicationValue");
		}
	}
    @Override
    public List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
        		"id", 0, "String", false, true, true, false));
        tableHeadData.add(new SummaryTableColumn("label.laborType.laborType",
        		"laborType", 35, "String", true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("label.laborType.multiplication.value",
        		"multiplicationValue", 65, "String"));     
        return tableHeadData;
    }
    public String preview() throws ServletException, DocumentException,
    IOException {
    		return SUCCESS;
    }

    @Required
    public void setLaborTypeService(LaborTypeService laborTypeService) {
        this.laborTypeService = laborTypeService;
    }

	public void prepare() throws Exception {
		if(null!=id && id.trim().length()!=0)
		{
			laborType = (LaborType)laborTypeService.findById(new Long(id));
		}
	}
	public String detail(){
        laborType = (LaborType)laborTypeService.findById(new Long(id));        
        return SUCCESS;
    }
    
	public String createLaborType()throws Exception{
		if(laborTypeService.findByName(laborType.getLaborType())!=null){
			addActionError("message.manage.createLaborType.duplicate");
			return INPUT;
		}
		laborType.setStatus(TWMSWebConstants.STATUS_ACTIVE);
		laborTypeService.save(laborType);
		addActionMessage(MESSAGE_KEY_CREATE);
		return SUCCESS;
	}
	public String deleteLaborType()throws Exception{
		laborType.setStatus(TWMSWebConstants.STATUS_INACTIVE);
		laborTypeService.update(laborType);
		addActionMessage(MESSAGE_KEY_DELETE);
		return SUCCESS;			
    }
	public String updateLaborType()throws Exception{
		laborTypeService.update(laborType);
		addActionMessage(MESSAGE_KEY_UPDATE);
		return SUCCESS;			
    }
	public String load(){
		return SUCCESS;
	}
    
}