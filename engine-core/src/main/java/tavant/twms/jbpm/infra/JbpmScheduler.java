package tavant.twms.jbpm.infra;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.Duration;
import org.jbpm.db.SchedulerSession;
import org.jbpm.scheduler.exe.Timer;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springmodules.workflow.jbpm31.JbpmCallback;
import org.springmodules.workflow.jbpm31.JbpmTemplate;

/**
 * This is an attempt to fix the following issues that have been found in production environment
 * with jBPM's timer processor (SchedulerThread).
 * 1) Each timer is processed in it's own transaction instead of the current behavior where all
 *    the timers are processed within a single transaction
 * 2) This has the added benefit that it keeps the hibernate session size small. With the earlier
 *    approach the session size would become very huge when processing a large number of timers
 * 3) Only one hibernate session (spring managed) is created to process each timer. Earlier there
 *    were 2 open hibernate sessions (one created by jBPM and the other by spring. This at times
 *    causes hibernate to throw illegal access exceptions (Illegal attempt to associate a
 *    collection with two open sessions)
 * 4) The timers get deleted on successful completion. Earlier the timers were not deleted and
 *    instead the exception column had the 'task instance already ended' exception
 * 5) This can be scheduled using Quartz. This is an improvement over the current behaviour
 *    where even though the interval is specified, the scheduler can run earlier than the
 *    specified interval
 */
public class JbpmScheduler {

  private static final Log log = LogFactory.getLog(JbpmScheduler.class);
  static BusinessCalendar businessCalendar = new BusinessCalendar();

  private TransactionTemplate transactionTemplate;
  private JbpmTemplate jbpmTemplate;
  private ThreadLocal<String> threadLocal = new ThreadLocal<String>();

  public void executeTimers() {
    boolean isDueDateInPast = true;

    JbpmContext jbpmContext = jbpmTemplate.getJbpmConfiguration().createJbpmContext();
    try {

      SchedulerSession schedulerSession = jbpmContext.getSchedulerSession();

      log.debug("checking for timers");
      Iterator iter = schedulerSession.findTimersByDueDate();
      while( (iter.hasNext())
             && (isDueDateInPast)
           ) {
        Timer timer = (Timer) iter.next();
        log.debug("found timer "+timer);

        // if this timer is due
        if (timer.isDue()) {
          log.debug("executing timer '"+timer+"'");
          try{          
            executeTimerWithinTransactionTemplate(timer.getId());
          }catch(Exception e){
              log.error("Error occured processing timer with id : " + timer.getId(), e);
              timer.setException(threadLocal.get());
              schedulerSession.saveTimer(timer);
          }finally{
              threadLocal.remove();
          }
        } else { // this is the first timer that is not yet due
          isDueDateInPast = false;
        }
      }

    } finally {
      jbpmContext.close();
    }
  }

  public void executeTimerWithinTransactionTemplate(final long timerId) {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {

      protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
        executeTimerWithinJbpmTemplate(timerId);
      }
    });
  }

  public void executeTimerWithinJbpmTemplate(final long timerId) {
    this.jbpmTemplate.execute(new JbpmCallback() {

      public Object doInJbpm(JbpmContext jbpmContext) throws JbpmException {
        executeTimer(jbpmContext, timerId);
        return null;
      }
    });
  }

  public void executeTimer(JbpmContext jbpmContext, long timerId) {
    Timer timer = (Timer) jbpmContext.getSession().get(Timer.class, timerId);
    SchedulerSession schedulerSession = jbpmContext.getSchedulerSession();

      // execute
      timer.execute();

      // save the process instance
      jbpmContext.save(timer.getProcessInstance());

      // if there was an exception, just save the timer
      if (timer.getException()!=null) {
        schedulerSession.saveTimer(timer);
        threadLocal.set(timer.getException());

      // if repeat is specified
      } else if (timer.getRepeat()!=null && timer.getTaskInstance().isOpen()) {
        // update timer by adding the repeat duration
        Date dueDate = timer.getDueDate();

        // suppose that it to the timer runner thread a
        // very long time to execute the timers.
        // then the repeat action dueDate could already have passed.
        while (dueDate.getTime()<=System.currentTimeMillis()) {
          dueDate = businessCalendar
                .add(dueDate,
                  new Duration(timer.getRepeat()));
        }
        timer.setDueDate( dueDate );
        // save the updated timer in the database
        log.debug("saving updated timer for repetition '"+timer+"' in '"+(dueDate.getTime()-System.currentTimeMillis())+"' millis");
        schedulerSession.saveTimer(timer);

      } else {
        // delete this timer
        log.debug("deleting timer '"+timer+"'");
        schedulerSession.deleteTimer(timer);
      }
  }

  public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
    this.jbpmTemplate = jbpmTemplate;
  }
  
}
