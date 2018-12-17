/**
 * 
 */
package  tavant.twms.domain.alarmcode;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author surajdeo.prasad
 * 
 */
public class AlarmCodeServiceImpl extends
		GenericServiceImpl<AlarmCode, Long, Exception> implements
		AlarmCodeService {
	private AlarmCodeRepository alarmCodeRepository;

	public PageResult<AlarmCode> findPage(ListCriteria listCriteria) {
		return alarmCodeRepository.findPage(" from AlarmCode alarmCode",
				listCriteria);
	}

	public boolean checkDuplicateCode(AlarmCode alarmCode) {
		List<AlarmCode> list = alarmCodeRepository
				.findEntitiesThatMatchPropertyValue("code", alarmCode);
		if (!isNullOrZero(alarmCode.getId())) {
			List<AlarmCode> codeList = alarmCodeRepository.findEntitiesThatMatchPropertyValue("id", alarmCode);
			if(codeList != null && !codeList.isEmpty()) {
				AlarmCode modifiableCode = codeList.get(0);
				for (AlarmCode code : list) {
					if (modifiableCode.getId().longValue() == code.getId()
							.longValue()) {
						list.remove(code);
						break;
					}
				}
			}
		}

		return list == null || list.isEmpty();
	}

	private boolean isNullOrZero(Long id) {
		return id == null || id.longValue() == 0;
	}

	@Override
	public GenericRepository<AlarmCode, Long> getRepository() {
		return alarmCodeRepository;
	}

	public void setAlarmCodeRepository(AlarmCodeRepository alarmCodeRepository) {
		this.alarmCodeRepository = alarmCodeRepository;
	}

	public List<AlarmCode> findAllAlarmCodeOfProductWithNameLike(
			final String partialAlarmCode, final ItemGroup itemGroup,
			final int pageNumber, final int pageSize) {
		return alarmCodeRepository.findAllAlarmCodeOfProductWithNameLike(
				partialAlarmCode, itemGroup, pageNumber, pageSize);
	}

	public AlarmCode alarmCodeByCode(final String code) {
		return alarmCodeRepository.alarmCodeByCode(code);
	}
	
	public List<AlarmCode> findAlarmCodesOfProductByCodes(
			final List<String> alarmCodes, final ItemGroup itemGroup) {
		return alarmCodeRepository.findAlarmCodesOfProductByCodes(alarmCodes, itemGroup);
	}
	
	public List<AlarmCode> getACListFromAlarmCodes(final List<String> alramCodeList){
		return alarmCodeRepository.getACListFromAlarmCodes(alramCodeList);
	}
	
	public List<AlarmCode> getFaultCodeList(String searchPrefix)
	{
	   return alarmCodeRepository.findFaultcodes(searchPrefix);
	}
	
	  public List<ItemGroup> ListAllProductCodesMatchingName(String partialProductName) {
	        return alarmCodeRepository.ListAllProductCodesMatchingName(partialProductName);
	  }
	  
	  public List<AlarmCode> getFaultCodeListUsingProductIdAndCode(String code,Long productId){
		  return alarmCodeRepository.getFaultCodeListUsingProductIdAndCode(code,productId);
	  }
}
