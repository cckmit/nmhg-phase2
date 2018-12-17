package tavant.twms.integration.layer.util;
public class LocaleUtil {
	public static String getLocale(String descriptionLanguage) {
		if(descriptionLanguage!=null){
		if (descriptionLanguage.equalsIgnoreCase("DE")) {
			return "de_DE";
		} else if (descriptionLanguage.equalsIgnoreCase("ES")) {

			return "es_ES";
		} else if (descriptionLanguage.equalsIgnoreCase("FR")) {

			return "fr_FR";
		} else if (descriptionLanguage.equalsIgnoreCase("IT")) {

			return "it_IT";
		} else if (descriptionLanguage.equalsIgnoreCase("NL")) {

			return "nl_NL";
		} else if (descriptionLanguage.equalsIgnoreCase("EN")) {

			return "en_GB";
		} else if (descriptionLanguage.equalsIgnoreCase("ZH")) {

			return "zh_CN";
		}
		return "en_US";
		}
		return "en_US";
	}
	
}