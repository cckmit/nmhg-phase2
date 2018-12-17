<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="warranty_coverages" class="section_div">
	<div id="warranty_coverages_title" class="section_heading"><s:text name="warranty.transfer.coverage"/></div>
	<div id="policy_list_div">   
		<jsp:include flush="true" page="warranty_transfer_policy_list.jsp" />
	</div>	
</div>