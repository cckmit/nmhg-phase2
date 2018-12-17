package tavant.twms.domain.email;

import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

import org.apache.log4j.Logger;

import tavant.twms.domain.common.RecoveryClaimDocumentType;
import tavant.twms.domain.notification.MessageState;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.infra.GenericRepositoryImpl;
import com.domainlanguage.timeutil.Clock;

public class EmailMessageRepositoryImpl  extends GenericRepositoryImpl<NotificationMessage, Long> implements  EmailMessageRepository {
	private static final Logger logger = Logger.getLogger(EmailMessageRepositoryImpl.class);
	
	
	public List<NotificationMessage> getPendingEmailMessage()
	{
		String query = "from NotificationMessage e " +
                "where e.numberOfTrials < 3 and " +
                "e.messageState in ('FAILED','PENDING') and " +
                "e.creationDate > sysdate-1 and e.creationDate < sysdate+1";
		HashMap params = new HashMap();
        return findUsingQuery(query, params);
	}
	
	public void saveAllNotificationMessages(List<NotificationMessage> notificationMessageList)
	{
		saveAll(notificationMessageList);
	}

	public void updateEmailMessage(NotificationMessage emailMessage) {
		update(emailMessage);		
	}

    public List<NotificationMessage> getNewPendingRecoveryEmailMessage(String recoveryClaimId)
    {
        String query = "from NotificationMessage e where e.id in (select params.notificationMessage from NotificationMessageParameter params where params.key='recClaimId' and params.value=:recoveryClaimId) " +
                "and e.numberOfTrials < 3 and " +
                "e.messageState in ('FAILED','PENDING') and " +
                "e.messageTemplate = 'email_initial_response_exceed.vm' and e.creationDate > sysdate-1";
        HashMap params = new HashMap();
        params.put("recoveryClaimId", recoveryClaimId);
        return findUsingQuery(query, params);
    }

    public List<NotificationMessage> getPendingRecoveryEmailMessage(String recoveryClaimId)
    {
        String query = "from NotificationMessage e where e.id in (select params.notificationMessage from NotificationMessageParameter params where params.key='recClaimId' and params.value=:recoveryClaimId) " +
                "and e.numberOfTrials < 3 and " +
                "e.messageState in ('FAILED','PENDING') and " +
                "e.messageTemplate = 'email_final_response_exceed.vm' and e.creationDate > sysdate-1";
        HashMap params = new HashMap();
        params.put("recoveryClaimId", recoveryClaimId);
        return findUsingQuery(query, params);
    }
    
    public List<NotificationMessage> getAllPendingEmailMessages() {
        String query = "from NotificationMessage e " + "where e.numberOfTrials < 3 and " + "e.messageState in ('FAILED','PENDING') ";
        HashMap params = new HashMap();
        return findUsingQuery(query, params);
    }

    public List<NotificationMessage> getAllPendingRecoveryEmailMessage(String recoveryClaimId)
    {
        String query = "from NotificationMessage e where e.id in (select params.notificationMessage from NotificationMessageParameter params where params.key='recClaimId' and params.value=:recoveryClaimId) " +
                "and e.numberOfTrials < 3 and " +
                "e.messageState in ('FAILED','PENDING') and " +
                "e.messageTemplate in ('email_final_response_exceed.vm','email_initial_response_exceed.vm') and e.creationDate > sysdate-1";
        HashMap params = new HashMap();
        params.put("recoveryClaimId", recoveryClaimId);
        return findUsingQuery(query, params);
    }


}
