package tavant.twms.web.supplier;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingRepository;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.web.actions.TwmsActionSupport;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 30/9/13
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class FetchSupplierDetail extends TwmsActionSupport {

    private Contract contract;
    
    private Item nmhgPart;
    
    private ItemMappingRepository itemMappingRepository;
    
    private ItemMapping supplierItemMapping;

	public ItemMapping getSupplierItemMapping() {
		return supplierItemMapping;
	}

	public void setSupplierItemMapping(ItemMapping supplierItemMapping) {
		this.supplierItemMapping = supplierItemMapping;
	}

	public void setItemMappingRepository(ItemMappingRepository itemMappingRepository) {
    	this.itemMappingRepository = itemMappingRepository;
    }

    public Item getNmhgPart() {
		return nmhgPart;
	}

	public void setNmhgPart(Item nmhgPart) {
		this.nmhgPart = nmhgPart;
	}

	public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getSupplierDetails(){
    	supplierItemMapping = itemMappingRepository.findItemMappingForOEMItem(nmhgPart, contract.getSupplier(), null);
        return SUCCESS;
    }
}
