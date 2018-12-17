package tavant.twms.worklist.common;

import tavant.twms.domain.common.BUSettings;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 27/12/13
 * Time: 11:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class BuSettingsServiceImpl implements BuSettingsService {

    private BuSettingsDao buSettingsDao;

    public BuSettingsDao getBuSettingsDao() {
        return buSettingsDao;
    }

    public void setBuSettingsDao(BuSettingsDao buSettingsDao) {
        this.buSettingsDao = buSettingsDao;
    }

    public String getSetting(String keyName){
        return buSettingsDao.getSetting(keyName);
    }

    public String getStringSetting(BUSetting setting, String... params){
         return buSettingsDao.getStringSetting(setting, params);
    }

    public boolean getBooleanSetting(BUSetting setting, String... params){
        return buSettingsDao.getBooleanSetting(setting, params);
    }

    public int getIntSetting(BUSetting setting, String... params){
        return buSettingsDao.getIntSetting(setting, params);
    }

    public List<BUSettings> getAllBUSetting(){
        return buSettingsDao.getAllConfigurationSettings();
    }

}
