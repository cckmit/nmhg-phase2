package tavant.twms.web.uom;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.uom.I18nUomMappingValues;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.domain.uom.UomMappingsService;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ValidationAware;

@SuppressWarnings("serial")
public class ManageUom  extends I18nActionSupport implements Preparable,ValidationAware{

	private Long id;
	private UomMappings uomMappings;
	private UomMappingsService uomMappingsService;
	private List<UomMappings> mappings = new ArrayList<UomMappings>();
	private List<ItemUOMTypes> unMappedUoms;
	private ProductLocaleService productLocaleService;
	private List<ProductLocale> locales;
	private PartReplacedService partReplacedService;
	

	public String showPreview(){
		this.uomMappings = uomMappingsService.findById(this.id);
		return SUCCESS;
	}
	
	public String showDetail(){
		showPreview();		
		return SUCCESS;
	}

	public String updateUom() throws Exception{				
		uomMappingsService.update(this.uomMappings);
		addActionMessage("uom.manageUom.update.success");
		return SUCCESS;
	}
	
	public String deleteUom() throws Exception{		
		try
		{
			partReplacedService.updateUOMMappingsOfOEMReplacedParts(this.uomMappings);
			uomMappingsService.delete(this.uomMappings);
		}
		catch(Exception e)
		{
			addActionError("uom.manageUom.delete.error");
			return INPUT;
		}
		addActionMessage("uom.manageUom.delete.success");
		return SUCCESS;
	}
	
	public String addUom() throws Exception{
		return SUCCESS;
	}
	
	public String saveUom() throws Exception{		
		if(hasActionErrors()){			
			return INPUT;
		}		
		mappings.remove(Collections.singleton(null));		 		 		
		for (UomMappings element : mappings) {
			/**
			 * Populating i18n UOM Mappings to default english at the time of creation
			 */
			I18nUomMappingValues i18nedUOMMappingValues = new I18nUomMappingValues();
			i18nedUOMMappingValues.setLocale("en_US");
			i18nedUOMMappingValues.setMappedUom(element.getMappedUom());			
			//element.setBaseUom(ItemUOMTypes.valueOf(StringUtils.stripToEmpty(element.getBaseUom().getType().toUpperCase())));
			element.getI18nUomMappings().add(i18nedUOMMappingValues);
		}	
		
		this.uomMappingsService.saveAll(mappings);
		addActionMessage("uom.manageUom.add.success");
		return SUCCESS;
	}
	
	@Override
	public void validate() {	
		if(this.uomMappings == null)
		{
			//UOM Creation validation
			if(mappings == null || mappings.size() ==0){
				addActionError("uom.addUom.noInput");
			    return;	
			}
					
			List<String> baseUomsList = new ArrayList<String>() ;		
			
			for (UomMappings element : mappings) {			
				 if(baseUomsList.contains(element.getBaseUom().getType())){ 
					 addActionError("uom.addUom.baseUom.multipleMapping");
					 break;
				 } else{
					 baseUomsList.add(element.getBaseUom().getType()); 
				 }			  			  
			}	
		}
		else
		{
				//UOM updation validation
			for (I18nUomMappingValues element : uomMappings.getI18nUomMappings()) {			
				 if(element.getLocale().equals("en_US") && 
						 !org.springframework.util.StringUtils.hasText(element.getMappedUom())){ 
					 addActionError("error.i18nuom.mappedUOM");
					 break;
				 } 
				 if(element.getLocale().equals("en_US"))
				 {
					 this.uomMappings.setMappedUom(element.getMappedUom());
				 }
			}	
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public UomMappings getUomMappings() {
		return uomMappings;
	}

	public void setUomMappings(UomMappings uomMappings) {
		this.uomMappings = uomMappings;
	}

	public void setUomMappingsService(UomMappingsService uomMappingsService) {
		this.uomMappingsService = uomMappingsService;
	}

	public List<UomMappings> getMappings() {
		return mappings;
	}

	public void setMappings(List<UomMappings> mappings) {
		this.mappings = mappings;
	}	

	public List<ItemUOMTypes> getUnMappedUoms() {
		return unMappedUoms;
	}

	public void setUnMappedUoms(List<ItemUOMTypes> unMappedUoms) {
		this.unMappedUoms = unMappedUoms;
	}

	public void setProductLocaleService(ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}

	public List<ProductLocale> getLocales() {
		return locales;
	}

	public void setLocales(List<ProductLocale> locales) {
		this.locales = locales;
	}

	public void prepare() throws Exception {
		this.unMappedUoms = this.uomMappingsService.findUnMappedUoms();	
		this.locales = productLocaleService.findAll();
	}

	public void setPartReplacedService(PartReplacedService partReplacedService) {
		this.partReplacedService = partReplacedService;
	}	
}
