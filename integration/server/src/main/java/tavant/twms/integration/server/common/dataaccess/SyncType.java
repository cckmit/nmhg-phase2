/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.common.dataaccess;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author prasad.r
 */
@Entity
public class SyncType implements Serializable {
    
    @Id
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SyncType other = (SyncType) obj;
        return this.type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
    
    
    
}
