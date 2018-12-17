/**
 * 
 */
package tavant.twms.worklist;

import java.util.List;

/**
 * @author kannan.ekanath
 * 
 */
public class InboxItemList {

    List inboxItems;

    int totalCount;

    public InboxItemList(List inboxItems, int totalCount) {
        this.inboxItems = inboxItems;
        this.totalCount = totalCount;
    }

    public List getInboxItems() {
        return inboxItems;
    }

    public void setInboxItems(List inboxItems) {
        this.inboxItems = inboxItems;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
