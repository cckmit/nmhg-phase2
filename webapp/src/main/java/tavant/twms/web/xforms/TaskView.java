/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.web.xforms;

import org.apache.commons.lang.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.jbpm.nodes.FormTaskNode;

import java.util.ArrayList;
import java.util.List;

public class TaskView {

    private static final String PREVIEW_FORM_SUFFIX = "_preview.xhtml";

    private static final String FORMS_PATH = "forms/";

    private static final String DETAIL_FORM_SUFFIX = "_detail.xhtml";

    private static final String RESPONSE_FORM_SUFFIX = "_response.xhtml";

    TaskInstance task;

    String takenTransition;

    String transferTo;

    String repliesTo;

    String seekAdviceFrom;
    
    String seekReviewFrom;

    List<String> warningMessages;

    List<String> errorMessages;

    Claim claim = null;

    OEMPartReplaced part;

    public TaskView() {
    }

    public TaskView(TaskInstance task) {
        this.task = task;
    }

    public List getTransitions() {
        if (task != null) {
            return getTransitionNames(getAvailableTransitions());
        }
        return null;
    }

    public List<Transition> getAvailableTransitions() {
        return getFormTaskNode(task).getAvailableTransitions(task);
    }

    List<String> getTransitionNames(List<Transition> availableTransitions) {
        List<String> transitions = new ArrayList<String>();
        for (Transition transition : availableTransitions) {
            transitions.add(transition.getName());
        }
        return transitions;
    }

    public long getTaskId() {
        if (task != null) {
            return task.getId();
        } else {
            return -1;
        }
    }

    public Claim getClaim() {
        if (task != null) {
            claim = (Claim) task.getVariable("claim");
        }
        return claim;
        //never really create the claim here.
        //return claim != null ? claim : new Claim();
    }

    public OEMPartReplaced getPart() {
        if (task != null) {
            part = (OEMPartReplaced) task.getVariable("part");
        }
        return part;
    }


    public void setClaim(Claim claim) {
        if (task != null) {
            task.setVariable("claim", claim);
        }
        this.claim = claim;
    }

    public String getTaskName() {
        if (task != null) {
            return task.getName();
        }
        return null;
    }

    /**
     * @return the takenTransition
     */
    public String getTakenTransition() {
        return takenTransition;
    }

    /**
     * @param takenTransition
     *            the takenTransition to set
     */
    public void setTakenTransition(String takenTransition) {
        this.takenTransition = takenTransition;
    }

    public String getBaseFormName(){
        FormTaskNode formTaskNode = null;
        formTaskNode = getFormTaskNode(task);
        return formTaskNode.getDefaultForm();
    }

    public String getFormName() {
        return getFormName(task);
    }

    public String getPreviewForm() {
        return FORMS_PATH + getFormTaskNode(task).getDefaultForm()
                + PREVIEW_FORM_SUFFIX;
    }

    public String getDetailForm() {
        return FORMS_PATH + getFormTaskNode(task).getDefaultForm()
                + DETAIL_FORM_SUFFIX;
    }

    public String getResponseForm() {
        return FORMS_PATH + getFormTaskNode(task).getDefaultForm()
                + RESPONSE_FORM_SUFFIX;
    }

    private String getFormName(TaskInstance taskInstance) {
        FormTaskNode formTaskNode = null;
        formTaskNode = getFormTaskNode(taskInstance);
        return "/forms/" + formTaskNode.getDefaultForm() + ".xhtml";
    }

    private FormTaskNode getFormTaskNode(TaskInstance taskInstance) {
        // TODO Method kind of a hack to get underlying FormTaskNode.
        TaskNode taskNode = taskInstance.getTask().getTaskNode();
        if (taskNode instanceof FormTaskNode) {
            return (FormTaskNode) taskNode;
        } else if (taskNode instanceof HibernateProxy) {
            return (FormTaskNode) ((HibernateProxy) taskNode).getHibernateLazyInitializer()
                    .getImplementation();
        }
        throw new RuntimeException("Node not a task node");
    }

    public TaskInstance getTask() {
        return task;
    }

    public void setTask(TaskInstance task) {
        this.task = task;
    }

    public String getTransferTo() {
        return transferTo;
    }

    public void setTransferTo(String transferTo) {
        this.transferTo = transferTo;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(List<String> warningMessages) {
        this.warningMessages = warningMessages;
    }

    public String getSeekAdviceFrom() {
        return seekAdviceFrom;
    }

    public void setSeekAdviceFrom(String seekAdviceFrom) {
        this.seekAdviceFrom = seekAdviceFrom;
    }
    
    public String getSeekReviewFrom() {
        return seekReviewFrom;
    }

    public void setSeekReviewFrom(String seekReviewFrom) {
        this.seekReviewFrom = seekReviewFrom;
    }

    // TODO - hack to get the processor review
    // page working - need to find a correct way of doing this
    public String getAssignTo(){
        if (StringUtils.isNotEmpty(transferTo)) {
            return transferTo;
        }else if (StringUtils.isNotEmpty(seekReviewFrom)) {
            return seekReviewFrom;    
        }else if(StringUtils.isNotEmpty(repliesTo)) {
            return repliesTo;
        }
        else {
            return seekAdviceFrom;
        }
    }

    public boolean isPartsClaim() {
        return InstanceOfUtil.isInstanceOfClass(PartsClaim.class, getClaim());
    }

    public String getRepliesTo() {
        return repliesTo;
    }

    public void setRepliesTo(String repliesTo) {
        this.repliesTo = repliesTo;
    }
}
