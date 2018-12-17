package tavant.twms.web.claim.alarmcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.alarmcode.AlarmCodeService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author surajdeo.prasad
 * 
 */

@SuppressWarnings("serial")
public class ManageAlarmCode extends SummaryTableAction implements Preparable,
		Validateable {
	private AlarmCodeService alarmCodeService;
	private AlarmCode alarmCode = new AlarmCode();

	@Override
	protected PageResult<?> getBody() {
		return alarmCodeService.findPage(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "String", false,
				true, true, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.code",
				"code", 20, "String", "code", true, false, false, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.common.description", "description", 50, "String",
				"description", true, false, false, false));
	   return tableHeadData;
	}

	public void setAlarmCodeService(AlarmCodeService alarmCodeService) {
		this.alarmCodeService = alarmCodeService;
	}

	public void prepare() throws Exception {
		// TODO Auto-generated method stub

	}

	public String saveAlarmCodes() throws Exception {
		if (hasActionErrors()) {
			return INPUT;
		}
		this.alarmCodeService.save(alarmCode);
		addActionMessage("alarmcode.add.success");
		return SUCCESS;
	}

	public String modifyAlarmCodes() throws Exception {
		if (hasActionErrors()) {
			return INPUT;
		}
		this.alarmCodeService.update(alarmCode);
		addActionMessage("alarmcode.update.success");
		return SUCCESS;
	}
	
	@Override
	public void validate() {
		validateAlarmCodes();
		if (!hasActionErrors()) {
			if (isNullOrEmpty(alarmCode.getCode())) {
				addActionError("alarmcode.noinput", new String[] { "Code" });
			} else if (!alarmCodeService.checkDuplicateCode(alarmCode)) {
				addActionError("alarmcode.code.duplicate");
			} else {
				Set<Long> uniqueProductId = new HashSet<Long>();
				for (ItemGroup group : alarmCode.getApplicableProducts()) {
					if (!uniqueProductId.add(group.getId())) {
						addActionError("alarmcode.product.duplicate");
						break;
					}
				}
			}
		}
	}
	
	private void validateAlarmCodes() {
		List<ItemGroup> itemGroupList = alarmCode.getApplicableProducts();
		String code = alarmCode.getCode();
		String description = alarmCode.getDescription();
		if(itemGroupList != null && !itemGroupList.isEmpty()) {
			for(ItemGroup itemGroup : itemGroupList) {
				if(itemGroup.getId() == null) {
					addActionError("error.manageMLR.validProduct");
				}
			}
		}
		if(code != null) {
			if(code.trim().length() == 0) {
				addActionError("alarmcode.noinput", new String[] { "Code" });
			} else {
				alarmCode.setCode(code.trim());
			}
		}
		if(description != null) {
			if(description.trim().length() == 0) {
				addActionError("alarmcode.noinput", new String[] { "Description" });
			} else {
				alarmCode.setDescription(description.trim());
			}
		}
		
	}

	private boolean isNullOrEmpty(final String str) {
		return str == null || str.length() <= 0;
	}

	public String createAlarmCodeData() {
		this.alarmCode = new AlarmCode();
		return SUCCESS;
	}

	public String loadAlarmCodeData() {
		this.alarmCode = this.alarmCodeService.findById(Long.valueOf(id));
		return SUCCESS;
	}

	public AlarmCode getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(AlarmCode alarmCode) {
		this.alarmCode = alarmCode;
	}
}
