/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.web.action;

import java.util.Collection;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;


/**
 *
 * @author prasad.r
 */
public class QuartzAdminAction extends AbstractAction {

    @Override
    public void validate(){
    }
    
    
    @Override
    public void prepare() throws Exception {
        super.prepare();
    }
    
    public String listSchedulers() throws SchedulerException, JSONException{
        Collection schedulers = getAllSchedulers();
        JSONObject json = new JSONObject();
        json.put("total", schedulers.size()/10);
        json.put("page", 1);
        json.put("records", schedulers.size());
        JSONArray jsonArray = new JSONArray();
        for (Object object : schedulers) {
            JSONObject row = new JSONObject();
            Scheduler s = (Scheduler) object;
            String name = s.getSchedulerName();
            boolean isStarted = s.getMetaData().isStarted();
            Date runningSince = s.getMetaData().runningSince();
            long jobCount = getJobCount(s);
            row.put("scheduler.name", name);
            row.put("started", isStarted);
            row.put("runningSince", runningSince.toString());
            row.put("jobs.count", jobCount);
            jsonArray.put(row);
        }
        json.put("rows", jsonArray);
        writeJSONResponse(json.toString());
        return null;
    }
    

    private long getJobCount(Scheduler s) throws SchedulerException {
        long jobsCount = 0L;
        String [] groupNames = s.getJobGroupNames();
        for (String grpName : groupNames) {
            String[] jobNames = s.getJobNames(grpName);
            jobsCount += jobNames.length;
        }
        return jobsCount;
    }

    
}
