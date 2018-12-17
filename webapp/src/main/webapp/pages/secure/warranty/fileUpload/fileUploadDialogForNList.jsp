<%@taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("twms.widget.Dialog");
</script>

<div style="display:none">
    <div dojoType="twms.widget.Dialog" id="fileUploadDialog_<s:property value = "%{#nListIndex}" />" style="width:54%;overflow-y: auto;">
        <div class="dialogContent" dojoType="dijit.layout.LayoutContainer" id="dialogLayoutContainer_<s:property value = "%{#nListIndex}" />"
              style="padding: 0; margin: 0; width:auto;">
            <iframe id="fileUploadFrame_<s:property value = "%{#nListIndex}" />" style="border: none; width: 100%; height:100%"></iframe>
        </div>
    </div>
</div>
<script type="text/javascript">
    var _uploadFileWizard = {
        uploadDialog : null,
        uploadIframe : null,
        dialogLayoutContainer : null,
        dialogBaseHeight : 120,
        heightIncrementFactor : 40,
        callback : function(message) {
            if(this.usersCallback) {
                this.usersCallback(message);
            }
            this.hideDialog();
        },
        usersCallback : null,
        hideDialog : function() {
            this._resetUsersFunctions();
            this.uploadDialog.hide();
            this.uploadIframe.src = "";
        },
        onClose : function() {
            if(this.usersOnClose) {
                this.usersOnClose();
            }
            this.hideDialog();
        },
        _resetUsersFunctions : function() {
            this.usersCallback = null;
            this.usersOnClose = null;
        },
        usersOnClose : null,
        setDialogHeight : function(/*int*/ batchSize) {
            var multiples = batchSize ? batchSize - 1 : 0;
            dojo.style(this.dialogLayoutContainer.domNode, "height",
                    (this.dialogBaseHeight + 15 + this.heightIncrementFactor*multiples) + "px");
        }
    };
    dojo.addOnLoad(function() {
    	var nListIndex = "<s:property value="%{#nListIndex}"/>";
        _uploadFileWizard.uploadDialog = dijit.byId("fileUploadDialog_"+nListIndex);
        _uploadFileWizard.uploadIframe = dojo.byId("fileUploadFrame_"+nListIndex);
        _uploadFileWizard.dialogLayoutContainer = dijit.byId("dialogLayoutContainer_"+nListIndex);        
        dojo.subscribe("/uploadDocument/dialog/show_"+nListIndex, null, function(/*attrs 'callback' and 'onClose'*/message) {
            _uploadFileWizard.usersCallback = message.callback;
            _uploadFileWizard.usersOnClose = message.onClose;
            _uploadFileWizard.setDialogHeight(message.batchSize);
            var uploaderSrc = "showUploadForm.action";
            if(message.batchSize) {
                uploaderSrc += ("?batchSize=" + message.batchSize + "&nListIndex=" + nListIndex);
            }
            _uploadFileWizard.uploadIframe.src = uploaderSrc;
            _uploadFileWizard.uploadDialog.show();
        });
        dojo.subscribe("/uploadDocument/uploaded_"+nListIndex, null, function(message) {
            _uploadFileWizard.callback(message);
        });

    });
</script>