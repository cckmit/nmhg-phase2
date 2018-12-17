package tavant.twms.web.typeconverters;

import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.catalog.MiscellaneousItemConfigRepository;

public class MiscellanousPartNumberConverter
		extends
		NamedDomainObjectConverter<MiscellaneousItemConfigRepository, MiscellaneousItem> {
	public MiscellanousPartNumberConverter() {
		super("miscellaneousItemConfigRepository");
	}

	/* (non-Javadoc)
	 * @see tavant.twms.web.typeconverters.NamedDomainObjectConverter#fetchByName(java.lang.String)
	 */
	@Override
	public MiscellaneousItem fetchByName(String partNumber) throws Exception {
		MiscellaneousItem miscPart = getService()
				.findMiscellaneousItemByPartNumber(partNumber);
		if (miscPart != null) {
			return miscPart;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see tavant.twms.web.typeconverters.NamedDomainObjectConverter#getName(java.lang.Object)
	 */
	@Override
	public String getName(MiscellaneousItem miscPart) throws Exception {
		return miscPart.getPartNumber();
	}

}
