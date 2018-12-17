package tavant.twms.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import java.io.File;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.FileUploadInterceptor;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 29 Jun, 2009
 * Time: 5:13:51 PM
 */
public class TwmsFileUploadInterceptor extends FileUploadInterceptor {

    private static final String DEFAULT_MESSAGE = "no.message.found";   
    private static final Logger log = Logger.getLogger(TwmsFileUploadInterceptor.class);
    // Shamelessly copy-pasting entire method from the original Struts2 interceptor. Am afraid there is no way around
    // this, since they have put all the logic in this one monolithic method and the stuff that we need to override
    // comes somwhere in the middle! :-( -- Vikas Sasidharan
    @SuppressWarnings("unchecked")
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);

        if (!(request instanceof MultiPartRequestWrapper)) {
            if (log.isDebugEnabled()) {
                ActionProxy proxy = invocation.getProxy();
                log.debug(getTextMessage("struts.messages.bypass.request", new Object[]{proxy.getNamespace(),
                        proxy.getActionName()}, ActionContext.getContext().getLocale()));
            }

            return invocation.invoke();
        }

        final Object action = invocation.getAction();
        ValidationAware validation = null;

        if (action instanceof ValidationAware) {
            validation = (ValidationAware) action;
        }

        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper) request;

        if (multiWrapper.hasErrors()) {
            for (Object o : multiWrapper.getErrors()) {
                String error = (String) o;

                if (validation != null) {
                    validation.addActionError(error);
                }

                log.error(error);
            }
        }

        Map parameters = ac.getParameters();
        
        // Bind allowed Files
        Enumeration fileParameterNames = multiWrapper.getFileParameterNames();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            // get the value of this input tag
            String inputName = (String) fileParameterNames.nextElement();

            // get the content type
            String[] contentType = multiWrapper.getContentTypes(inputName);

            if (isNonEmpty(contentType)) {
                // get the name of the file from the input tag
                String[] fileName = multiWrapper.getFileNames(inputName);

                if (isNonEmpty(fileName)) {
                    // Get a File object for the uploaded File
                    File[] files = multiWrapper.getFiles(inputName);
                    if (files != null) {
                        for (int index = 0; index < files.length; index++) {

                            if (acceptFile(files[index], contentType[index], inputName, validation, ac.getLocale(),
                                    fileName)) {
                                parameters.put(inputName, files);
                                parameters.put(inputName + ".contentType", contentType);
                                parameters.put(inputName + ".fileName", fileName);
                            }
                        }
                    }
                } else {
                    log.error(getTextMessage("struts.messages.invalid.file", new Object[]{inputName},
                            ActionContext.getContext().getLocale()));
                }
            } else {
                log.error(getTextMessage("struts.messages.invalid.content.type", new Object[]{inputName},
                        ActionContext.getContext().getLocale()));
            }
        }

        // invoke action
        String result = invocation.invoke();

        // cleanup
        fileParameterNames = multiWrapper.getFileParameterNames();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            String inputValue = (String) fileParameterNames.nextElement();
            File[] file = multiWrapper.getFiles(inputValue);
            for (File currentFile : file) {

                if(log.isInfoEnabled()) {
                    log.info(getTextMessage("struts.messages.removing.file", new Object[]{inputValue, currentFile},
                            ActionContext.getContext().getLocale()));
                }

                if ((currentFile != null) && currentFile.isFile()) {
                    currentFile.delete();
                }
            }
        }

        return result;
    }
     private static boolean containsItem(Collection itemCollection, String key) {
        return itemCollection.contains(key.toLowerCase());
    }

    // Overridden to allow the use of filenames in the function required to display meaningful error message.
    protected boolean acceptFile(File file, String contentType, String inputName, ValidationAware validation,
                                 Locale locale, String[] fileName) {

        boolean fileIsAcceptable = false;

        // If it's null the upload failed
        if (file == null) {
            String errMsg = getTextMessage("struts.messages.error.uploading", new Object[]{inputName}, locale);
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            log.error(errMsg);
            
        } else if (maximumSize != null && maximumSize < file.length()) {
            String errMsg = getTextMessage("struts.messages.error.file.too.large",
                    new Object[]{inputName, fileName[0], file.length()}, locale);

            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            log.error(errMsg);

        } else if ((! allowedTypesSet.isEmpty()) && (!containsItem(allowedTypesSet, contentType))) {
            String errMsg = getTextMessage("struts.messages.error.content.type.not.allowed",
                    new Object[]{inputName, file.getName(), contentType}, locale);

            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            log.error(errMsg);

        } else {
            fileIsAcceptable = true;
        }

        return fileIsAcceptable;
    }

    private static boolean isNonEmpty(Object[] objArray) {
        boolean result = false;
        for (int index = 0; index < objArray.length && !result; index++) {
            if (objArray[index] != null) {
                result = true;
            }
        }
        return result;
    }

    private String getTextMessage(String messageKey, Object[] args, Locale locale) {
        if (args == null || args.length == 0) {
            return LocalizedTextUtil.findText(this.getClass(), messageKey, locale);
        } else {
            return LocalizedTextUtil.findText(this.getClass(), messageKey, locale, DEFAULT_MESSAGE, args);
        }
    }  
   
}