package tavant.twms.domain.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.Auditable;
import tavant.twms.security.AuditableColumns;

@Entity
public class NotificationEvent implements AuditableColumns{

	@Id
    @GeneratedValue(generator = "NotificationEvent")
    @GenericGenerator(name = "NotificationEvent", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters={
    		@Parameter(name = "sequence_name", value = "NOTIFICATION_EVENT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
			
    private Long id;
	
	private String eventName;

	private String entityName;

	private long numberOfTrials;

	private boolean pending;

	@OneToMany(mappedBy = "notificationEvent", fetch = FetchType.EAGER)
	@Cascade( { CascadeType.ALL })
	private Set<NotificationParameter> parameterMap;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public long getNumberOfTrials() {
		return numberOfTrials;
	}

	public void setNumberOfTrials(long numberOfTrials) {
		this.numberOfTrials = numberOfTrials;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public HashMap<String, Object> getParameterMap()
	{
		HashMap<String, Object> notificationParameters = null;
		if(this.parameterMap == null || this.parameterMap.size() == 0)
		{
			notificationParameters = new HashMap<String, Object>(); 
		}
		else 
		{
			notificationParameters = new HashMap<String, Object>();
			NotificationParameter currentParameter = null;
			for(Iterator<NotificationParameter> ite = this.parameterMap.iterator();ite.hasNext();)
			{
				currentParameter = (NotificationParameter)ite.next();
				notificationParameters.put(currentParameter.getKey(), currentParameter.getValue());				
			}
		}
		return notificationParameters;
	}

	public void setParameterMap(final HashMap<String, Object> paramMap) 
	{
		Set<NotificationParameter> newSet = null;
		if(paramMap != null && paramMap.size() > 0)
		{
			Set<String> keySet = paramMap.keySet();
			String currentKey = null;
			newSet = new HashSet<NotificationParameter>(); 
			for(Iterator<String> ite = keySet.iterator();ite.hasNext();)
			{
				currentKey = (String)ite.next();
				NotificationParameter np =  new NotificationParameter(currentKey,((String)paramMap.get(currentKey)), this);
				newSet.add(np);
			}
		}
		else
		{
			newSet = new HashSet<NotificationParameter>();
		}
		
		this.parameterMap = newSet;
	}	
}
