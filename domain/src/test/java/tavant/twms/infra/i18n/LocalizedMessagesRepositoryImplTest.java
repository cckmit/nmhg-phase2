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

package tavant.twms.infra.i18n;

import org.springframework.util.DefaultPropertiesPersister;
import tavant.twms.infra.DomainRepositoryTestCase;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Properties;

public class LocalizedMessagesRepositoryImplTest extends DomainRepositoryTestCase {

    private LocalizedMessagesRepository localizedMessagesRepository;

    public void testSave() throws IOException {

        LocalizedMessages messages = new LocalizedMessages();
        messages.setLocale(Locale.GERMANY);
        messages.setMessages(getPropertiesAsString());

        localizedMessagesRepository.save(messages);
        flushAndClear();

        messages = localizedMessagesRepository.findById(Locale.GERMANY);
        assertEquals("über", getPropertiesFromString(messages.getMessages()).get("key"));
    }

    private String getPropertiesAsString() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("key", "über");
        StringWriter sw = new StringWriter();
        DefaultPropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
        propertiesPersister.store(properties, sw, null);
        return sw.toString();
    }

    private Properties getPropertiesFromString(String messages) throws IOException {
        StringReader reader = new StringReader(messages);
        DefaultPropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
        Properties properties = new Properties();
        propertiesPersister.load(properties, reader);
        return properties;
    }

    public void setLocalizedMessagesRepository(LocalizedMessagesRepository localizedMessagesRepository) {
        this.localizedMessagesRepository = localizedMessagesRepository;
    }

}
