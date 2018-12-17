package tavant.twms.domain.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@SuppressWarnings("serial")
@Entity
@Table(name = "notification_message_parameter")
public class NotificationMessageParameter 
{
	
	@Id
	@GeneratedValue(generator = "NotificationMessageParameter")
	@GenericGenerator(name = "NotificationMessageParameter", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "NOTIFICATION_MSG_PARAM_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private AbstractNotificationMessage notificationMessage;
	
	@Column
	private String key;
    
	@Column
	private String value;

	public NotificationMessageParameter()
	{
		
	}
	
	public NotificationMessageParameter(final String key, final String value, final AbstractNotificationMessage message)
	{
		this.key = key;
		this.value = value;
		this.notificationMessage = message;
	}

	public Long getId() 
	{
		return id;
	}

	public void setId(Long id) 
	{
		this.id = id;
	}

	public AbstractNotificationMessage getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(
			AbstractNotificationMessage notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public String getKey() 
	{
		return key;
	}

	public void setKey(String key) 
	{
		this.key = key;
	}

	public String getValue() 
	{
		return value;
	}

	public void setValue(String value) 
	{
		this.value = value;
	}
}
