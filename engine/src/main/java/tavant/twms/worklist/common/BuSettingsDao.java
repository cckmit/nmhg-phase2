package tavant.twms.worklist.common;

import org.springframework.transaction.annotation.Transactional;
import tavant.twms.domain.common.BUSettings;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 26/12/13
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BuSettingsDao {

    @Transactional(readOnly = true)
    public String getSetting(final String name);

    @Transactional(readOnly = true)
    public String getStringSetting(final BUSetting setting, final String... params);

    @Transactional(readOnly = true)
    public boolean getBooleanSetting(final BUSetting setting, final String... params);

    @Transactional(readOnly = true)
    public int getIntSetting(final BUSetting setting, final String... params);

    @Transactional(readOnly = true)
    public List<BUSettings> getAllConfigurationSettings();


}
