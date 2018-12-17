package tavant.twms.domain.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.query.view.InboxView;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Parameter;

/**
 * 
 * @author roopali.agrawal
 * 
 */
@Entity
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "created_by", "folder_name" }))
public class DefaultFolderView {
		
    @Id
	@GeneratedValue(generator = "DefaultFolderView")
	@GenericGenerator(name = "DefaultFolderView", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "DEFAULT_FOLDER_VIEW_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;
	
	@Column(nullable = false)
	private String folderName;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)    
    private InboxView defaultInboxView;

    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User createdBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InboxView getDefaultInboxView() {
		return defaultInboxView;
	}

	public void setDefaultInboxView(InboxView defaultInboxView) {
		this.defaultInboxView = defaultInboxView;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
}
