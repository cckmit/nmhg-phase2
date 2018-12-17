/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.domain.claim;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 *
 * @author prasad.r
 */
@Entity
@Table(name="CLAIM_SNAPSHOT_XML")
public class ClaimSnapshot {
    
    @Id
    @GeneratedValue(generator = "ClaimSnapshot")
	@GenericGenerator(name = "ClaimSnapshot", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CLAIM_SNAPSHOT_XML_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;
    
    @Lob
    @Column(length = 16777210, name = "prev_claim_snapshot_string")
    private String previousClaimSnapshotAsString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreviousClaimSnapshotAsString() {
        return previousClaimSnapshotAsString;
    }

    public void setPreviousClaimSnapshotAsString(String previousClaimSnapshotAsString) {
        this.previousClaimSnapshotAsString = previousClaimSnapshotAsString;
    }

}
