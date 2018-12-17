/*

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

*/
/**
 * @author: janmejay.singh
 */
/**
 * This class manages the refreshing of folder's span which holds the number of items in that folder.
 * When the refresh functionality is needed by some folder li it registers with the refreshManager
 * by calling the register method. After registering if the method requestRefresh is called with the folderId
 * as its argument (the folderId of a folder that is registered), an AJAX request is fired on the
 * associated action. This AJAX request returns with the correct numbers, and all the folders
 * (not just the requested folder) that were associated with that particular action, get
 * refreshed (new number appears next to the links).
 */
dojo.declare("tavant.twms.RefreshManager", null, {

	/*
	 * This is meant to hold the actions, along with the folders that are associated with it.
	 * holds the maps of the format:
	 * {
	 * 	  action : url,
	 * 	  folders :  array of elements of the format {
	 *              id : string(folderId),
	 * 				name : string(label of the folder),
	 * 				spanDom : domNode(span that holds the count)
	 *    }
	 * }
	 */
	_registeredActions : new Array(),

	/*
	 * This array holds the actionNames for which request has been sent but the response has not been recived yet.
	 * Once the response is recived the actionName on which the request was made, is deleted from the array as the
	 * first step of response. If the actionName is set in this array the new AJAX request on the same action is
	 * not fired. This saves the server when more than one field that use the same action to refresh request refresh.
	 * Firing a new request in those cases becomes useless and is a waste of resources.
	 */
	//_pendingRequests : new Array(),

	/*
	 * This array holds the action names, for which the ajax request has to be fired. When the request is fired,
	 * the item is removed from the queue, and handled, and lock is obtained. When the lock is released, next item
	 * is handled.
	 */
	_requestQueue : new Array(),

	_requestInProgress : false,//this acts as a lock to prevent more than one request at a time.

	constructor : function() {
		//doing nothing
	},

	/****Public Methods****/

	register : function(folderId, folderName, action) {
		var folder = dojo.byId(folderId);
		var span = dojo.query("span", folder)[0];
		var i = 0;
		for(i = 0; i < this._registeredActions.length; i++) {
			if(this._registeredActions[i].action == action) {
				this._addTo(i, folderId, folderName, span);
				return;
			}
		}
		this._registeredActions.push({
			action: action,
			folders: new Array()
		});
		this._addTo(this._registeredActions.length - 1, folderId, folderName, span);
	},

	refreshEventHandler : function(event) {
		if(event) {
			if(event.action) {
				this._requestRefreshByActionName(event.action);
			} else if(event.folderId) {
				this._requestRefreshByFolderId(event.folderId);
			} else if(event.folderName) {
				this._requestRefreshByFolderName(event.folderName);
			} else {
				this._refreshAll();
			}
		} else {
			this._refreshAll();
		}
	},

	/****Private Methods****/

	_addTo : function(registeredActionIndex, folderId, folderName, spanDom) {
		this._registeredActions[registeredActionIndex].folders.push({
				id: folderId,
				name: folderName,
				spanDom: spanDom
			});
	},

	_requestRefreshByActionName : function(actionName) {
		for(var i = 0; i < this._registeredActions.length; i++) {
			if(this._registeredActions[i].action == actionName) {
				this._appendToQueue(actionName);
			}
		}
	},

	_requestRefreshByFolderName : function(folderName) {
		var actionName = null;
		for(var i = 0; i < this._registeredActions.length; i++) {
			for(var j = 0; j < this._registeredActions[i].folders.length; j++) {
				if(this._registeredActions[i].folders[j].name == folderName) {
					actionName = this._registeredActions[i].action;
				}
			}
		}
		if(actionName != null) {
			this._appendToQueue(actionName);
		}
	},

	_requestRefreshByFolderId : function(folderId) {
		var actionName = null;
		for(var i = 0; i < this._registeredActions.length; i++) {
			for(var j = 0; j < this._registeredActions[i].folders.length; j++) {
				if(this._registeredActions[i].folders[j].id == folderId) {
					actionName = this._registeredActions[i].action;
				}
			}
		}
		if(actionName != null) {
			this._appendToQueue(actionName);
		}
	},

	_fireRequest : function(actionName) {
		var self = this;
		twms.ajax.fireJsonRequest(actionName, null, function(response) {
            	var action = null;
            	for(var j = 0; j < self._registeredActions.length; j++) {
            		if(self._registeredActions[j].action == actionName) {
	            		action = self._registeredActions[j];
            		}
            	}
            	if(action != null) {
	            	for(var i = 0; i < response.length; i++) {
    	        		for(j = 0; j < action.folders.length; j++) {
    	        			if(action.folders[j].name == response[i][0]) {
    	        				if(action.folders[j].spanDom.innerHTML != response[i][1]) {
    	        					action.folders[j].spanDom.innerHTML = response[i][1];
    	        					var tab = tabManager.getTabHavingLabel(action.folders[j].name);
    	        					if(tab != null) {
    	        						var frame = window.frames[tab.id + "_iframe"];
    	        						if(frame != null) {
    	        							frame.publishEvent("/refresh/inboxView", {folderName: action.folders[j].name});
    	        						}
    	        						delete frame;
    	        					}
    	        					delete tab;
    	        				}
    	        			}
    	        		}
        	    	}
            	}
            	delete action;
            	self._releaseLock();
                return response;
            }, function(response) {
                //alert("error in loading data! \n" + error.message); //FIXME
        	    //ignoring, can't show this popup to the user.
        	    // assuming the error to be a session expiry
                publishEvent("/refresh/stopAutoRefresh");
                self._releaseLock();
                return response;
            }, true);
	},

	_refreshAll : function() {
		for(var i = 0; i < this._registeredActions.length; i++) {
			this._appendToQueue(this._registeredActions[i].action);
		}
	},

	_appendToQueue : function(action) {
		for(var i = 0; i < this._requestQueue.length; i++) {
			if(this._requestQueue[i] == action) {
				return;
			}
		}
		this._requestQueue.push(action);
		if(!this._requestInProgress) {
			this._processRequest();
		}
	},

	_processRequest : function() {
		if(this._requestInProgress || ( this._requestQueue.length == 0 ) ) {
			return;
		}
		this._obtainLock();
		var action = this._requestQueue.shift();
		this._fireRequest(action);
	},

	_obtainLock : function() {
		this._requestInProgress = true;
	},

	_releaseLock : function() {
		this._requestInProgress = false;
		this._processRequest();
	}
});

var refreshManager = null;

dojo.addOnLoad(function() {
	refreshManager = new tavant.twms.RefreshManager();
	dojo.subscribe("/refresh/folderCount", refreshManager, "refreshEventHandler");
});


/*
 * Automatic refresh of the folders
 */
var _oAutoRefresh = null;
var autoRefreshInterval = 15*60*1000;

function autoRefreshFolderCount() {
	if(_oAutoRefresh != null) { // to prevent one extra request in the begining.
		publishEvent("/refresh/folderCount");
   	}
    _oAutoRefresh = setTimeout(autoRefreshFolderCount, autoRefreshInterval);
}

function stopAutoRefreshing() {
	clearTimeout(_oAutoRefresh);
}

dojo.addOnLoad(function() {
	autoRefreshFolderCount();
	dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);
});