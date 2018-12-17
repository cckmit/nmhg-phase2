package tavant.twms.worklist.common;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitInfo;

import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 21/12/13
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */
public enum BUSetting {


    PRINT_PDI("Print PDI","To Print the PDI for DR/ETR", "DR.print.pdi.{0}","false"),
    
    /*following has been moved to configName.java*/
    CREATE_TECHNICIAN("Create Technician Hyperlink","To hide Create Technician hyperlink in claim pages for Processor","create.technician.hyperlink.{0}","false"),
    VIEW_UNIT_COMMENTS("View Unit Comments","To View Unit Comments in claim Pages for Processor", "claim.view.UnitComments.{0}","false");
    
    private String name;
    private String description;
    private String setting;
    private String fallback;

    BUSetting(String name, String description, String setting, String fallback){
        this.name = name;
        this.description = description;
        this.setting = setting;
        this.fallback = fallback;
    }

    public String getFallback(){
        return this.fallback;
    }

    public String setting(final String... parameters)
    {
        return MessageFormat.format(setting, (Object[]) parameters);
    }

    public static BUSetting forKey(final String key)
    {
        for (final BUSetting settings : values())
        {
            String keyRe = settings.setting.replace(".", "\\.").replaceAll("\\{[0-9]\\}", ".*?");
            Pattern p = Pattern.compile(keyRe);
            if (p.matcher(key).matches())
            {
                return settings;
            }
        }
        return null;
    }
}
