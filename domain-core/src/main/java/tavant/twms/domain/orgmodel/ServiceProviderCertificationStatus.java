package tavant.twms.domain.orgmodel;
public enum ServiceProviderCertificationStatus {
	CERTIFIED("CERTIFIED"), NOTCERTIFIED("NOTCERTIFIED"), ANY("ANY");
	
     private String type;
	
     private ServiceProviderCertificationStatus(String type) {
         this.type = type;
     }

     public String getType() {
	        return this.type;
	    }
   
     @Override
     public String toString() {
         return this.type;
     }
	
}
