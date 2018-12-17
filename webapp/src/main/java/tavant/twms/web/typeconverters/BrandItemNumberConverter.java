package tavant.twms.web.typeconverters;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deepak.patel on 24/2/14.
 */
public class BrandItemNumberConverter extends NamedDomainObjectConverter <CatalogService, BrandItem>{

    public BrandItemNumberConverter() {
        super("catalogService");
    }

    @Override
    public BrandItem fetchByName(String name) throws CatalogException {
        BrandItem itemToReturn = null;
        try
        {

            itemToReturn = getService().findBrandItemById(Long.parseLong(name));

        }
        catch(Exception e)
        {        //check for brand item number
            return null;
        }
        return itemToReturn;
    }

    public List<BrandItem> fetchByNames(String[] names)
            throws CatalogException {

        try {
            if (names != null && names.length > 0) {
                List<BrandItem> items = new ArrayList<BrandItem>();
                for (String name : names) {
                    items.add(getService().findBrandItemById(Long.parseLong(name)));
                }
                return items;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public String getName(BrandItem item) {
        return item.getItemNumber();
    }
}
