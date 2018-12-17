<script type="text/javascript">
dojo.addOnLoad(function(){
	var submit1 = dojo.byId("submitButton");
	var submit2 = dojo.byId("submitButton2");
	var submit3 = dojo.byId("submitButton3");
	var submit4 = dojo.byId("submitButton4");

	if(submit1) {
		dojo.connect(submit1, "onclick", function() {
			var form = document.getElementById("claim_form");
			if(dijit.byId("validations")){
               dojo.style(dijit.byId("validations").closeButtonNode,"display","none");
            }
			form.submit();
		});
	}else if(submit3) {
		dojo.connect(submit3, "onclick", function() {
			var form = document.getElementById("claim_form");
			form.action="campaign_claim_submit.action";
			if(dijit.byId("validations")){
                           dojo.style(dijit.byId("validations").closeButtonNode,"display","none");
                        }
			form.submit();
		});
	}else if(submit4) {
		dojo.connect(submit4, "onclick", function() {
			var form = document.getElementById("claim_form");
			form.action="claim_reopen_submit.action";
			if(dijit.byId("validations")){
                           dojo.style(dijit.byId("validations").closeButtonNode,"display","none");
                        }
			form.submit();
		});	
	} else {
		dojo.connect(submit2, "onclick", function() {
			var form = document.getElementById("claim_form");
			if(dijit.byId("validations")){
                           dojo.style(dijit.byId("validations").closeButtonNode,"display","none");
                        }
			form.action="parts_claim_submit.action";
      form.submit();
		});
	}
});
</script>