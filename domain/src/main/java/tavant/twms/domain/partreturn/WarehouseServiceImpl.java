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
package tavant.twms.domain.partreturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class WarehouseServiceImpl extends
		GenericServiceImpl<Warehouse, Long, Exception> implements
		WarehouseService {
	
	private WarehouseRepository warehouseRepository;
	private OrgService orgService;

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	@Override
	public GenericRepository<Warehouse, Long> getRepository() {
		return warehouseRepository;
	}

	public Warehouse findByWarehouseCode(String code) {
		return warehouseRepository.findByWarehouseCode(code);
	}

	public List<String> findWarehouseCodesStartingWith(String code) {
		return warehouseRepository.findWarehouseCodesStartingWith(code);
	}
	
	public List<Location> findWarehouseLocationsStartingWith(String code) {
		return warehouseRepository.findWarehouseLocationsStartingWith(code);
	}
	
	public String getInspectorAtLocation(Location location) {
		return warehouseRepository.getInspectorAtLocation(location);
	}

	public String getPartShipperAtLocation(Location location) {
		return warehouseRepository.getPartShipperAtLocation(location);
	}
	
	public String getPartShipperForPart(OEMPartReplaced partReplaced) {
        if(partReplaced.isAskBackFromSupplier()){
            //return warehouseRepository.getPartShipperAtLocation(partReplaced.getTempLocationSetupForSupplier());
            return partReplaced.getActedBy();
        }
		else if(! partReplaced.getPartReturns().isEmpty()){
			Location location= partReplaced.getActivePartReturn().getReturnLocation();
			return warehouseRepository.getPartShipperAtLocation(location);
		}
		return null;	
	}

	public String getReceiverAtLocation(Location location) {
		return warehouseRepository.getReceiverAtLocation(location);
	}

	public void setWarehouseRepository(WarehouseRepository warehouseRepository) {
		this.warehouseRepository = warehouseRepository;
	}

	public List<User> getAvailableUsersForTheRole(String rolename) {
		Set<User> usersForTheRole = orgService.findUsersBelongingToRole(rolename);
		/*
		List<Warehouse> warehouses = warehouseRepository.findAll();
		for (Warehouse warehouse : warehouses) {
			for (User user : getUsersFromWareHouse(rolename, warehouse)) {
				usersForTheRole.remove(user);
			}
		}
		*/
		List<User> theUsers = new ArrayList<User>();
		for (User aUser : usersForTheRole) {
			theUsers.add(aUser);
		}
		return theUsers;
	}

	private List<User> getUsersFromWareHouse(String rolename, Warehouse warehouse) {
		if("receiver".equals(rolename)) {
			return warehouse.getRecievers();
		} else if("inspector".equals(rolename)) {
			return warehouse.getInspectors();
		} else if("partshipper".equals(rolename)) {
			return warehouse.getPartShippers();
		}
		return null;
	}

    public Location getDefaultReturnLocation(String locationCode){
      return warehouseRepository.getDefaultReturnLocation(locationCode);
    }

	public List<Warehouse> findAllWarehouseForLabel(Label label) {
		return this.warehouseRepository.findAllWarehouseForLabel(label);
	}
}
