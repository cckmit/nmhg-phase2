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
public interface BuSettingsService {

    public String getSetting(String keyName);

    public String getStringSetting(BUSetting setting, String... params);

    public boolean getBooleanSetting(BUSetting setting, String... params);

    public int getIntSetting(BUSetting setting, String... params);

    public List<BUSettings> getAllBUSetting();

}
