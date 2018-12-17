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
package tavant.twms.web.admin.shippers;

import org.springframework.util.StringUtils;

import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.CarrierService;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public class ManageShippers extends I18nActionSupport implements Preparable,Validateable {

	private String id;
	private Carrier carrier;
	private CarrierService carrierService;
	
	public void prepare() throws Exception {
		if (StringUtils.hasText(id)) {
            carrier = carrierService.findById(Long.parseLong(id));
        }
	}

	@Override
	public void validate() {
		if(!StringUtils.hasText(carrier.getName())) {
			addActionError("error.manageShipper.nameNotEntered");
		}
		
		if(!StringUtils.hasText(carrier.getDescription())) {
			addActionError("error.manageShipper.descriptionNotEntered");
		}
		
		if(!StringUtils.hasText(carrier.getUrl())) {
			addActionError("error.manageShipper.urlNotEntered");
		}

		if(!hasActionErrors() && carrier.getId() == null){
			Carrier existingCarrier = carrierService.findCarrierByName(carrier.getName());
			if(existingCarrier != null) {
				addActionError("error.manageShipper.duplicateShipper");
			}
		}
	}

	public String createShipper() throws Exception {
		return SUCCESS;
	}
	
	public String viewShipper() throws Exception {
		if(StringUtils.hasText(id)){
			carrier = carrierService.findById(Long.parseLong(id));
		}
		return SUCCESS;
	}
	
	public String saveShipper() throws Exception {
		carrierService.save(carrier);
		addActionMessage("message.manageShipper.saveSuccess");
		return SUCCESS;
	}
	
	public String updateShipper() throws Exception {
		carrierService.update(carrier);
		addActionMessage("message.manageShipper.updateSuccess");
		return SUCCESS;
	}
	
	public String deleteShipper() throws Exception {
		carrier.getD().setActive(Boolean.FALSE);
		carrierService.update(carrier);
		addActionMessage("message.manageShipper.deleteSuccess");
		return SUCCESS;
	}
	
	public Carrier getCarrier() {
		return carrier;
	}

	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}

	public void setCarrierService(CarrierService carrierService) {
		this.carrierService = carrierService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
