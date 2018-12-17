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

package tavant.twms.web.i18n;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import tavant.twms.infra.i18n.LocalizedMessages;
import tavant.twms.infra.i18n.LocalizedMessagesService;

import java.io.InputStreamReader;
import java.util.Locale;

public class UploadMessageResources implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private LocalizedMessagesService localizedMessagesService;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setLocalizedMessagesService(LocalizedMessagesService localizedMessagesService) {
        this.localizedMessagesService = localizedMessagesService;
    }

    public void afterPropertiesSet() throws Exception {
        String envMode = System.getProperty("TWMS_ENV_MODE");
        if (envMode != null && !envMode.equals("dev")) {
            Resource[] resources = applicationContext.getResources("classpath*:/messages_*.properties");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                String localeString = filename.substring(filename.indexOf('_')+1, filename.indexOf('.'));
                upload(resource, StringUtils.parseLocaleString(localeString));
            }
        }
    }

    private void upload(Resource resource, Locale locale) throws Exception {

        LocalizedMessages localizedMessages = localizedMessagesService.findById(locale);

        if (localizedMessages == null) {
            localizedMessages = new LocalizedMessages();
            localizedMessages.setLocale(locale);
            localizedMessages.setMessages(
                    FileCopyUtils.copyToString(
                            new InputStreamReader(
                                    resource.getInputStream())));

            localizedMessagesService.save(localizedMessages);
        }
    }
}
