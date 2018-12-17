<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
 <head>
      <title><s:text name="title.common.responsePage"/></title>
      <s:head theme="twms"/> 
     
      <u:stylePicker fileName="common.css"/>  
      
      <script type="text/javascript">
         dojo.require("dijit.layout.ContentPane");
         dojo.require("dijit.layout.LayoutContainer"); 
      </script>
  </head> 
<u:body>
   <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">
   	  <u:actionResults wipeMessages="false"/>
      <script type="text/javascript">
          dojo.addOnLoad(function() {
              var summaryTableId = getFrameAttribute("TST_ID");
              if (summaryTableId) {
                  manageTableRefresh(summaryTableId, true);
              }
          });
      </script>
      <div id="submit" align="center">
            <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
                   onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
        </div>
	</div>
</u:body>	
</html>