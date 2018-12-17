/**
 * 
 */
package tavant.twms.web.common;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import tavant.twms.infra.i18n.LocalizedMessages;
import tavant.twms.infra.i18n.LocalizedMessagesService;
import tavant.twms.web.actions.TwmsActionSupport;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

/**
 * @author aniruddha.chaturvedi
 *
 */
@SuppressWarnings("serial")
public class InternationalisationAction extends TwmsActionSupport implements Preparable, ServletResponseAware, Validateable {
	
	private LocalizedMessagesService localizedMessagesService;
	private List<LocalizedMessages> messages;
	private String localeStr;
	private HttpServletResponse httpServletResponse;
	private File upload;

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public void prepare() throws Exception {
		messages = localizedMessagesService.findAll();
	}
	
	public String listLocalisedFiles() {
		return SUCCESS;
	}
	
	public void downloadFile() throws IOException {
		Locale locale = StringUtils.parseLocaleString(localeStr);
		LocalizedMessages message = localizedMessagesService.findById(locale);
		httpServletResponse.setHeader("Content-disposition", "attachment; filename=messages_"+localeStr+".properties");
		httpServletResponse.setContentType("text/plain");
        PrintWriter pw = new PrintWriter(httpServletResponse.getOutputStream());
        pw.print(message.getMessages());
        pw.flush();
    }

	public String uploadFile() throws Exception {
		Locale locale = StringUtils.parseLocaleString(localeStr);
		LocalizedMessages message = localizedMessagesService.findById(locale);
        message.setMessages(FileCopyUtils.copyToString(new FileReader(upload)));
		localizedMessagesService.update(message);
		addActionMessage("message.uploads.fileUploaded");
		return SUCCESS;
	}
	
	@Override
	public void validate() {
		//TODO
	}
	
	public List<LocalizedMessages> getMessages() {
		return messages;
	}

	public void setMessages(List<LocalizedMessages> messages) {
		this.messages = messages;
	}

	public void setLocalizedMessagesService(
			LocalizedMessagesService localizedMessagesService) {
		this.localizedMessagesService = localizedMessagesService;
	}

	public void setServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}

	public String getLocaleStr() {
		return localeStr;
	}

	public void setLocaleStr(String localeStr) {
		this.localeStr = localeStr;
	}
}
