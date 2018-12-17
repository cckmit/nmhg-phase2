<script type="text/javascript">
<#if (parameters.tagWasUsedBefore == false)>
dojo.declare("tavant.twms.EventManager", null, {
 _recived : null,
 _listen : null,
 _publish : null,
 constructor : function(listen, publish) {
  this._recived = new Array();
  this._listen = listen;
  this._publish = publish;
 },
 markRecived : function(topic) {
  for(var i = 0; i < this._recived.length; i++) {
   if(this._recived[i] == topic) {
    this._requestCheck();
    return;
   }
  }
  this._recived.push(topic);
  this._requestCheck();
 },
 resetRecived : function() {
  this._recied = null;//just in case.... to prevent all possible memory leaks...
  delete this._recived;
  this._recived = new Array();
 },
 _requestCheck : function() {
  if(this._recived.length == this._listen.length) {
   this._publishAll();
  }
 },
 _publishAll : function() {
  for(var i = 0; i < this._publish.length; i++) {
   dojo.publish(this._publish[i], [{triggeredBy : this._listen}]);
  }
 }
});
</#if>
 var ${parameters.id?html}_eventManager = null;
 dojo.addOnLoad(function() {
  var listen = new Array();
  var publish = new Array();
  <@s.iterator value="parameters.publish" id="topicPublish">
   publish.push("${topicPublish}");
  </@s.iterator>
  <@s.iterator value="parameters.listen" id="topicListen">
   listen.push("${topicListen}");
   dojo.subscribe("${topicListen}", this, function(evt) {
    ${parameters.id?html}_eventManager.markRecived("${topicListen}");<#--this is optimization, this won't throw null pointer exception-->
   });
  </@s.iterator>
  ${parameters.id?html}_eventManager = new tavant.twms.EventManager(listen, publish);
  <@s.iterator value="parameters.resetOn" id="topicReset">
   dojo.subscribe("${topicReset}", this, function() {
    ${parameters.id?html}_eventManager.resetRecived();
   });
  </@s.iterator>
 });
</script>