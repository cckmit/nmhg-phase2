package tavant.twms.integration.layer.component;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.external.IntegrationBridge;
import tavant.twms.integration.layer.IntegrationRepositoryTestCase;
import tavant.twms.integration.layer.transformer.PriceCheckResponseTransformer;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class PriceCheckTest extends IntegrationRepositoryTestCase {

	ProcessPriceCheck priceCheck;

	IntegrationBridge integrationBridge;
	
	private PriceCheckResponseTransformer priceCheckResponseTransformer;
	
	public void testPriceCheckResponse(){
		String xml = "";
		//priceCheckResponseTransformer.transform(xml);
	}
	
	public void testPriceCheckRequest() {
		PartsClaim claim = getClaim();
/*		PriceCheckResponse priceCheckResponse = integrationBridge.checkPrice(claim);
		System.out.println(priceCheckResponse.toString());
*/	}

	private PartsClaim getClaim() {
		PartsClaim claim = new PartsClaim();
		claim.setId(Long.valueOf(1));
		claim.setClaimNumber("1");
		claim.setConditionFound("new");

		Dealership dealership = new Dealership();
		dealership.setDealerNumber("some dealer");
		dealership.setName("some dealer");
		dealership.setPreferredCurrency(Currency.getInstance("USD"));
		claim.setForDealerShip(dealership);

		claim.setFiledOnDate(CalendarDate.date(2007, 10, 20));
		claim.setRepairDate(CalendarDate.date(2007, 10, 30));
		ItemReference itemRef = new ItemReference();
		InventoryItem invItem = new InventoryItem();
		invItem.setSerialNumber("SEriAlNumBER");
		itemRef.setReferredInventoryItem(invItem);
		claim.setItemReference(itemRef);

		claim.setServiceInformation(createServiceInformation());

		return claim;
	}

	private ServiceInformation createServiceInformation() {
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setServiceDetail(createServiceDetail());
		return serviceInformation;
	}

	private ServiceDetail createServiceDetail() {
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceDetail.setOEMPartsReplaced(createOEMPartsReplaced());
		return serviceDetail;
	}

	private List<OEMPartReplaced> createOEMPartsReplaced() {
		List<OEMPartReplaced> oemList = new ArrayList<OEMPartReplaced>();
		oemList.add(createOEMPart("01", 2, 10));
		oemList.add(createOEMPart("02", 2, 20));
		return oemList;
	}

	private OEMPartReplaced createOEMPart(String partNumber, int noOfUnits,
			int pricePerUnit) {
		OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
		oemPartReplaced.setId(Long.parseLong(partNumber));
		oemPartReplaced.setNumberOfUnits(noOfUnits);
		oemPartReplaced.setPricePerUnit(Money.dollars(pricePerUnit));
		return oemPartReplaced;
	}
	public void setPriceCheckResponseTransformer(
			PriceCheckResponseTransformer priceCheckResponseTransformer) {
		this.priceCheckResponseTransformer = priceCheckResponseTransformer;
	}

	public void setIntegrationBridge(IntegrationBridge integrationBridge) {
		this.integrationBridge = integrationBridge;
	}

	public void setPriceCheck(ProcessPriceCheck priceCheck) {
		this.priceCheck = priceCheck;
	}
}
