/**
 * 
 */
package  tavant.twms.domain.alarmcode;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author surajdeo.prasad
 * 
 */
public interface AlarmCodeService extends
		GenericService<AlarmCode, Long, Exception> {
	public PageResult<AlarmCode> findPage(ListCriteria listCriteria);

	public boolean checkDuplicateCode(AlarmCode alarmCode);

	public List<AlarmCode> findAllAlarmCodeOfProductWithNameLike(
			final String partialAlarmCode, final ItemGroup itemGroup,
			final int pageNumber, final int pageSize);

	public AlarmCode alarmCodeByCode(final String code);
	
	public List<AlarmCode> findAlarmCodesOfProductByCodes(
			final List<String> alarmCodeList, final ItemGroup itemGroup);
	
	public List<AlarmCode> getACListFromAlarmCodes(final List<String> alramCodeList);
	
	public  List<AlarmCode> getFaultCodeList(String searchPrefix );
	
	public List<ItemGroup> ListAllProductCodesMatchingName(String partialProductName);
	
	public List<AlarmCode> getFaultCodeListUsingProductIdAndCode(String code,Long productId);
}
