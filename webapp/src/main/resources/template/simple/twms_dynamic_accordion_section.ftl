<script type="text/javascript">
<#if parameters.tagWasUsedBefore == false>
 dojo.declare("tavant.twms.taglib.DASection", null, {
  _ol : null,<#--domNode-->
  _url : "",<#--String-->
  _appendMode : false,<#--boolean-->
  _ajaxer : null,<#--AJAXer(utility.js)-->
  constructor : function(<#--ol domNode--> ol, <#--String url--> url, <#--event topic to fetch on--> topic, <#--boolean--> appendMode) {
   this._ol = ol;
   this._url = url;
   this._appendMode = appendMode;
   dojo.subscribe(topic, this, "_fetchMarkup");
   this._fetchMarkup();
  },
  _fetchMarkup : function() {
   twms.ajax.fireJsonRequest(this._url, {}, dojo.hitch(this, this.setContent));
  },
  setContent : function(<#--JSON data having format {markup : "markup", script : "script"}--> data) {
   this._setNewContent(data.markup, data.script);
  },
  _setNewContent : function(<#--String (markup)--> markup, <#--String (script)--> script) {
   var nodes = dojo.toDom(markup);
   if(!this._appendMode) {
    this._purgeAndRecreateOl();
   }
   if(typeof nodes === 'object'){
        this._ol.appendChild(nodes);
   }else if (dojo.isArray(nodes)){
        for(var i in nodes) {
            this._ol.appendChild(nodes[i]);
        }
   }
   eval(script);
  },
  _purgeAndRecreateOl : function() {
   var newOl = document.createElement("ol");
   dojo.dom.replaceNode(this._ol, newOl);
   dojo.dom.destroyNode(this._ol);
   this._ol = newOl;
  }
 });
</#if>
dojo.addOnLoad(function() {
 new tavant.twms.taglib.DASection(dojo.byId("${parameters.id}"),
  "${parameters.fetchFrom}", "${parameters.fetchOn}", <#if parameters.appendMode == true>true<#else>false</#if>);
});
</script>
<h4 <#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}" <#rt/>
</#if>>
<@s.text name="${parameters.title}"/>
</h4>
<ol id="${parameters.id}">
</ol>