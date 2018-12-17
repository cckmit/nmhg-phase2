dojo.provide("twms.widget.FileUploader");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.Tooltip");
dojo.require("dojo.io.iframe");
dojo.require("twms.widget.LoadingLid");

dojo.declare("twms.widget.FileUploader", [dijit._Widget, dijit._Templated], {
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("twms", "widget/templates/FileUploader.html"),
    name: "",
    fieldSize:1,
    uploadedFilesInfo: "",
    trimFileNameDisplayTo: "",
    documentDownloadUrl: "",
    documentUploadUrl: "",
    documentDeletionUrl: "",
    fileBrowserTriggerLabel: "Upload",
    disabled: false,
    canDeleteAlreadyUploaded: true,
    selectedFilesCountParamName: "",
    cssClasses: "",
    cssStyle: "",
    maxFilesToBeUploaded: "",
    singleFileUpload:false,

    _nextAvailableIndex: 0,
    _trimFileNameDisplay: false,
    _fileIndicesMarkedForDeletion: [],
    _newFileDetailsTemplate: "${name}",
    _existingFileDetailsTemplate: "Name: ${name} <br/> Uploaded By: ${uploadedBy} <br/> Uploaded On: ${uploadedOn} <br/> Size: ${size}",
    _uploadFilesCount: 0,
    _selectedFilesCountParam: null,
    _fileBrowserNodeMarkup : null,

    postCreate: function() {
        
	   
        this._trimFileNameDisplay = !dojo.string.isBlank(this.trimFileNameDisplayTo);
        this._fileBrowserNodeMarkup = this.fileBrowserNodeWrapper.innerHTML.replace(/([ ]{2,}|\n)/gm, '');

        dojo.dom.removeNode(this.selectedFileTemplateNode);

        this._processSelectedFilesCountParam();

        this._initFileBrowser(this.fileBrowserNode);

        if (!dojo.string.isBlank(this.uploadedFilesInfo)) {
            this._processAlreadyUploadedFiles();
        }
        
        var containingForm = this.fileBrowserNode.form;

        if (containingForm && "multipart/form-data" != containingForm.encoding.toLowerCase()) {
            containingForm.encoding = "multipart/form-data";
        }
        
    },

    _processSelectedFilesCountParam : function() {
        if(!this.disabled && !dojo.string.isBlank(this.selectedFilesCountParamName)) {
            this._selectedFilesCountParam = this._addHiddenInputField(this.domNode, this.selectedFilesCountParamName);
        }
    },

    _initFileBrowser: function(fileBrowserNode) {
        if (this.disabled) {
            this.fileBrowserNode.disabled = true;
            dojo.html.hide(this.fileBrowserNodeContainer);
            dojo.removeClass(this.domNode,"fileUploader");
            dojo.addClass(this.domNode,"fileUploaderDisabled");
        } else {
            //            dojo._setOpacity(this.fileBrowserNode, 0);

            this._listenForFileBowserNodeChange(fileBrowserNode);
        }
    },

    _listenForFileBowserNodeChange: function(fileBrowserNode) {
        dojo.connect(fileBrowserNode, "onchange", this, "_uploadFile");
    },

    _uploadFile: function() {
        twms.util.putLidOn(this.domNode, "Uploading File...");
        var _thisRef = this;

        dojo.io.iframe.send({
            form: this.uploadFormNode,
            handleAs: "json",
            load: dojo.hitch(_thisRef, "_handleUploadSuccess"),
            error: dojo.hitch(_thisRef, "_handleUploadFailure")
        });
    },

    _handleUploadSuccess: function(/*object*/ fileInfo) {
        twms.util.removeLidFrom(this.domNode);
        this._displayAttachedFileSection(fileInfo);

        // This is required, since we can't reset the value of the file browser
        // programmatically, due to security reasons. And if we don't reset it,
        // the "upload-delete-try-uploading-same-file-again" flow won't work,
        // since the onchange won't get triggerred.
        this._replaceFileBrowserNodeWithNewOne();
    },

    _handleUploadFailure: function(/*object*/ error) {
        twms.util.changeLidMessageFor(this.domNode, "Upload Failed");
        setTimeout(dojo.hitch(this, function() {
            twms.util.removeLidFrom(this.domNode);
        }), 3000);
    },

    _replaceFileBrowserNodeWithNewOne: function() {
        var _newFileBrowserNode = dojo.html.createNodesFromText(this._fileBrowserNodeMarkup, true);
        var _oldFileBrowserNode = dojo.dom.replaceNode(this.fileBrowserNode,
            _newFileBrowserNode);

        dojo.dom.destroyNode(_oldFileBrowserNode);
        this._initFileBrowser(_newFileBrowserNode);
        this.fileBrowserNode = _newFileBrowserNode;
    },

    _processAlreadyUploadedFiles: function() {
        var foo = this.uploadedFilesInfo = dojo.fromJson(this.uploadedFilesInfo);
        if(this.singleFileUpload && foo.id){
        	 this._displayAttachedFileSection(this.uploadedFilesInfo);
        }else{
        	 dojo.forEach(this.uploadedFilesInfo, function(uploadedFileInfo) {
                 this._displayAttachedFileSection(uploadedFileInfo);
             }, this);
        }
    },
    
    _displayAttachedFileSection: function(/*object*/ fileInfo) {
        var newFileNode = this.selectedFileTemplateNode.cloneNode(true);

        this._processFileDisplaySection(
            this._getFileDisplaySectionForNode(newFileNode), fileInfo);

        var fileRemovalSection = this._getFileRemovalSectionForNode(newFileNode);

        if (this._shouldShowFileRemovalSection(fileInfo)) {
            this._processFileRemovalSection(fileRemovalSection, newFileNode);
        } else {
            dojo.html.hide(fileRemovalSection);
        }

        this._nextAvailableIndex++;
        this._updateSelectedFilesCountParamValue(true); // increment count.

        dojo.html.show(newFileNode);
        this.selectedFilesNode.appendChild(newFileNode);
    },

    getSelectedFilesCount: function() {
        return this._uploadFilesCount;
    },

    _shouldShowFileRemovalSection: function(/*object*/ fileInfo) {
        return !this.disabled && (this.canDeleteAlreadyUploaded ||
            fileInfo.isOrphan);
    },

    _getFileDisplaySectionForNode: function(/*domNode*/ newFileNode) {
        return this._getChildByClass(newFileNode, "fileDisplaySection");
    },

    _getFileRemovalSectionForNode: function(/*domNode*/ newFileNode) {
        return this._getChildByClass(newFileNode, "fileRemovalSection");
    },

    _getFileSectionForNode: function(/*domNode*/ newFileNode) {
        return this._getChildByClass(newFileNode, "fileSection");
    },

    _getChildByClass: function(/*domNode*/ parentNode, /*string*/ className) {
        return dojo.query("." + className, parentNode)[0];
    },

    _processFileDisplaySection: function(/*domNode*/ fileDisplaySection, /*object*/ fileInfo) {
        var fileName = fileInfo.name;

        fileDisplaySection._rowIndex = this._nextAvailableIndex;
        fileDisplaySection._fileId = fileInfo.id;
        fileDisplaySection._isOrphan = fileInfo.isOrphan;
        fileDisplaySection._toolTip =
            dojo.string.substitute(this._existingFileDetailsTemplate, fileInfo);
        fileDisplaySection.innerHTML = this._trimFileNameDisplay ?
            twms.string.summarize(fileName, this.trimFileNameDisplayTo) :
            fileName;
        
        if(this.singleFileUpload){
        	 var hiddenFieldName = this.name ;
        }else{
        	 var hiddenFieldName = this.name + "[" + this._nextAvailableIndex + "]";
        }
        this._addHiddenInputField(fileDisplaySection, hiddenFieldName, fileInfo.id);

        this._configureDocumentTooltip(fileDisplaySection, fileInfo);

        this._configureDocumentDownload(fileDisplaySection, fileInfo);
    },

    _configureDocumentTooltip: function(/*domNode*/ fileDisplaySection, /*object*/ fileInfo) {
        dojo.connect(fileDisplaySection, "onmouseover", this, function() {
            dijit.showTooltip(fileDisplaySection._toolTip, fileDisplaySection);
            setTimeout(function() {
                dijit.hideTooltip(fileDisplaySection);
            }, 5000);
        });

        dojo.connect(fileDisplaySection, "onmouseout", function() {
            dijit.hideTooltip(fileDisplaySection);
        });
    },

    _configureDocumentDownload: function(/*domNode*/ fileDisplaySection, /*object*/ fileInfo) {
        dojo.connect(fileDisplaySection, "onclick", this, function() {
            dojo.io.iframe.send({
                url: this.documentDownloadUrl,
                content: {
                    document: fileInfo.id
                },
                timeout: 200
            });
        });
    },

    _processFileRemovalSection: function(/*domNode*/ fileRemovalSection, /*domNode*/ newFileNode) {
        dojo.connect(fileRemovalSection, "onclick", this, function() {
            this._handleSelectionRemoval(newFileNode);
        });
    },

    _handleSelectionRemoval: function(/*domNode*/ nodeToBeDeleted) {
        var fileDisplaySection = this._getFileDisplaySectionForNode(nodeToBeDeleted);

        if(fileDisplaySection._isOrphan) {
            twms.ajax.fireHtmlRequest(this.documentDeletionUrl, {
                document: fileDisplaySection._fileId
            }, function(response) {
                // Absolutely nothing to do here, but we still need to provide
                // function, or else the dojo.xhr call wud crib!
            });
        } else {
            this._markUploadedFileForDeletion(fileDisplaySection);
        }

        this._updateSelectedFilesCountParamValue(false);
        dojo.dom.destroyNode(nodeToBeDeleted);
    },

    _markUploadedFileForDeletion: function(/*domNode*/ fileDisplaySection) {
        this._fileIndicesMarkedForDeletion.push(fileDisplaySection._fileId);

        var deletedFilesParamNode = this.deletedFilesParamNode;

        deletedFilesParamNode.disabled = false;
        deletedFilesParamNode.value = this._fileIndicesMarkedForDeletion.join(",");
    },

    _updateSelectedFilesCountParamValue : function(/*boolean*/ incrementCount) {
    	if(this.singleFileUpload){
			 this.maxFilesToBeUploaded=1;   	
		}
        if(incrementCount) {
            this._uploadFilesCount++;
        } else {
            this._uploadFilesCount--;
        }

        if(this._selectedFilesCountParam) {
            this._selectedFilesCountParam.value = this._uploadFilesCount;
        }
       
        if(this._uploadFilesCount == this.maxFilesToBeUploaded){
        	 this._HideBrowseButton(true);
        }
        
        if(this._uploadFilesCount < this.maxFilesToBeUploaded){
        	 this._HideBrowseButton(false);
       }
      
    },
    
    _HideBrowseButton: function(/*boolean*/hide){
    	this.fileBrowserNode.disabled = hide;
    	if(hide){
            dojo.html.hide(this.fileBrowserNodeContainer);
    	}else{
    		 dojo.html.show(this.fileBrowserNodeContainer);
    	}
    },

    _addHiddenInputField: function(/*domNode*/ parentNode, /*string*/ name, /*string*/ value) {
        var hiddenField = document.createElement("input");
        hiddenField.type = "hidden";
        hiddenField.name = name;
        hiddenField.value = value;
        parentNode.appendChild(hiddenField);

        return hiddenField;
    }
});	