${response.setHeader( "Pragma", "no-cache" )}
${response.addHeader( "Cache-Control", "must-revalidate" )}
${response.addHeader( "Cache-Control", "no-cache" )}
${response.addHeader( "Cache-Control", "no-store" )}
${response.setDateHeader("Expires", 0)}

<script language="JavaScript" type="text/javascript"> 
     var dojoConfig = { 
         baseScriptUri: "<@s.url includeParams='none' value='/scripts/vendor/dojo-widget/' includeParams="none" encode='false'/>", 
         baseRelativePath: "<@s.url includeParams='none' value='/scripts/vendor/dojo-widget/' includeParams="none" encode='false'/>", 
         isDebug: ${parameters.debug?default(false)?string},
         bindEncoding: "${parameters.encoding}", 
         debugAtAllCosts: ${parameters.debug?default(false)?string}, 
         parseOnLoad: true, 
         usePlainJson: true, 
         modulePaths: {"twms":"../twms"}, 
         googleAnalyticsCode:"<@s.property value='applicationSettings.googleAnalyticsCode'/>",
         googleAnalyticsEnabled:"<@s.property value='applicationSettings.googleAnalyticsEnabled'/>",
         googleMapsAPIKey: "<@s.property value='googleMapsSettings.apiKey'/>",
         loggedInUserId: "<@s.property value='securityHelper.loggedInUser.name'/>"
      }; 
      var pageLoadStartTime = (new Date()).getTime();
  </script> 
  <script language="JavaScript" type="text/javascript" 
          src="<@s.url includeParams='none' value='/scripts/vendor/dojo-widget/dojo/dojo.js' includeParams="none" encode='false'/>"> 
  </script> 
  <script language="JavaScript" type="text/javascript" 
          src="<@s.url includeParams='none' value='/scripts/vendor/dojo-widget/dojo/twms-dojo.js' includeParams="none" encode='false'/>"> 
  </script> 
  <script language="JavaScript" type="text/javascript" 
          src="<@s.url includeParams='none' value='/scripts/ui-ext/common/utility.js' includeParams="none" encode='false'/>"> 
  </script>
  
  <@s.if test="applicationSettings.googleAnalyticsEnabled == 'true'">
  	<script language="JavaScript"  type='text/javascript'
  		src="<@s.url includeParams='none' value='/scripts/common/analytics.js' includeParams="none" encode='false'/>">
  	</script>
  </@s.if>
  <script language="JavaScript" type="text/javascript"> 
      dojo.require("dojo.parser"); 
      dojo.require("twms.widget.LoadingLid"); 
      <@s.if test="allowSessionTimeOut" >
      dojo.addOnLoad(function() { 
          twms.util.updateSessionAccessTime(); 
      }); 
      </@s.if>

      <@s.if test="pageReadOnly">
        dojo.addOnLoad(function() {
            twms.util.makePageReadOnly("dishonourReadOnly");
        });
      </@s.if>
  </script>
  <style type="text/css"> 
      @import "scripts/vendor/dojo-widget/dojo/resources/dojo.css"; 
      @import "css/theme/claro/claro.css"; 
  </style>
