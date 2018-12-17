package tavant.twms.web.partsreturn;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tavant.twms.domain.claim.Claim;

public class PrintShipmentVO implements Comparable{
	
	private Claim claim;
	
    private Collection<PartReturnVO> partReturnVOList;
    
    
	public Collection<PartReturnVO> getPartReturnVOList() {
		return partReturnVOList;
	}

	public void setPartReturnVOList(Collection<PartReturnVO> partReturnVOList) {
		this.partReturnVOList = partReturnVOList;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return this.claim.getClaimNumber().compareTo(((PrintShipmentVO)arg0).getClaim().getClaimNumber());
	}

    /*public String getRmaNumber(){
        return claim.getActiveClaimAudit().getServiceInformation().getServiceDetail().getReplacedParts().get(0).getPartReturnConfiguration().getRmaNumber();
    }*/

    public String getReceiverInstructions(){
        return claim.getActiveClaimAudit().getServiceInformation().getServiceDetail().getReplacedParts().get(0).getPartReturnConfiguration().getPartReturnDefinition().getReceiverInstructions();
    }
}
