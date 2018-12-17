package tavant.twms.domain.thirdparty;

import tavant.twms.infra.ListCriteria;

/**
 * This is the class which will help our beloved team in location a list of third parties from database
 * based on a search criteria; specified in this file. This class will be declared and used in a action
 * class which will make the optimal use of this class.
 * 
 * @author priyank.gupta
 *
 */
public class ThirdPartySearch extends ListCriteria 
{
	
	/**
	 * Variable to store the third party name passed from JSP.
	 */
	private String thirdPartyName;
	
	/**
	 * Variable to store the third party number passed from JSP.
	 */
	private String thirdPartyNumber;

	/**
	 * Getter for Third party name
	 * 
	 * @return
	 */
	public String getThirdPartyName() 
	{
		return thirdPartyName;
	}

	/**
	 * Setter for Third party name
	 * 
	 * @param thirdPartyName
	 */
	public void setThirdPartyName(String thirdPartyName) 
	{
		this.thirdPartyName = thirdPartyName;
	}

	/**
	 * Getter for Third party number
	 * 
	 * @return
	 */
	public String getThirdPartyNumber() 
	{
		return thirdPartyNumber;
	}

	/**
	 * Setter for Third party number
	 * 
	 * @param thirdPartyNumber
	 */
	public void setThirdPartyNumber(String thirdPartyNumber) 
	{
		this.thirdPartyNumber = thirdPartyNumber;
	}
}
