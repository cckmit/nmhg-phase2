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

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericService;

/**
 * @author aniruddha.chaturvedi
 *
 */
public interface WarehouseService extends GenericService<Warehouse, Long, Exception> {
	public Warehouse findByWarehouseCode(final String code);

    public List<String> findWarehouseCodesStartingWith(String code);
    
    public String getReceiverAtLocation(Location location);
	
	public String getPartShipperAtLocation(Location location);
	
	public String getInspectorAtLocation(Location location);
	
	public List<User> getAvailableUsersForTheRole(String rolename);
	
	public List<Location> findWarehouseLocationsStartingWith(String code);
	
	@Transactional(readOnly=true)
	public String getPartShipperForPart(OEMPartReplaced partReplaced);

    @Transactional(readOnly=true)
    public Location getDefaultReturnLocation(String locationCode);
	
    public List<Warehouse> findAllWarehouseForLabel(Label label);

}
