package tavant.twms.infra;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

/**
 * This is the base domain entity that has the base columns that all entity would need.
 * @author ramalakshmi.p
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class BaseDomain {

    public static final String ACTIVE = "ACTIVE";

    public static final String INACTIVE = "INACTIVE";

    private String status;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
