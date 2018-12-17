/**
 * 
 */
package tavant.twms.web.admin.warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.CarrierService;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Warehouse;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.domain.partreturn.WarehouseShippers;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author aniruddha.chaturvedi
 *
 */
@SuppressWarnings("serial")
public class ManageWarehouses extends I18nActionSupport implements Preparable,Validateable {

	private String id;
	private Warehouse warehouse;
	private WarehouseService warehouseService;
	private List<User> recievers;
	private List<User> inspectors;
	private List<User> partShippers;
	private List<String> bins;
	private List<Carrier> carriers;
	private CarrierService carrierService;
	private List<WarehouseShippers> warehouseShippers;

	public String showWarehouse() {
		initBins();
		removeInactiveShippers(warehouse.getWarehouseShippers());
		return SUCCESS;
	}

	/**
	 * 
	 */
	private void initBins() {
		bins = new ArrayList<String>();
		for (String bin : warehouse.getWarehouseBins()) {
			bins.add(bin);
		}
	}

	public String createWarehouse() {
		warehouse = new Warehouse();
		warehouse.setRecievers(new ArrayList<User>());
		warehouse.setInspectors(new ArrayList<User>());
		warehouse.setPartShippers(new ArrayList<User>());
		warehouse.setLocation(new Location("", new Address()));
		return SUCCESS;
	}

	public String saveWarehouse() throws Exception {
		if(warehouse.getId() != null) {
			warehouseService.update(warehouse);
			addActionMessage("message.manageWarehouse.updateSuccess");
		} else {
			warehouseService.save(warehouse);
			addActionMessage("message.manageWarehouse.saveSuccess");
		}
		prepare();
		initBins();
		return SUCCESS;
	}

	public void prepare() throws Exception {
		Long idToBeUsed = null;
		if (id != null) {
			idToBeUsed = Long.parseLong(id);
		} else if ((warehouse != null) && (warehouse.getId() != null)) {
			idToBeUsed = warehouse.getId();
		}
		if (idToBeUsed != null) {
			warehouse = warehouseService.findById(idToBeUsed);
		}

		recievers = warehouseService.getAvailableUsersForTheRole("receiver");
		Collections.sort(recievers, User.SORT_BY_COMPLETE_NAME);

		inspectors = warehouseService.getAvailableUsersForTheRole("inspector");
		Collections.sort(inspectors, User.SORT_BY_COMPLETE_NAME);

		partShippers = warehouseService.getAvailableUsersForTheRole("partshipper");
		Collections.sort(partShippers, User.SORT_BY_COMPLETE_NAME);
		carriers = carrierService.findAll();
	}

	@Override
	public void validate() {

		if(!StringUtils.hasText(warehouse.getContactPersonName())){
			addActionError("error.manageWarehouse.contactPersonNotEntered");
		}

		if(warehouse.getRecievers() == null || warehouse.getRecievers().size() == 0) {
			addActionError("error.manageWarehouse.recieverNotSelected");
		}

		if(warehouse.getInspectors() == null || warehouse.getInspectors().size() == 0) {
			addActionError("error.manageWarehouse.inspectorNotSelected");
		}

		if(warehouse.getPartShippers() == null || warehouse.getPartShippers().size() == 0) {
			addActionError("error.manageWarehouse.partShipperNotSelected");
		}

		if(warehouse.getId()==null && !StringUtils.hasText(warehouse.getLocation().getCode())) {
			addActionError("error.manageWarehouse.invalidWareHouseName");
		} else {
			if(warehouse.getId()==null && warehouseService.findByWarehouseCode(warehouse.getLocation().getCode())!=null){
				addActionError("error.manageWarehouse.duplicateWareHouseName",warehouse.getLocation().getCode());
			}
		}

		if(bins == null || bins.isEmpty()) {
			addActionError("error.manageWarehouse.binRequired");
		} else {
			for (Iterator iter = bins.iterator(); iter.hasNext();) {
				if (iter.next() == null) {
					iter.remove();
				}
			}
			for (String bin : bins) {
				if(bin.trim().equals("")) {
					addActionError("error.manageWarehouse.binNameRequired");
					break;
				}
			}
		}

		if(!isShipmentThroughCEVA() && (warehouseShippers == null || warehouseShippers.isEmpty())){
			addActionError("error.manageWarehouse.shipperRequired");
		} else if(warehouseShippers != null && warehouseShippers.size() > 0){
			Map<Long, String> shipperMap = new HashMap<Long, String>(); 
			for(WarehouseShippers warehouseShipper : warehouseShippers){
				if(warehouseShipper != null){
					if(null==warehouseShipper.getForCarrier().getId()){
						addActionError("error.manageWarehouse.selectShipper");
						break;
					}
					if(!shipperMap.containsKey(warehouseShipper.getForCarrier().getId())){
						shipperMap.put(warehouseShipper.getForCarrier().getId(), warehouseShipper.getAccountNumber());
					} else {
						addActionError("error.manageWarehouse.duplicateShipper");
						break;
					}
				}
			}
		}
		if(warehouseShippers != null){
			removeNullsFromList(warehouseShippers);
			warehouse.getWarehouseShippers().clear();
			warehouse.getWarehouseShippers().addAll(warehouseShippers);
		}
		if(!hasActionErrors()) {
			Set<String> binsFromWeb = new TreeSet<String>();
			for (String bin : bins) {
				binsFromWeb.add(bin);
			}
			warehouse.setWarehouseBins(binsFromWeb);
		} else {
			removeInactiveShippers(warehouse.getWarehouseShippers());
		}
	}

	private void removeNullsFromList(List oList) {
		for(Iterator iter = oList.iterator();iter.hasNext();){
			if (iter.next() == null) {
				iter.remove();
			}
		}
	}

	private void removeInactiveShippers(List<WarehouseShippers> warehouseShippers){
		if(!CollectionUtils.isEmpty(warehouse.getWarehouseShippers())){
			WarehouseShippers warehouseShipper = null;
			for(Iterator<WarehouseShippers> itr = warehouseShippers.iterator();itr.hasNext();){
				warehouseShipper = itr.next();
				if(warehouseShipper.getForCarrier() != null && !warehouseShipper.getForCarrier().getD().isActive()){
					itr.remove();
				}
			}
			removeNullsFromList(warehouseShippers);
		}
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<User> getInspectors() {
		return inspectors;
	}

	public void setInspectors(List<User> inspectors) {
		this.inspectors = inspectors;
	}

	public List<User> getPartShippers() {
		return partShippers;
	}

	public void setPartShippers(List<User> partShippers) {
		this.partShippers = partShippers;
	}

	public List<User> getRecievers() {
		return recievers;
	}

	public void setRecievers(List<User> recievers) {
		this.recievers = recievers;
	}

	public List<String> getBins() {
		return bins;
	}

	public void setBins(List<String> bins) {
		this.bins = bins;
	}

	public List<Carrier> getCarriers() {
		return carriers;
	}

	public void setCarriers(List<Carrier> carriers) {
		this.carriers = carriers;
	}

	public void setCarrierService(CarrierService carrierService) {
		this.carrierService = carrierService;
	}

	public List<WarehouseShippers> getWarehouseShippers() {
		return warehouseShippers;
	}

	public void setWarehouseShippers(List<WarehouseShippers> warehouseShippers) {
		this.warehouseShippers = warehouseShippers;
	}

    public boolean isShipmentThroughCEVA() {
        return getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName());
    }
}
