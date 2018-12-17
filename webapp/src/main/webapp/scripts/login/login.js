dojo.require("twms.widget.Tooltip");

dojo.declare("twms.util.CapsLockOnDetector", null, {
    _alphaCharPattern : new RegExp("[a-zA-Z]"),
    _upperCasePattern : new RegExp("[A-Z]"),
    _capsLockWarningToolTip : null,
    _capsLockOnWarningShown : false,
    displayMessage: null,

    constructor: function(/*string or domNode*/targetNodeOrId, /* string*/ displayMessage) {
        var targetNode = dojo.byId(targetNodeOrId);

        if(!targetNode) {
            throw new Error("twms.util.CapsLockDetector: Specified target node doesn't exist: " + targetNodeOrId);
        }

        if(dojo.string.isBlank(displayMessage)) {
            throw new Error("twms.util.CapsLockDetector: The message to be displayed cannot be empty!");
        }

        this.displayMessage = "<div class='warning>" + displayMessage + "</div>";

        dojo.connect(targetNode, "onkeypress", this, "_warnIfCapsLockOn");
        dojo.connect(targetNode, "onblur", dojo.hitch(this, function() {
            this._capsLockOnWarningShown = false;    
        }));

    },

    _warnIfCapsLockOn: function(evt) {
        var capsLockOn = this._isCapsLockOn(evt);

        if(!this._capsLockOnWarningShown && capsLockOn) {
            this._capsLockOnWarningShown = true;
            this._showCapsLockOnWarningTooltip();
        } else {
            this._capsLockOnWarningShown = capsLockOn;
        }
    },

    _showCapsLockOnWarningTooltip: function() {
        if(!this._capsLockWarningToolTip) {
            this._capsLockWarningToolTip = new twms.widget.Tooltip({
                label: this.displayMessage,
                showDelay: 10,
                connectId: ["password"]
            });
        }

        this._capsLockWarningToolTip.setDisabled(false);
        this._capsLockWarningToolTip.open();

        setTimeout(dojo.hitch(this, function() {
            this._capsLockWarningToolTip.setDisabled(true);
        }), 3500);
    },

    _isCapsLockOn: function(evt) {
        var typedKey = evt.keyChar;

        var keyIsAlphaChar = this._alphaCharPattern.test(typedKey);
        if(!keyIsAlphaChar) {
            // Can't say...
            return null;
        }

        var shiftPressed = evt.shiftKey;
        var keyIsInUpperCase = this._upperCasePattern.test(typedKey);
        var keyIsInLowerCase = !keyIsInUpperCase;

        return (!shiftPressed && keyIsInUpperCase) ||
               (shiftPressed && keyIsInLowerCase);
    }
});