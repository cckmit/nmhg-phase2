package tavant.twms.web.upload;

import com.opensymphony.xwork2.validator.DelegatingValidatorContext;

import java.util.*;

public class GenericValidatorContext extends DelegatingValidatorContext {

    private Collection actionErrors;

    private Collection actionMessages;

    private Map fieldErrors;

    public GenericValidatorContext(Object object) {
        super(object);
    }

    public synchronized void setActionErrors(Collection errorMessages) {
        this.actionErrors = errorMessages;
    }

    @SuppressWarnings("unchecked")
    public synchronized Collection getActionErrors() {
        return new ArrayList(internalGetActionErrors());
    }

    public synchronized void setActionMessages(Collection messages) {
        this.actionMessages = messages;
    }

    @SuppressWarnings("unchecked")
    public synchronized Collection getActionMessages() {
        return new ArrayList(internalGetActionMessages());
    }

    public synchronized void setFieldErrors(Map errorMap) {
        this.fieldErrors = errorMap;
    }

    @SuppressWarnings("unchecked")
    public synchronized Map getFieldErrors() {
        return new HashMap(internalGetFieldErrors());
    }

    @SuppressWarnings("unchecked")
    public synchronized void addActionError(String anErrorMessage) {
        internalGetActionErrors().add(anErrorMessage);
    }

    @SuppressWarnings("unchecked")
    public void addActionMessage(String aMessage) {
        internalGetActionMessages().add(aMessage);
    }

    @SuppressWarnings("unchecked")
    public synchronized void addFieldError(String fieldName, String errorMessage) {

        final Map errors = internalGetFieldErrors();
        List thisFieldErrors = (List) errors.get(fieldName);
        if (thisFieldErrors == null) {
            thisFieldErrors = new ArrayList();
            errors.put(fieldName, thisFieldErrors);
        }
        thisFieldErrors.add(errorMessage);
    }

    public synchronized boolean hasActionErrors() {
        return (actionErrors != null) && !actionErrors.isEmpty();
    }

    public synchronized boolean hasErrors() {
        return (hasActionErrors() || hasFieldErrors());
    }

    public synchronized boolean hasFieldErrors() {
        return (fieldErrors != null) && !fieldErrors.isEmpty();
    }

    private Collection internalGetActionErrors() {

        if (actionErrors == null) {
            actionErrors = new ArrayList();
        }
        return actionErrors;
    }

    private Collection internalGetActionMessages() {

        if (actionMessages == null) {
            actionMessages = new ArrayList();
        }
        return actionMessages;
    }

    private Map internalGetFieldErrors() {

        if (fieldErrors == null) {
            fieldErrors = new HashMap();
        }
        return fieldErrors;
    }

}
