package tavant.twms.web.xls.reader;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ObjectFactory;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.inventory.InventoryItem;
import junit.framework.TestCase;

public class BeanColumnMappingTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        ObjectFactory.setObjectFactory(new ObjectFactory());
    }

    public void testPopulateInternalWithoutList() {
        BeanColumnMapping mapping = new BeanColumnMapping();
        InvItemBean bean = new InvItemBean();
        mapping.setExpression("serialNumber");
        List<Long> data = new ArrayList<Long>();
        data.add(22L);
        mapping.populateInternal(bean, data);
        assertEquals(22L, bean.getSerialNumber().longValue());
    }

    public void testPopulateInternalWithList() {
        BeanColumnMapping mapping = new BeanColumnMapping();
        InvItemBean bean = new InvItemBean();
        mapping.setExpression("faultCodes[].name");
        List<String> data = new ArrayList<String>();
        data.add("test1");
        data.add("test2");
        data.add("test3");
        mapping.populateInternal(bean, data);
        assertTrue(bean.getFaultCodes().size() == 3);
        List<FaultCodeBean> faultCodes = bean.getFaultCodes();
        assertTrue(faultCodes.get(0).name.equals("test1"));
        assertTrue(faultCodes.get(1).name.equals("test2"));
        assertTrue(faultCodes.get(2).name.equals("test3"));
    }

    public void testConvertorPopulateInternalWithoutList() {
        BeanColumnMapping mapping = new BeanColumnMapping();
        InvItemBean bean = new InvItemBean();
        mapping.setExpression("itemReference.referredItem");
        List<Long> data = new ArrayList<Long>();
        data.add(22L);
        List dependency = new ArrayList();
        dependency.add(Boolean.FALSE);
        mapping.setConvertor(new DummyItemReferenceObjectResolver());
        mapping.populateInternalWithDependent(bean, data, dependency);
        assertEquals(22L, new Long(bean.getItemReference().getReferredItem().getNumber()).longValue());
    }

    public void testConvertorPopulateInternalWithList() {
        BeanColumnMapping mapping = new BeanColumnMapping();
        InvItemBean bean = new InvItemBean();
        mapping.setExpression("itemReferences[].referredInventoryItem");
        List<String> data = new ArrayList<String>();
        List dependency = new ArrayList();
        data.add("test1");
        dependency.add(Boolean.TRUE);
        data.add("test2");
        dependency.add(Boolean.TRUE);
        data.add("test3");
        dependency.add(Boolean.TRUE);
        mapping.setConvertor(new DummyItemReferenceObjectResolver());
        mapping.populateInternalWithDependent(bean, data, dependency);
        assertTrue(bean.getItemReferences().size() == 3);
        List<ItemReference> refs = bean.getItemReferences();
        assertEquals("test1", refs.get(0).getReferredInventoryItem().getOfType().getNumber());
        assertEquals("test2", refs.get(1).getReferredInventoryItem().getOfType().getNumber());
        assertEquals("test3", refs.get(2).getReferredInventoryItem().getOfType().getNumber());
    }


}

class DummyItemReferenceObjectResolver implements Convertor {

    /* (non-Javadoc)
     * @see tavant.twms.web.xls.reader.Convertor#resolve(java.lang.Object)
     */
    public Object convert(Object object) {
        Item item = new Item();
        item.setNumber((String)object);
        return item;
    }

    /* (non-Javadoc)
     * @see tavant.twms.web.xls.reader.Convertor#resolveWithDependency(java.lang.Object, java.lang.Object)
     */
    public Object convertWithDependency(Object object, Object dependency) {
        Item item = new Item();
        item.setNumber(object.toString());
        if ((Boolean)dependency) {
            InventoryItem invItem = new InventoryItem();
            invItem.setOfType(item);
            return invItem;
        } else {
            return item;
        }
    }

}
