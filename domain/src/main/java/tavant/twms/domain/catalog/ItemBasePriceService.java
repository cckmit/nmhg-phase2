/**
 *
 */
package tavant.twms.domain.catalog;

import tavant.twms.infra.GenericService;

/**
 * @author kaustubhshobhan.b
 *
 */
public interface ItemBasePriceService extends GenericService<ItemBasePrice,Long,Exception> {

	public ItemBasePrice findByItem(Item item);
}
