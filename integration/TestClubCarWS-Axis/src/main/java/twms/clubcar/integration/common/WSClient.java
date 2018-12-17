package twms.clubcar.integration.common;

public class WSClient {
	
	protected  String username = "Tavantuser";
	//protected  String password = "Tavant_1";
	protected  String password = "Tavant$123";
	protected  String basePath = "D:\\Warranty\\clubcar\\CLUB_CAR_DOJO_MIG\\integration\\TestClubCarWS-Axis\\src\\main\\resources\\";
	
	protected String hostname = null;
	protected String port = null;
	protected boolean readFromFile = true;
	protected String fileName = null;
	protected String syncType = null;
	protected String url = null;
	protected String urlFragment=null;
	
	protected String fullFilePath = null;
	
	protected String xml = null;
	
	
	protected void service(){
		url = "http://"+hostname+":"+port+urlFragment;
		fullFilePath = basePath + syncType + "\\"+ fileName;
		xml = ReadWriteTextFile.getContents(fullFilePath);
		System.out.println("*************************************");
		System.out.println("The URL for the Service : ");
		System.out.println(url);
/*		System.out.println("*************************************");
		System.out.println("Input XML");
		System.out.println(xml);*/
		System.out.println("*************************************");

		
	}

}
