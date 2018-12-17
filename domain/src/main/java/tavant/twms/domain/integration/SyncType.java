/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tavant.twms.domain.integration;


import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 *
 * @author prasad.r
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class SyncType implements Serializable,AuditableColumns {

//    private static final SyncType CUSTOMER = new SyncType("Customer");
//    private static final SyncType CREDITNOTIFICATION = new SyncType("CreditNotification");
//    private static final SyncType TKTSA_CUSTOMER = new SyncType("TKTSA-Customer");
//    private static final SyncType FETCHFOC = new SyncType("FetchFoc");
//    private static final SyncType CLAIM = new SyncType("Claim");
//    private static final SyncType INSTALLBASE = new SyncType("InstallBase");
//    private static final SyncType POSTFOC = new SyncType("PostFoc");
//    private static final SyncType TKTSA_PARTSINVENTORY = new SyncType("TKTSA-PartsInventory");
//    private static final SyncType ITEM = new SyncType("Item");
//    private static final SyncType EXTWARRANTYPURCHASENOTIFICATION = new SyncType("ExtWarrantyPurchaseNotification");
//    private static final SyncType CURRENCYEXCHANGERATE = new SyncType("CurrencyExchangeRate");

    @Id
    private String type;

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    public SyncType() {
    }

    public SyncType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
//    public static SyncType getSyncType(String type){
//        if("Customer".equalsIgnoreCase(type)){
//            return CUSTOMER;
//        }else if("CreditNotification".equalsIgnoreCase(type)){
//            return CREDITNOTIFICATION;
//        }else if("TKTSA-Customer".equalsIgnoreCase(type)){
//            return TKTSA_CUSTOMER;
//        }else if("FetchFoc".equalsIgnoreCase(type)){
//            return FETCHFOC;
//        }else if("Claim".equalsIgnoreCase(type)){
//            return CLAIM;
//        }else if("InstallBase".equalsIgnoreCase(type)){
//            return INSTALLBASE;
//        }else if("PostFoc".equalsIgnoreCase(type)){
//            return POSTFOC;
//        }else if("TKTSA-PartsInventory".equalsIgnoreCase(type)){
//            return TKTSA_PARTSINVENTORY;
//        }else if("Item".equalsIgnoreCase(type)){
//            return ITEM;
//        }else if("ExtWarrantyPurchaseNotification".equalsIgnoreCase(type)){
//            return EXTWARRANTYPURCHASENOTIFICATION;
//        }else if("CurrencyExchangeRate".equalsIgnoreCase(type)){
//            return CURRENCYEXCHANGERATE;
//        }
//        throw new RuntimeException("Unknown sync type [" + type + "]" );
//    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity auditableColEntity) {
        this.d = auditableColEntity;
    }

}
