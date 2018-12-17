/*
  User: jishnu.viswanath
  Date: Feb 22, 2008
*/
/*The function used to auto complete state field, ie when Countries is selected states list
 *is populated. In idle case it expects index.
 */
var currentAddressId = null;
var index = null;


function intilizeAddress(/*String*/ index){

   //On change of country fetch others
    dojo.connect(dijit.byId("country_"+index),"onChange",function(value) {

                                      getStatesByCountry(value, dijit.byId("validatable_state_"+index),
                                                                false,
                                                                index,
                                                                [
                                                                    "free_text_state_" + index
                                                                ],
                                                                [
                                                                ]);

                                            });
}

/*
* Ok this is another function, the save button and save and close :P
* So this one accepts lot of boolean check and depends on it actually does other thingy :)
* function self explanatory
*
 */

function initilizeAddressSubmit(/*String*/ item, /*boolean*/ matchRead,
                            /*boolean*/ newCustomer, /*Boolean*/ hideSelect, /*Number*/index){
    var pushInfoPressed = dojo.byId('pushInfoPressed');
        pushInfoPressed.value="false";    

    if(hideSelect){
        dojo.connect(dojo.byId("saveAndSelectButton_"+index), "onclick", function() {
                pushInfoPressed.value="true";
            if(validateAddress())
                submitForm(true, item, matchRead, newCustomer);
        });
    }

    dojo.connect(dojo.byId("saveButton_"+index), "onclick", function() {
        if(validateAddress())
            submitForm(false, item, matchRead, newCustomer);
    });
}

function submitForm(/*boolean*/ saveAndSelect,/*String*/ item, /*boolean*/ matchRead,
                            /*boolean*/ newCustomer){

            if(saveAndSelect && matchRead){
                dojo.byId('matchRead').value="true";
            }
            if(item == null){
                dojo.byId('selectedAddressId').value = '';
            }else{

                dojo.byId('selectedAddressId').value = item;
            }
            if(newCustomer){
               dojo.byId("createCustomerForm").submit();
            }else{
               dojo.byId("updateCustomerForm").submit();
            }

}

function validateAddress(){
    //TODO: Add client side form submission validation here :)
    return true;
}

/*
*Ok here goes again :) There are lot of things shown and hide in the page :) The initila shown hide part comes here
* It accepts 2 arguments if its new and customer/#newNo is null
 */

function onLoadAddressPage(/*String*/id, /*String*/ selectedVar)
{
             var validatableStateId = dijit.byId("validatable_state_"+id);
			 var freeTextStateId = dojo.byId("free_text_state_"+id);
			 dojo.html.setDisplay(validatableStateId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextStateId,!selectedVar);
}

/**
 *  Assumption no data error happends, when the country change returns that state is there then what ever the value db returns is true to be asumed.
 *  If some value is not getting displayed in common address field, then mostly data errror.
 */
function reIntlizeValues(/*Number*/index,/*String*/address1,/*String*/address2,/*String*/country,/*String*/state,/*String*/city,/*String*/zip,
                        /*String*/phone,/*String*/email,/*String*/fax){
        dojo.byId("customerAddress1_"+index).value=address1;
        dojo.byId("customerAddress2_"+index).value=address2;
        dijit.byId("country_"+index).setValue(country);
        dojo.subscribe("statechange_"+index,this,function(result){
            if(result.isAvailable){
                dijit.byId("validatable_state_"+index).setValue(state);
            }else{
                dijit.byId("validatable_state_"+index).setDisabled(true); 
                dojo.byId("free_text_state_"+index).value=state;
            }
        });

        dojo.byId("free_text_city_"+index).value=city;
        dojo.byId("free_text_zip_"+index).value=zip;
        dojo.byId("free_text_phone_"+index).value=phone;
        dojo.byId("free_text_email_"+index).value=email;
        dojo.byId("free_text_fax_"+index).value=fax;

}

/**
 * Expecting the page to have newCompanyAddr for company submission newAddr for individual
 *
 */
function addNewCompany(){
    //Enable Visiblity only once then remove the handle to null
    if(newCompanyAddr != null){
        twms.util.adjustVisibilityAndSubmission(newCompanyAddr,true);
        newCompanyAddr = null;//Removing the handle to the mewCompanyAddr so that even if u click nothing will happend.
    }
}

function addIndividual(){
    //Enable Visiblity only once then remove the handle to null
    if(newAddr != null){
        twms.util.adjustVisibilityAndSubmission(newAddr,true);
        newAddr = null;//Removing the handle to the newAddr so that even if u click nothing will happend.
    }

}


