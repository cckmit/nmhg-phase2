<#if (parameters.autoSuggestWasUsedBefore?exists && (parameters.autoSuggestWasUsedBefore == false))>
 dojo.declare("twms.tavant.AutoSuggestTagDataProvider", null, {
  constructor : function (options) {
   this.searchUrl = options.dataUrl;
   this._cache = {};
   this._inFlight = false;
   this._lastRequest = null;
  },

  _addToCache : function(/*String*/ keyword, /*Array*/ data){
   if(this.allowCache){
    this._cache[keyword] = data;
   }
  },

  startSearch : function(/*String*/ searchStr, /*Function*/ callback){
   if(this._inFlight){
    return;
   }
   var tss = encodeURIComponent(searchStr);
   var realUrl = dojo.string.substitute(this.searchUrl, {"searchString": tss});
   realUrl = this.buildUrl(realUrl, this.associatedFields);//late bound
   var _this = this;
   var request = this._lastRequest = dojo.io.bind({
    url: realUrl,
    method: "get",
    mimetype: "text/json",
    load: function(data, evt){
     _this._inFlight = false;
     if(!dojo.lang.isArray(data)){
      var arrData = [];
      for(var key in data){
       arrData.push([data[key], key]);
      }
      data = arrData;
     }
     _this._addToCache(searchStr, data);
     if (request == _this._lastRequest){
      callback(data);
     }
    }
   });
  this._inFlight = true;
  }
 });
</#if>
 dojo.addOnLoad(function() {
  var ${parameters.id?html} = dijit.byId("${parameters.id?html}");
  var input_${parameters.id?html} = dojo.query("input", ${parameters.id?html}.domNode)[0];
<#if (tagType == "text")><#-- else it is assumed to be select-->
  var img_${parameters.id?html} = dojo.query("img", ${parameters.id?html}.domNode)[0];
  dojo.dom.destroyNode(img_${parameters.id?html});
  dojo.removeClass(input_${parameters.id?html}, "dojoComboBox");
  delete img_${parameters.id?html};
</#if>
  ${parameters.id?html}.dataProvider.associatedFields = new Array();//late binding...
 <@s.iterator value="parameters.associatedFieldIds" id="fieldId">
  ${parameters.id?html}.dataProvider.associatedFields.push(dojo.byId("${fieldId}"));
 </@s.iterator>
  ${parameters.id?html}.dataProvider.buildUrl = function(url, assocFields) {//late binding...
   for(var i = 0; i < assocFields.length; i++) {
    var value = encodeURIComponent(assocFields[i].value);
    var name = assocFields[i].name;
    url += ("&" + name + "=" + value);
    delete value, name;
   }
   return url;
  };
 });