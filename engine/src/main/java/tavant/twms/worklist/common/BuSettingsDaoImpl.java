package tavant.twms.worklist.common;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import tavant.twms.domain.common.BUSettings;

import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 26/12/13
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuSettingsDaoImpl extends HibernateDaoSupport implements BuSettingsDao {

    @Transactional(readOnly = true)
    public String getSetting(final String keyName){
        BUSettings result =  (BUSettings) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from BUSettings busetting where busetting.keyName = :keyName").setParameter("keyName", keyName).uniqueResult();
            }

        });
        return result != null ? result.getKeyValue() : null;
    }

    @Transactional(readOnly = true)
    public String getStringSetting(final BUSetting setting, final String... params){
        return getSetting(setting.setting(params), setting.getFallback());
    }

    @Transactional(readOnly = true)
    public boolean getBooleanSetting(final BUSetting setting, final String... params){
        return Boolean.parseBoolean(getStringSetting(setting, params));
    }

    @Transactional(readOnly = true)
    public int getIntSetting(final BUSetting setting, final String... params){
        return getSetting(setting.setting(params), Integer.parseInt(setting.getFallback()));
    }

    @Transactional(readOnly = true)
    private String getSetting(final String name, final String fallbackValue)
    {
        String result = getSetting(name);
        if (result == null)
        {
            result = fallbackValue;
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<BUSettings> getAllConfigurationSettings(){
        return getHibernateTemplate().loadAll(BUSettings.class);
    }

    @Transactional(readOnly = true)
    private int getSetting(final String name, final int fallbackValue)
    {
        String value = getSetting(name, String.valueOf(fallbackValue));
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex)
        {
            return fallbackValue;
        }
    }

}
