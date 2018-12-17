<script type="text/javascript">
 dojo.addOnLoad(function() {
			var submit1 = dojo.byId("submitButton");
			var submit2 = dojo.byId("submitButton2");
			var submit3 = dojo.byId("submitButton3");
 
 			if(submit1) {
 				dojo.connect(dojo.byId("submitButton"), "onclick", function() {
	         var form = document.getElementById("claim_form");//document.forms[0]; // dojo.byId() does not work here somehow
	         var transition = document.createElement("input");
	         transition.value = "Submit";
	         transition.name = "task.takenTransition";
	         form.appendChild(transition);
	         if(dijit.byId("validations")){
                                        dojo.style(dijit.byId("validations").closeButtonNode,"display","none");
                                     }
	         form.submit();
	     });
 			} else if(submit2){
 				dojo.connect(dojo.byId("submitButton2"), "onclick", function() {
	         var form = document.getElementById("claim_form");//document.forms[0]; // dojo.byId() does not work here somehow
	         var transition = document.createElement("input");
	         transition.value = "Submit";
	         transition.name = "task.takenTransition";
	         form.appendChild(transition);
	         form.action="parts_claim_submit.action";
	         if(dijit.byId("validations")){
                                        dojo.style(dijit.byId("validations").closeButtonNode,"display","none");
                                     }
	         form.submit();
	     });
 			}else {
 				dojo.connect(dojo.byId("submitButton3"), "onclick", function() {
	         var form = document.getElementById("claim_form");//document.forms[0]; // dojo.byId() does not work here somehow
	         var transition = document.createElement("input");
	         transition.value = "Submit";
	         transition.name = "task.takenTransition";
	         form.appendChild(transition);
	         form.action="campaign_claim_submit.action";
	         if(dijit.byId("validations")){
                                        dojo.style(dijit.byId("validations").closeButtonNode,"display","none");
                                     }
	         form.submit();
	     });
 			}
     
 });
 </script>