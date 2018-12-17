/**
 * 
 */
package  tavant.twms.domain.alarmcode;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepository;

/**
 * @author surajdeo.prasad
 * 
 */
public interface AlarmCodeRepository extends GenericRepository<AlarmCode, Long> {
	public List<AlarmCode> findAllAlarmCodeOfProductWithNameLike(
			final String partialAlarmCode, final ItemGroup itemGroup, final int pageNumber, final int pageSize);
	public AlarmCode alarmCodeByCode(final String code);
	public List<AlarmCode> findAlarmCodesOfProductByCodes(
			final List<String> alarmCodes, final ItemGroup itemGroup);
	public List<AlarmCode> getACListFromAlarmCodes(final List<String> alramCodeList);
	  public List<AlarmCode> findFaultcodes( final String partialAlarmCode) ;
	  public List<ItemGroup> ListAllProductCodesMatchingName(final String partialProductName);
	public List<AlarmCode> getFaultCodeListUsingProductIdAndCode(String code,
			Long productId);
}
