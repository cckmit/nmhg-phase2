package tavant.twms.integration.layer;

public interface DealerIntegrationService {
    //public abstract Object getUnitServiceHistory(String inputXml);
    public abstract String submitClaim(String inputXML);

    
    /**
     * Added for UnitWarrantyRegistration
     * 
     * @param soapInputXML
     * @return String
     */
   // public abstract String registerUnitWarranty(String soapInputXML);


   // public abstract String registerMajorComponent(String inputXML);
}