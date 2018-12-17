<script type="text/javascript">
<#if (parameters.tagWasUsedBefore == false)>
 function fetchMarkup_connect(on, urlBuilderFn,paramBuilderFn, div, onchangeFn, publishOnChangeTopic, yftColor, yftDuration) {
  dojo.subscribe(on, this, function() {
   var currentUrl = urlBuilderFn();
   var params=paramBuilderFn();
   twms.ajax.fireHtmlRequest(currentUrl,params,function(data, event) {
     div.innerHTML = data;
     <#-- Stuff to make dojo widgets out of markup fetched using AJAX -->
     var parser = new dojo.xml.Parse();
     var frag  = parser.parseElement(div, null, true);
     dojo.parser.parse(frag);
     <#-- done -->
     <#-- to execute javascript that comes with the new markup -->
     var scriptElements = div.getElementsByTagName("script");
     var scriptContent = "";
     for(var i = 0; i < scriptElements.length; i++) {
      scriptContent += scriptElements[i].innerHTML;
     }
     if((scriptContent != null) && (dojo.string.trim(new String(scriptContent)).length > 0)) {
      eval(scriptContent);
     } 
     <#-- done-->
     if(onchangeFn != null) {
      event.div = div;
      onchangeFn(event);
     }
     if(publishOnChangeTopic != null) {
      event.div = div;
      event.id = "${parameters.id?html}"; 
      dojo.publish(publishOnChangeTopic, [event]);
     }
     if (yftColor != null) {
     	dojo.fx.html.highlight(div, dojo.gfx.color.hex2rgb(yftColor), yftDuration).play();
     }     
    });

  });
 }
</#if>
<#--this(delay)just makes sure that, all the id's that have to set mannualy(using javascript) for dojo widget's inputs... is done...-->
 dojo.addOnLoad(function() {dojo.lang.setTimeout(${parameters.id?html}_bindFieldDependencies,100)});
 function ${parameters.id?html}_bindFieldDependencies() {
  var ${parameters.id?html}_fetchMarkup_basedOnFields = new Array();
 <@s.iterator value="parameters.basedOnIds" id="fieldId">
  ${parameters.id?html}_fetchMarkup_basedOnFields.push(dojo.byId("${fieldId}"));
 </@s.iterator>
  var buildUrl = function() {
   return "${parameters.url}";
  };
  var buildParams=function() {
    var params={};
    for(var i = 0; i < ${parameters.id?html}_fetchMarkup_basedOnFields.length; i++) {
        var value = encodeURIComponent(${parameters.id?html}_fetchMarkup_basedOnFields[i].value);
        var name = ${parameters.id?html}_fetchMarkup_basedOnFields[i].name;
        params[name]=value;
   }
   return params;
  };
  fetchMarkup_connect("${parameters.on}", buildUrl,buildParams,<#rt/>,
   dojo.byId("${parameters.id?html}")<#rt/>
   <#if (parameters.onchange?exists || parameters.publishOnChange?exists)>
   <#if parameters.onchange?exists> 
    , ${parameters.onchange}<#rt/>
   <#else>
    , null<#rt/>
   </#if>
   <#if parameters.publishOnChange?exists> 
    , "${parameters.publishOnChange}"<#rt/>
   <#else>
    , null<#rt/>
   </#if>
   <#else>
    , null, null<#rt/>
   </#if>   
   <#if parameters.yftColor?exists>
    , "${parameters.yftColor}", "${parameters.yftDuration}"
   <#else>
    , null, null<#rt/> 
   </#if>
    );
 }
</script>
<<#rt/>
<#if parameters.tagType?exists>
${parameters.tagType?html}<#rt/>
<#else>
div<#rt/>
</#if>
 id="${parameters.id?html}"<#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
>