<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<style type="text/css">
    button.marginedButton {
       margin: 35px 10px 0pt 10pt;
    }

    #excelDownloadDlg {
        width: 65%;
    }
</style>
<script type="text/javascript">
	dojo.require("twms.widget.Dialog");
	
	dojo.declare("twms.download.Helper", null, {
	
		_url : "",
		_eventToPopulate : "",
		_dialog : null,
		_totalRecords : null,
		_pageSize : <s:property value="maxDownloadableRows"/>,
		_pageNumber : null,
	
		constructor : function(/*Download dialog [dijit.Dialog]*/ dialog,
							   /*Map (domNodes for downloadAll and downloadPage)*/ actionButtons) {
			this._dialog = dialog;
			//dojo.connect(actionButtons.downloadAll, "onclick", this, "downloadAll");
			dojo.connect(actionButtons.downloadPage, "onclick", this, "downloadPage");
		},
		
		setDownloadParamsAndShowDialog : function(eventToPopulate, url, totalRecords) {
			this._url = url;
			this._eventToPopulate = eventToPopulate;
			if(this._totalRecords == null) {
				if(totalRecords) {
					this._totalRecords = totalRecords;
				}else {
					for(var tableId in summaryTableVars) {
						this._totalRecords = dojo.byId(summaryTableVars[tableId].totalRecordSpanId).innerHTML;
						break;
					}
				}
				this.preparePageLinks();
			}
			this._dialog.show();
		},
		
		downloadAll : function(pageNumber) {
			this._download(true, pageNumber);
		},
		
		downloadPage : function() {
			this._download();
		},
		
		_download : function(/*boolean? (download all)*/ downloadAll, pageNumber) {
			publishEvent(this._eventToPopulate, {
			    url : this._url,
			    returnTo : function(newUrl) {
			        newUrl += downloadAll ? "&downloadMaxResults=true&downloadPageNumber="+pageNumber : "";
			        getFileDownloader().download(newUrl);
			    }
			});
			this.hideDialog();
		},
		
		hideDialog : function() {
			this._dialog.hide();
		},
		
		preparePageLinks : function() {
			var _quickPaginatorRowHolder = dojo.byId('paginatingRowHolder');
			var pageLinks = '';
			var noPages = Math.floor(this._totalRecords/this._pageSize) + (this._totalRecords%this._pageSize==0?0:1);
			dojo.dom.destroyNode(dojo.query("tr", _quickPaginatorRowHolder)[0]);
			var tr = document.createElement("tr");
			var self = this;
			var onClickGenerator = function(pageNo) {
	            return function() {
		            self._download(true,pageNo)
	            };
	        }
			for(var i=1;i<=noPages;i++) {
				var td = document.createElement("td");
				if(noPages == 1) 
					pageLink = "<s:text name="label.download.all" />";
				else {
					pageLink = ((i-1)*this._pageSize+1);
					pageLink += '-';
					if(i == noPages) {
						pageLink += this._totalRecords;
					}else {
						pageLink += (i*this._pageSize);
					}
				}
	            td.innerHTML = pageLink;
	            var pageNo = i;
	            dojo.connect(td, "onclick", onClickGenerator(pageNo));
	            delete pageNo;
	            tr.appendChild(td);
	            if(i%5 == 0) {
					_quickPaginatorRowHolder.appendChild(tr)
					tr = document.createElement("tr");
				}
				if(i==noPages) {
					_quickPaginatorRowHolder.appendChild(tr);
				}
			}
		}
	});
	
	var exportExcel = null;
	
	dojo.addOnLoad(function() {
		var downloadDialog = dijit.byId("excelDownloadDlg");
		if(typeof summaryTableVars === "undefined") {
			dojo.byId("downloadPage").style.display="none";
		}else{
			dojo.byId("downloadPage").style.display="inline";
		}
		var actionButtons = {downloadPage : dojo.byId("downloadPage")};
		downloadDialog.doDefault = function() {
			actionButtons.downloadPage.click();
		};
		var helper = new twms.download.Helper(downloadDialog, actionButtons);
		exportExcel = dojo.hitch(helper, "setDownloadParamsAndShowDialog");
	});
</script>
<div style="display:none">
<div dojoType="twms.widget.Dialog" id="excelDownloadDlg" bgColor="white" bgOpacity="0.5" toggle="fade"
	toggleDuration="250" title="Download To Excel">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client" 
             style="height: 90px; background : #F3FBFE;">
			<center>
                <button id="downloadPage" class="buttonGeneric marginedButton">
                    <s:text name="label.download.page"/>
                </button>
            </center>
            <center>
            	<div class="quick_page_switch" style="float:center">
            		<table celpadding="4"><tbody id="paginatingRowHolder"><tr></tr></tbody></table>
            	</div>
            </center>
		</div>
</div>
</div>