/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.web.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.util.ServletContextAware;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.context.ApplicationContext;
import tavant.twms.integration.server.common.BeanLocator;
import tavant.twms.integration.server.quartz.job.QuartzJobListner;


/**
 *
 * @author prasad.r
 */
public class AbstractAction extends ActionSupport implements ServletResponseAware, ServletContextAware ,
        Preparable, Validateable{

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z"); 
    private HttpServletResponse response;
    private ServletContext servletContext;
    private SchedulerFactory schedulerFactory;
    private String id;
    private Scheduler scheduler;
    private Scheduler quartzScheduler;
    private ApplicationContext applicationContext;
    private BeanLocator beanLocator;
    private QuartzJobListner quartzJobListner;


    public void prepare() throws Exception {
        beanLocator = new BeanLocator();
        quartzJobListner = (QuartzJobListner) beanLocator.getBean("quartzJobListner");
        this.schedulerFactory = (SchedulerFactory) servletContext.getAttribute("org.quartz.impl.StdSchedulerFactory.KEY");
        if(schedulerFactory != null && isValidString(id)){
            scheduler = schedulerFactory.getScheduler(getId());
        }else{
            if(quartzScheduler != null)
                scheduler = quartzScheduler;
            else{
                quartzScheduler = (Scheduler) beanLocator.getBean("quartzScheduler");
                if(quartzScheduler == null){
                    quartzScheduler = (Scheduler) applicationContext.getBean("quartzScheduler");
                }
                scheduler = quartzScheduler;
            }
        }
        
    }
    
    protected String writeJSONResponse(String jsonString) {
         response.setHeader("Pragma", "no-cache");
	     response.addHeader("Cache-Control", "must-revalidate");
	     response.addHeader("Cache-Control", "no-cache");
	     response.addHeader("Cache-Control", "no-store");
	     response.setDateHeader("Expires", 0);     
		
		response.setContentType("text/json-comment-filtered");
		try {
			response.getWriter().write(jsonString);
			response.flushBuffer();

			return null;
		} catch (IOException e) {
			String errorMessage = "Exception while writing JSON string \""
					+ jsonString + "\" to response :";
			throw new RuntimeException(errorMessage, e);
		}
    }

    
    public void setServletResponse(HttpServletResponse hsr) {
        this.response = hsr;
    }

    public void setServletContext(ServletContext sc) {
        this.servletContext = sc;
    }

    public SchedulerFactory getSchedulerFactory() {
        return schedulerFactory;
    }

    public boolean isValidString(String s){
        return s != null && !"".equals(s);
    }
    
    protected  List<JobDetail> getJobDetails(Scheduler scheduler) throws SchedulerException {
        List<JobDetail> details = new ArrayList<JobDetail>();
        String [] groupNames = scheduler.getJobGroupNames();
        for (String grpName : groupNames) {
            String[] jobNames = scheduler.getJobNames(grpName);
            for (String jobName : jobNames) {
                JobDetail jd = scheduler.getJobDetail(jobName, grpName);
                details.add(jd);
            }            
        }
        return details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    protected Collection getAllSchedulers() throws SchedulerException{
        if(schedulerFactory != null){
            return schedulerFactory.getAllSchedulers();
        }else if(scheduler != null){
            List l = new ArrayList(1);
            l.add(scheduler);
            return l;
        }
        return Collections.EMPTY_LIST;
    }

    public void setQuartzScheduler(Scheduler quartzScheduler) {
        this.quartzScheduler = quartzScheduler;
    }

    public BeanLocator getBeanLocator() {
        return beanLocator;
    }

    public void setBeanLocator(BeanLocator beanLocator) {
        this.beanLocator = beanLocator;
    }

    public QuartzJobListner getQuartzJobListner() {
        return quartzJobListner;
    }

    public void setQuartzJobListner(QuartzJobListner quartzJobListner) {
        this.quartzJobListner = quartzJobListner;
    }
    
}
