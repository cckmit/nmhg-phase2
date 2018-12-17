dojo.addOnLoad(function() {
    var obj1 = new blah.Object(3,5);
    var obj2 = new blah.Object(7,7);
    var validator1 = new twms.utility.Validator(false, "EXP", "validator 1 failed.", "val1", "===", "val2");
    var validator2 = new twms.utility.Validator(false, "FN", "validator 2 failaed", "valIt");
    var validator3 = new twms.utility.Validator(false, "FN", "validator 3 failed", "tryIt", "4");
    var validator4 = new twms.utility.Validator(false, "FN", "validator 4 failed", "tryIt", "val2");
    var validatorChain1 = new twms.utility.ValidatorChain(true);
    with(validatorChain1) {
        add(validator1);
        add(validator2);
    }
    var validatorChain2 = new twms.utility.ValidatorChain(false);
    with(validatorChain2) {
        add(validator3);
        add(validator4);
    }
    var validatorChain = new twms.utility.ValidatorChain(false);
    with(validatorChain) {
        add(validator1);
        add(validatorChain1);
        add(validatorChain2);
    }
    alert("Messages for Obj1 : " + validatorChain.validate(obj1));
});
dojo.declare("blah.Object", null, {

    val1 : 0,
    val2 : 0,

    constructor : function(val1, val2) {
        this.val1 = val1;
        this.val2 = val2;
    },

    valIt : function() {
        return this.val1 === this.val2;
    },

    tryIt : function(value) {
        return this.val1 === value;
    }
});