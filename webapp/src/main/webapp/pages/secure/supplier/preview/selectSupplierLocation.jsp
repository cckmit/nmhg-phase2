 <script type="text/javascript">
                      var totalCount = '<s:property value="%{#partsCounter}"/>';
                      var displayText = '<s:text name="label.supplier.display.shipping.location" />';
                      var hideText = '<s:text name="label.supplier.hide.shipping.location" />';
                       dojo.addOnLoad(function() {
                              var index_count='<s:property value="%{#taskInstanceStatus.index}"/>';
                              dojo.subscribe("/supplier/returnLocation/changed/"+index_count, function(data){
                                //  dojo.byId("shippingLocation").value = document.getElementsByName("supplierPartReturn.returnLocation")[0].value;
                                 for(var i=0;i<totalCount;i++){
                                      dojo.byId("displayAddress_"+i).value = displayText;
                                      dojo.byId("displayAddress_"+i).innerHTML = displayText;
                                 }

                                 // displayAddressDetails(index_count);
                                  dojo.byId("showaddresshere").innerHTML = "";
                                 // dojo.byId("displayAddress_"+index_count).value = hideText;
                                 // dojo.byId("displayAddress_"+index_count).innerHTML = hideText;
                              });
                      });

                      function displayAddressDetails(index){
                         if(dojo.byId("displayAddress_"+index).value == (hideText)){
                              dojo.byId("showaddresshere").innerHTML = "";
                              dojo.byId("displayAddress_"+index).value = displayText;
                              dojo.byId("displayAddress_"+index).innerHTML = displayText;
                         }else{
                             var loc = document.getElementsByName("supplierPartReturnBeans["+index+"].returnLocation")[0].value;
                             twms.ajax.fireHtmlRequest("displayShippingAddress.action", {location:loc}, function(data) {
                                        dojo.byId("showaddresshere").innerHTML = data;
                                        dojo.byId("displayAddress_"+index).innerHTML = hideText;
                                        dojo.byId("displayAddress_"+index).value = hideText;
                                         for(var i=0;i<totalCount;i++){
                                             if(i != index){
                                                dojo.byId("displayAddress_"+i).value = displayText;
                                                dojo.byId("displayAddress_"+i).innerHTML = displayText;
                                             }
                                         }
                             });
                         }
                      }

                  </script>
                     <sd:autocompleter name='supplierPartReturnBeans[%{#taskInstanceStatus.index}].returnLocation' keyName='supplierPartReturnBeans[%{#taskInstanceStatus.index}].returnLocation' href='list_shipmentLocations.action'
                     loadMinimumCount='0' keyValue='%{#supplierPartReturn.returnLocation.id}' value='%{#supplierPartReturn.returnLocation.code}' listenTopics='/supplier/returnLocation/queryAddParams/%{#taskInstanceStatus.index}'
                     				  		notifyTopics="/supplier/returnLocation/changed/%{#taskInstanceStatus.index}"/>
                     <a id="displayAddress_<s:property value='%{#taskInstanceStatus.index}'/>" onclick="displayAddressDetails('<s:property value='%{#taskInstanceStatus.index}'/>')" value="display"><s:text name="label.supplier.display.shipping.location" /></a>
                     <%--<s:hidden name="supplierPartReturnBeans[%{#taskInstanceStatus.index}].returnLocation" id="shippingLocation" /> --%>
                     <script type="text/javascript">
                                 dojo.addOnLoad(function() {
                                     var isOnLoad=true;
                                     var inIndex='<s:property value="%{#taskInstanceStatus.index}"/>';
                                     var url= "list_supplierLocations.action";
                                     var supplierId = '<s:property value="#recClaim.contract.supplier.id"/>';
                                     dojo.publish("/supplier/returnLocation/queryAddParams/"+inIndex, [{
                                                 url: url,
                                                 params: {
                                                     "supplier": supplierId
                                                 },
                                                 makeLocal: true
                                             }]);

                                 });


                     </script>