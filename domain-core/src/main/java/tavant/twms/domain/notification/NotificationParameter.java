package tavant.twms.domain.notification;

import java.io.Serializable;

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
@Table(name = "notification_parameter")
public class NotificationParameter implements Serializable {
	
	@Id
	@GeneratedValue(generator = "NotificationParameter")
	@GenericGenerator(name = "NotificationParameter", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "NOTIFICATION_PARAM_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private NotificationEvent notificationEvent;
	
	@Column
	private String key;
    
	@Column
	private String value;

	public NotificationParameter()
	{
		
	}
	
	public NotificationParameter(String key, String value, NotificationEvent event)
	{
		this.key = key;
		this.value = value;
		this.notificationEvent = event;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NotificationEvent getNotificationEvent() {
		return notificationEvent;
	}

	public void setNotificationEvent(NotificationEvent notificationEvent) {
		this.notificationEvent = notificationEvent;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
