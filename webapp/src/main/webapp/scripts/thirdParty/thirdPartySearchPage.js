dojo.addOnLoad(function (){
        if(dojo.byId("thirdPartySearchForm")){
            new dojo.io.FormBind({
                formNode: dojo.byId("thirdPartySearchForm"),
                load: function(data, e) {
                    var valFailed = (data == "<true>");
                    //dojo.html.setDisplay(dojo.byId("noSearchParamsErrorSection"), valFailed);        	        
                    var dataDisplayed = (valFailed) ? "" : data;        	
                    dijit.byId('thirdPartyResultDiv').setContent(dataDisplayed);								            
                },
                error : function(error) {
                    dojo.body().style.cursor = "auto";
                }
            });       
        }
});