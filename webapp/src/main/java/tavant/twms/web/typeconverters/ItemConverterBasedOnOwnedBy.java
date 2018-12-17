package tavant.twms.web.typeconverters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.security.SecurityHelper;

public class ItemConverterBasedOnOwnedBy extends NamedDomainObjectConverter <CatalogService, Item> {

    public ItemConverterBasedOnOwnedBy() {
        super("catalogService");
    }

    @Override
    public Item fetchByName(String name) throws CatalogException {
    	Item itemToReturn = null;
    	try
    	{
    		
    		itemToReturn = getService().findItemOwnedByManuf(name);
    	
    	}
    	catch(CatalogException e)
    	{        //check for brand item number
    			itemToReturn=getService().findBrandItemByName(name)!=null?getService().findBrandItemByName(name).getItem():null;	
    		
	    	//Part is not owned by manufacturer, try if its owned by service provider
    	if(itemToReturn ==null)
		{
	    	Organization serviceProviderOrg = new SecurityHelper().getLoggedInUser().getBelongsToOrganization();
	    	if(serviceProviderOrg != null)
	    	{        		
	    		itemToReturn = getService().findItemByItemNumberOwnedByServiceProvider(name,serviceProviderOrg.getId());
	    	}
       
    	}
    	}
        return itemToReturn;
    }
    
	public List<Item> fetchByNames(String[] names)
			throws CatalogException {
		
		try {
			if (names != null && names.length > 0) {
				List<Item> items = new ArrayList<Item>();
				for (String name : names) {
					items.add(getService().findItemOwnedByManuf(name));
				}
				return items;
			}
			return null;
		} catch (CatalogException e) {
			// Part is not owned by manufacturer, try if its owned by service
			// provider
			Organization serviceProviderOrg = new SecurityHelper()
					.getLoggedInUser().getBelongsToOrganization();
			if (serviceProviderOrg != null) {
				if (names != null && names.length > 0) {
					List<Item> items = new ArrayList<Item>();
					for (String name : names) {
						items.add(getService()
								.findItemByItemNumberOwnedByServiceProvider(
										name, serviceProviderOrg.getId()));
					}
					return items;
				}
				return null;

			}
			return null;
		}

	}

    @Override
    public String getName(Item item) {
        return item.getNumber();
    }
}
