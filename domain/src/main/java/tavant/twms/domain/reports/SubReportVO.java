package tavant.twms.domain.reports;

import java.math.BigDecimal;

import com.domainlanguage.money.Money;

public class SubReportVO {
	//TODO:Replace Transformers.aliasToBean to Map in claimreportrepository so that the VO can be removed.
	public long count;
	public long countParts;
	public String status;
	public String dealer;
	public String partStatus;
	public long notReturned;
	public long outsideReturn;
	public long withinReturn;
	public long pdwCount5;
	public long pdwCount10;
	public long pdwCount15;
	public long pdwCount30;
	public long powCount5;
	public long powCount10;
	public long powCount15;
	public long powCount30;
	public String supplier;
    public Money costAfterApplyingContract;
    public Money recoveredCost;
    public int month;
    public int year;
	public BigDecimal oemPartLastYear;
	public BigDecimal oemPartCurrYear;
	public BigDecimal nonOemPartLastYear;
	public BigDecimal nonOemPartCurrYear;
	public BigDecimal laborLastYear;
	public BigDecimal laborCurrYear;
	public BigDecimal travelByTripLastYear;
	public BigDecimal travelByTripCurrYear;
	public BigDecimal travelByHrsLastYear;
	public BigDecimal travelByHrsCurrYear;
	public BigDecimal travelByDistLastYear;
	public BigDecimal travelByDistCurrYear;
	public BigDecimal itemFreightDutyLastYear;
	public BigDecimal itemFreightDutyCurrYear;
	public BigDecimal mealsLastYear;
	public BigDecimal mealsCurrYear;
	public BigDecimal handlingFeeLastYear;
	public BigDecimal handlingFeeCurrYear;
	public BigDecimal transportationLastYear;
	public BigDecimal transportationCurrYear;
	public BigDecimal parkingLastYear;
	public BigDecimal parkingCurrYear;
	public BigDecimal miscPartsLastYear;
	public BigDecimal miscPartsCurrYear;
	public BigDecimal perDiemLastYear;
	public BigDecimal perDiemCurrYear;
	public BigDecimal rentalLastYear;
	public BigDecimal rentalCurrYear;
	public BigDecimal addTravelLastYear;
	public BigDecimal addTravelCurrYear;
	public BigDecimal localPurchaseLastYear;
	public BigDecimal localPurchaseCurrYear;
	public BigDecimal otherFreightLastYear;
	public BigDecimal otherFreightCurrYear;
	public BigDecimal othersLastYear;
	public BigDecimal othersCurrYear;
	public BigDecimal tollsLastYear;
	public BigDecimal tollsCurrYear;
	public String productName;
	public String modelName;
	public long modelCount;
	public BigDecimal modelSum;
	public String faultName;
	public BigDecimal totalAmountLastYear;
	public BigDecimal totalAmountCurrentYear;
	public BigDecimal taxLastYear;
	public BigDecimal taxCurrentYear;


	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getCountParts() {
		return countParts;
	}
	public void setCountParts(long countParts) {
		this.countParts = countParts;
	}
	public String getDealer() {
		return dealer;
	}
	public void setDealer(String dealer) {
		this.dealer = dealer;
	}
	public long getNotReturned() {
		return notReturned;
	}
	public void setNotReturned(long notReturned) {
		this.notReturned = notReturned;
	}
	public long getOutsideReturn() {
		return outsideReturn;
	}
	public void setOutsideReturn(long outsideReturn) {
		this.outsideReturn = outsideReturn;
	}
	public String getPartStatus() {
		return partStatus;
	}
	public void setPartStatus(String partStatus) {
		this.partStatus = partStatus;
	}
	public long getPdwCount10() {
		return pdwCount10;
	}
	public void setPdwCount10(long pdwCount10) {
		this.pdwCount10 = pdwCount10;
	}
	public long getPdwCount15() {
		return pdwCount15;
	}
	public void setPdwCount15(long pdwCount15) {
		this.pdwCount15 = pdwCount15;
	}
	public long getPdwCount30() {
		return pdwCount30;
	}
	public void setPdwCount30(long pdwCount30) {
		this.pdwCount30 = pdwCount30;
	}
	public long getPdwCount5() {
		return pdwCount5;
	}
	public void setPdwCount5(long pdwCount5) {
		this.pdwCount5 = pdwCount5;
	}
	public long getPowCount10() {
		return powCount10;
	}
	public void setPowCount10(long powCount10) {
		this.powCount10 = powCount10;
	}
	public long getPowCount15() {
		return powCount15;
	}
	public void setPowCount15(long powCount15) {
		this.powCount15 = powCount15;
	}
	public long getPowCount30() {
		return powCount30;
	}
	public void setPowCount30(long powCount30) {
		this.powCount30 = powCount30;
	}
	public long getPowCount5() {
		return powCount5;
	}
	public void setPowCount5(long powCount5) {
		this.powCount5 = powCount5;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getWithinReturn() {
		return withinReturn;
	}
	public void setWithinReturn(long withinReturn) {
		this.withinReturn = withinReturn;
	}
	public Money getCostAfterApplyingContract() {
		return costAfterApplyingContract;
	}
	public void setCostAfterApplyingContract(Money costAfterApplyingContract) {
		this.costAfterApplyingContract = costAfterApplyingContract;
	}
	public Money getRecoveredCost() {
		return recoveredCost;
	}
	public void setRecoveredCost(Money recoveredCost) {
		this.recoveredCost = recoveredCost;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public BigDecimal getLaborCurrYear() {
		return this.laborCurrYear == null ? BigDecimal.ZERO : laborCurrYear.setScale(2);
	}
	public void setLaborCurrYear(BigDecimal laborCurrYear) {
		this.laborCurrYear = laborCurrYear;
	}
	public BigDecimal getLaborLastYear() {
		return this.laborLastYear == null ? BigDecimal.ZERO : laborLastYear.setScale(2);
	}
	public void setLaborLastYear(BigDecimal laborLastYear) {
		this.laborLastYear = laborLastYear;
	}
	public BigDecimal getNonOemPartCurrYear() {
		return this.nonOemPartCurrYear == null ? BigDecimal.ZERO : nonOemPartCurrYear.setScale(2);
	}
	public void setNonOemPartCurrYear(BigDecimal nonOemPartCurrYear) {
		this.nonOemPartCurrYear = nonOemPartCurrYear;
	}
	public BigDecimal getNonOemPartLastYear() {
		return this.nonOemPartLastYear == null ? BigDecimal.ZERO : nonOemPartLastYear.setScale(2);
	}
	public void setNonOemPartLastYear(BigDecimal nonOemPartLastYear) {
		this.nonOemPartLastYear = nonOemPartLastYear;
	}
	public BigDecimal getOemPartCurrYear() {
		return this.oemPartCurrYear == null ? BigDecimal.ZERO : oemPartCurrYear.setScale(2);
	}
	public void setOemPartCurrYear(BigDecimal oemPartCurrYear) {
		this.oemPartCurrYear = oemPartCurrYear;
	}
	public BigDecimal getOemPartLastYear() {
		return this.oemPartLastYear == null ? BigDecimal.ZERO : oemPartLastYear.setScale(2);
	}
	public void setOemPartLastYear(BigDecimal oemPartLastYear) {
		this.oemPartLastYear = oemPartLastYear;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public long getModelCount() {
		return modelCount;
	}
	public void setModelCount(long modelCount) {
		this.modelCount = modelCount;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public BigDecimal getModelSum() {
		return modelSum;
	}
	public void setModelSum(BigDecimal modelSum) {
		this.modelSum = modelSum;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getFaultName() {
		return faultName;
	}
	public void setFaultName(String faultName) {
		this.faultName = faultName;
	}
	public BigDecimal getTaxCurrentYear() {
		return taxCurrentYear;
	}
	public void setTaxCurrentYear(BigDecimal taxCurrentYear) {
		this.taxCurrentYear = taxCurrentYear;
	}
	public BigDecimal getTaxLastYear() {
		return taxLastYear;
	}
	public void setTaxLastYear(BigDecimal taxLastYear) {
		this.taxLastYear = taxLastYear;
	}
	public BigDecimal getTotalAmountCurrentYear() {
		return this.totalAmountCurrentYear == null ? BigDecimal.ZERO : totalAmountCurrentYear.setScale(2);
	}
	public void setTotalAmountCurrentYear(BigDecimal totalAmountCurrentYear) {
		this.totalAmountCurrentYear = totalAmountCurrentYear;
	}
	public BigDecimal getTotalAmountLastYear() {
		return this.totalAmountLastYear == null ? BigDecimal.ZERO : totalAmountLastYear.setScale(2);
	}
	public void setTotalAmountLastYear(BigDecimal totalAmountLastYear) {
		this.totalAmountLastYear = totalAmountLastYear;
	}
	public BigDecimal getAddTravelCurrYear() {
		return this.addTravelCurrYear == null ? BigDecimal.ZERO : addTravelCurrYear.setScale(2);
	}
	public void setAddTravelCurrYear(BigDecimal addTravelCurrYear) {
		this.addTravelCurrYear = addTravelCurrYear;
	}
	public BigDecimal getAddTravelLastYear() {
		return this.addTravelLastYear == null ? BigDecimal.ZERO : addTravelLastYear.setScale(2);
	}
	public void setAddTravelLastYear(BigDecimal addTravelLastYear) {
		this.addTravelLastYear = addTravelLastYear;
	}
	public BigDecimal getItemFreightDutyCurrYear() {
		return this.itemFreightDutyCurrYear == null ? BigDecimal.ZERO : itemFreightDutyCurrYear.setScale(2);
	}
	public void setItemFreightDutyCurrYear(BigDecimal itemFreightDutyCurrYear) {
		this.itemFreightDutyCurrYear = itemFreightDutyCurrYear;
	}
	public BigDecimal getItemFreightDutyLastYear() {
		return this.itemFreightDutyLastYear == null ? BigDecimal.ZERO : itemFreightDutyLastYear.setScale(2);
	}
	public void setItemFreightDutyLastYear(BigDecimal itemFreightDutyLastYear) {
		this.itemFreightDutyLastYear = itemFreightDutyLastYear;
	}
	public BigDecimal getLocalPurchaseCurrYear() {
		return this.localPurchaseCurrYear == null ? BigDecimal.ZERO : localPurchaseCurrYear.setScale(2);
	}
	public void setLocalPurchaseCurrYear(BigDecimal localPurchaseCurrYear) {
		this.localPurchaseCurrYear = localPurchaseCurrYear;
	}
	public BigDecimal getLocalPurchaseLastYear() {
		return this.localPurchaseLastYear == null ? BigDecimal.ZERO : localPurchaseLastYear.setScale(2);
	}
	public void setLocalPurchaseLastYear(BigDecimal localPurchaseLastYear) {
		this.localPurchaseLastYear = localPurchaseLastYear;
	}
	public BigDecimal getMealsCurrYear() {
		return this.mealsCurrYear == null ? BigDecimal.ZERO : mealsCurrYear.setScale(2);
	}
	public void setMealsCurrYear(BigDecimal mealsCurrYear) {
		this.mealsCurrYear = mealsCurrYear;
	}
	public BigDecimal getMealsLastYear() {
		return this.mealsLastYear == null ? BigDecimal.ZERO : mealsLastYear.setScale(2);
	}
	public void setMealsLastYear(BigDecimal mealsLastYear) {
		this.mealsLastYear = mealsLastYear;
	}
	public BigDecimal getMiscPartsCurrYear() {
		return this.miscPartsCurrYear == null ? BigDecimal.ZERO : miscPartsCurrYear.setScale(2);
	}
	public void setMiscPartsCurrYear(BigDecimal miscPartsCurrYear) {
		this.miscPartsCurrYear = miscPartsCurrYear;
	}
	public BigDecimal getMiscPartsLastYear() {
		return this.miscPartsLastYear == null ? BigDecimal.ZERO : miscPartsLastYear.setScale(2);
	}
	public void setMiscPartsLastYear(BigDecimal miscPartsLastYear) {
		this.miscPartsLastYear = miscPartsLastYear;
	}
	public BigDecimal getOtherFreightCurrYear() {
		return this.otherFreightCurrYear == null ? BigDecimal.ZERO : otherFreightCurrYear.setScale(2);
	}
	public void setOtherFreightCurrYear(BigDecimal otherFreightCurrYear) {
		this.otherFreightCurrYear = otherFreightCurrYear;
	}
	public BigDecimal getOtherFreightLastYear() {
		return this.otherFreightLastYear == null ? BigDecimal.ZERO : otherFreightLastYear.setScale(2);
	}
	public void setOtherFreightLastYear(BigDecimal otherFreightLastYear) {
		this.otherFreightLastYear = otherFreightLastYear;
	}
	public BigDecimal getOthersCurrYear() {
		return this.othersCurrYear == null ? BigDecimal.ZERO : othersCurrYear.setScale(2);
	}
	public void setOthersCurrYear(BigDecimal othersCurrYear) {
		this.othersCurrYear = othersCurrYear;
	}
	public BigDecimal getOthersLastYear() {
		return this.othersLastYear == null ? BigDecimal.ZERO : othersLastYear.setScale(2);
	}
	public void setOthersLastYear(BigDecimal othersLastYear) {
		this.othersLastYear = othersLastYear;
	}
	public BigDecimal getParkingCurrYear() {
		return this.parkingCurrYear == null ? BigDecimal.ZERO : parkingCurrYear.setScale(2);
	}
	public void setParkingCurrYear(BigDecimal parkingCurrYear) {
		this.parkingCurrYear = parkingCurrYear;
	}
	public BigDecimal getParkingLastYear() {
		return this.parkingLastYear == null ? BigDecimal.ZERO : parkingLastYear.setScale(2);
	}
	public void setParkingLastYear(BigDecimal parkingLastYear) {
		this.parkingLastYear = parkingLastYear;
	}
	public BigDecimal getPerDiemCurrYear() {
		return this.perDiemCurrYear == null ? BigDecimal.ZERO : perDiemCurrYear.setScale(2);
	}
	public void setPerDiemCurrYear(BigDecimal perDiemCurrYear) {
		this.perDiemCurrYear = perDiemCurrYear;
	}
	public BigDecimal getPerDiemLastYear() {
		return this.perDiemLastYear == null ? BigDecimal.ZERO : perDiemLastYear.setScale(2);
	}
	public void setPerDiemLastYear(BigDecimal perDiemLastYear) {
		this.perDiemLastYear = perDiemLastYear;
	}
	public BigDecimal getHandlingFeeLastYear() {
		return this.handlingFeeLastYear == null ? BigDecimal.ZERO:handlingFeeLastYear.setScale(2);
	}
	public void setHandlingFeeLastYear(BigDecimal handlingFeeLastYear) {
		this.handlingFeeLastYear = handlingFeeLastYear;
	}
	public BigDecimal getHandlingFeeCurrYear() {
		return this.handlingFeeCurrYear == null ? BigDecimal.ZERO:handlingFeeCurrYear.setScale(2);
	}
	public void setHandlingFeeCurrYear(BigDecimal handlingFeeCurrYear) {
		this.handlingFeeCurrYear = handlingFeeCurrYear;
	}	
	public BigDecimal getTransportationLastYear(){
		return this.transportationLastYear == null ? BigDecimal.ZERO:transportationLastYear.setScale(2);
	}
	public void setTransportationLastYear(BigDecimal transportationLastYear){
		this.transportationLastYear = transportationLastYear;
	}	
	public BigDecimal getTransportationCurrYear(){
		return this.transportationCurrYear == null ? BigDecimal.ZERO:transportationCurrYear.setScale(2);
	}
	public void setTransportationCurrYear(BigDecimal transportationCurrYear){
		this.transportationCurrYear = transportationCurrYear;
	}
	public BigDecimal getRentalCurrYear() {
		return this.rentalCurrYear == null ? BigDecimal.ZERO : rentalCurrYear.setScale(2);
	}
	public void setRentalCurrYear(BigDecimal rentalCurrYear) {
		this.rentalCurrYear = rentalCurrYear;
	}
	public BigDecimal getRentalLastYear() {
		return this.rentalLastYear == null ? BigDecimal.ZERO : rentalLastYear.setScale(2);
	}
	public void setRentalLastYear(BigDecimal rentalLastYear) {
		this.rentalLastYear = rentalLastYear;
	}
	public BigDecimal getTollsCurrYear() {
		return this.tollsCurrYear == null ? BigDecimal.ZERO : tollsCurrYear.setScale(2);
	}
	public void setTollsCurrYear(BigDecimal tollsCurrYear) {
		this.tollsCurrYear = tollsCurrYear;
	}
	public BigDecimal getTollsLastYear() {
		return this.tollsLastYear == null ? BigDecimal.ZERO : tollsLastYear.setScale(2);
	}
	public void setTollsLastYear(BigDecimal tollsLastYear) {
		this.tollsLastYear = tollsLastYear;
	}
	public BigDecimal getTravelByDistCurrYear() {
		return this.travelByDistCurrYear == null ? BigDecimal.ZERO : travelByDistCurrYear.setScale(2);
	}
	public void setTravelByDistCurrYear(BigDecimal travelByDistCurrYear) {
		this.travelByDistCurrYear = travelByDistCurrYear;
	}
	public BigDecimal getTravelByDistLastYear() {
		return this.travelByDistLastYear == null ? BigDecimal.ZERO : travelByDistLastYear.setScale(2);
	}
	public void setTravelByDistLastYear(BigDecimal travelByDistLastYear) {
		this.travelByDistLastYear = travelByDistLastYear;
	}
	public BigDecimal getTravelByHrsCurrYear() {
		return this.travelByHrsCurrYear == null ? BigDecimal.ZERO : travelByHrsCurrYear.setScale(2);
	}
	public void setTravelByHrsCurrYear(BigDecimal travelByHrsCurrYear) {
		this.travelByHrsCurrYear = travelByHrsCurrYear;
	}
	public BigDecimal getTravelByHrsLastYear() {
		return this.travelByHrsLastYear == null ? BigDecimal.ZERO : travelByHrsLastYear.setScale(2);
	}
	public void setTravelByHrsLastYear(BigDecimal travelByHrsLastYear) {
		this.travelByHrsLastYear = travelByHrsLastYear;
	}
	public BigDecimal getTravelByTripCurrYear() {
		return this.travelByTripCurrYear == null ? BigDecimal.ZERO : travelByTripCurrYear.setScale(2);
	}
	public void setTravelByTripCurrYear(BigDecimal travelByTripCurrYear) {
		this.travelByTripCurrYear = travelByTripCurrYear;
	}
	public BigDecimal getTravelByTripLastYear() {
		return this.travelByTripLastYear == null ? BigDecimal.ZERO : travelByTripLastYear.setScale(2);
	}
	public void setTravelByTripLastYear(BigDecimal travelByTripLastYear) {
		this.travelByTripLastYear = travelByTripLastYear;
	}
	
}
