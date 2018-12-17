package tavant.twms.external;

import java.util.ArrayList;
import java.util.List;

public class PriceCheckResponse {
	
	private String statusCode;
	
	private String errorMessage;
	
	private List<PriceCheckItem> priceCheckItemList = new ArrayList<PriceCheckItem>();
	
	private List<PriceCheckItem> costCheckItemList = new ArrayList<PriceCheckItem>();
	
	private boolean isGlobalPriceCheckResponse = false;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<PriceCheckItem> getPriceCheckItemList() {
		return priceCheckItemList;
	}

	public void setPriceCheckItemList(List<PriceCheckItem> priceCheckItemList) {
		this.priceCheckItemList = priceCheckItemList;
	}

	public List<PriceCheckItem> getCostCheckItemList() {
		return costCheckItemList;
	}

	public void setCostCheckItemList(List<PriceCheckItem> costCheckItemList) {
		this.costCheckItemList = costCheckItemList;
	}

	public boolean isGlobalPriceCheckResponse() {
		return isGlobalPriceCheckResponse;
	}

	public void setGlobalPriceCheckResponse(boolean isGlobalPriceCheckResponse) {
		this.isGlobalPriceCheckResponse = isGlobalPriceCheckResponse;
	}
	
}
