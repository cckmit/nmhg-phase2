package tavant.twms.domain.email;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.List;


/**
 * Created by deepak.patel on 31/3/14.
 */
@Aspect
public class EmailAddressChangeAspect {

    private String emailEnvironment;

    private TestEmailRepository testEmailRepository;

    private String defaultFallBackEmailAddress;

    public String getDefaultFallBackEmailAddress() {
        return defaultFallBackEmailAddress;
    }

    public void setDefaultFallBackEmailAddress(String defaultFallBackEmailAddress) {
        this.defaultFallBackEmailAddress = defaultFallBackEmailAddress;
    }

    public void setTestEmailRepository(TestEmailRepository testEmailRepository) {
        this.testEmailRepository = testEmailRepository;
    }

    private static final Logger logger = Logger.getLogger(EmailAddressChangeAspect.class);

    @Pointcut("@annotation(tavant.twms.domain.email.ApplyEmailAspect)")
    public void sendEmailServiceMethods() {}

    @Around("sendEmailServiceMethods()")
    public void checkEmailAddress(ProceedingJoinPoint joinPoint){

        Object[] args = joinPoint.getArgs();
        if(args !=  null && args.length >1){
            String toAddress = args[1].toString();
            if(emailEnvironment.equalsIgnoreCase("PROD")){
                try{
                    joinPoint.proceed();
                }catch (Throwable e){

                }
            }else if(isMailIDListedForTest(toAddress)){
                try{
                    joinPoint.proceed();
                }catch (Throwable e){

                }
            }else{
                try{

                    //change the subject too
                    if(args.length > 2)
                        args[2] = "Fall back email address used, SLMS trying to send mail to " +args[1] +" from a test environment.";
                    args[1] = defaultFallBackEmailAddress;
                    joinPoint.proceed(args);
                }catch (Throwable e){

                }
            }

        }else{
            try{
                joinPoint.proceed();
            }catch (Throwable e){
                logger.error("Sending email fails" + e.fillInStackTrace());
            }
        }
    }

    private boolean isMailIDListedForTest(String to){
        List<TestEmails> emails = testEmailRepository.findAllTestEmails();
        for(TestEmails email : emails){
            if(email.getEmail().equalsIgnoreCase(to)){
                return true;
            }
        }
        return false;
    }

    public String getEmailEnvironment() {
        return emailEnvironment;
    }

    public void setEmailEnvironment(String emailEnvironment) {
        this.emailEnvironment = emailEnvironment;
    }
}
