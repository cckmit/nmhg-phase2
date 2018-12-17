package tavant.twms.domain.notification;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("none")
public abstract class AbstractNotificationMessage {
	@Id
    @GeneratedValue(generator = "NotificationMessage")
    @GenericGenerator(name = "NotificationMessage", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters={
    		@Parameter(name = "sequence_name", value = "NOTIFICATION_MSG_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
			
    private Long id;
		
	@Column(insertable = false, updatable = false)
    String type;
	
	@Version
    private int version;
	
    private String recipient;
    
    @Column(nullable = true)
    private Date creationDate;
    
    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.notification.MessageState"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private MessageState messageState = MessageState.PENDING;
    
    private String messageTemplate;
    
    private Long numberOfTrials = new Long(0);
    
	@OneToMany(mappedBy = "notificationMessage", fetch = FetchType.EAGER)
	@Cascade( { CascadeType.ALL })
    private Set<NotificationMessageParameter> paramMap;

    private String exception;
      
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}



	public MessageState getMessageState() {
		return messageState;
	}

	public void setMessageState(MessageState messageState) {
		this.messageState = messageState;
	}



	public String getMessageTemplate() {
		return messageTemplate;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public Long getNumberOfTrials() {
		return numberOfTrials;
	}

	public void setNumberOfTrials(Long numberOfTrials) {
		this.numberOfTrials = numberOfTrials;
	}


	public HashMap<String, Object> getParameterMap() 
	{
		HashMap<String, Object> notificationMsgParameters = null;
		if(this.paramMap == null || this.paramMap.size() == 0)
		{
			notificationMsgParameters = new HashMap<String, Object>(); 
		}
		else 
		{
			notificationMsgParameters = new HashMap<String, Object>();
			NotificationMessageParameter currentParameter = null;
			for(Iterator<NotificationMessageParameter> ite = this.paramMap.iterator();ite.hasNext();)
			{
				currentParameter = (NotificationMessageParameter)ite.next();
				notificationMsgParameters.put(currentParameter.getKey(), currentParameter.getValue());				
			}
		}
		return notificationMsgParameters;
	}

	public void setParameterMap(final HashMap<String, Object> paramMap) 
	{
		Set<NotificationMessageParameter> newSet = null;
		if(paramMap != null && paramMap.size() > 0)
		{
			Set<String> keySet = paramMap.keySet();
			String currentKey = null;
			newSet = new HashSet<NotificationMessageParameter>(); 
			for(Iterator<String> ite = keySet.iterator();ite.hasNext();)
			{
				currentKey = (String)ite.next();
				NotificationMessageParameter np =  new NotificationMessageParameter(currentKey,((String)paramMap.get(currentKey)), this);
				newSet.add(np);
			}
		}
		else
		{
			newSet = new HashSet<NotificationMessageParameter>();
		}
		
		this.paramMap = newSet;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public void setNotifcationMessageParam(String recipient, String templateName, HashMap<String, Object> paramMap){
		this.creationDate = new Date();
		this.messageState = MessageState.PENDING;
		this.numberOfTrials = new Long(0);
		this.recipient = recipient;
		this.messageTemplate = templateName;
		setParameterMap(paramMap);
	}

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
