var wSplit = null;
var wNav = null;
var dDockNav = null;

dojo.addOnLoad(function() {
    wSplit = dijit.byId("split");
    wNav = dijit.byId("navigator");
    dDockNav = dojo.byId("dockNavigator");
});

var wWelcome = null;
var wDockNav = null;
var dUndockNav = null;
var wAccordion = null;
var wDock = null;
var wRootLayoutContainer = null;

dojo.addOnLoad(function() {
    wWelcome = dijit.byId("welcome");
    wDockNav = dijit.byId("dockNavigator");
    wAccordion = dijit.byId("accordion");
    wDock = createSideBar();
    wRootLayoutContainer = dijit.byId("rootLayoutContainer");
});

/**
 * Creating a structure like this....
 * <div dojoType="dijit.layout.LayoutContainer" id="dock" layoutAlign="left">
 *     <p id="undockNavigator" layoutAlign="top" class="button">&nbsp;&nbsp;</p>
 * </div>
 */
function createSideBar() {
	dUndockNav = document.createElement("p");
    dUndockNav.innerHTML = "&nbsp;&nbsp;";
    dojo.addClass(dUndockNav, "button");
    dUndockNav.id = "undockNavigator";
    dUndockNav.setAttribute("layoutAlign", "top");
    var navHolder = document.createElement("div");
    navHolder.appendChild(dUndockNav);
    var _wDock =  new dijit.layout.BorderContainer({id: "dock", layoutAlign: "left"}, navHolder);
	dojo.addClass(_wDock.domNode, "dock");
	return _wDock;
}

var wMyClaims = null;
var wInventory = null;
var wAdmin = null;
var wFavourites = null;
var wAlerts = null;
var wSettings = null;

dojo.addOnLoad(function() {
    wMyClaims = dijit.byId("myclaims");
    wInventory = dijit.byId("inventory");
    wAdmin = dijit.byId("admin");
    wFavourites = dijit.byId("favourites");
    wAlerts = dijit.byId("alerts");
    wSettings = dijit.byId("settings");
});

dojo.addOnLoad(function() {
    dojo.connect(dDockNav, "onclick", function() {
        dojo.publish("/navigator/dock");
    });
    dojo.connect(dUndockNav, "onclick", function() {
        dojo.publish("/navigator/undock");
    });
    dojo.subscribe("/navigator/dock", this, dockNavigator);
    dojo.subscribe("/navigator/undock", this, undockNavigator);
});

dojo.addOnLoad(function() {
    dojo.subscribe("/accordion", this, openAccordionTab);
});

dojo.addOnLoad(function() {
    if (isNavDocked()) {
        wSplit.removeChild(wNav);
        showSideBar();
    }
});

var bSideBarVisibility = false;

function isSideBarVisible() {
	return bSideBarVisibility;
}

function hideSideBar() {
	if(isSideBarVisible()) {
		wRootLayoutContainer.removeChild(wDock);
		bSideBarVisibility = false;
	}
}

function showSideBar() {
	if(!isSideBarVisible()) {
		if(isNavDocked()) {
			wRootLayoutContainer.addChild(wDock);
			bSideBarVisibility = true;
		}
	}
}

var _iNavShare = 210;
var _iNavWidth = "210px";
function dockNavigator() {
    if (isNavDocked()) return;
    removeNavFromSplit();
    dojo.publish("/arrowKeyNavigation/disable");
   	showSideBar();
}

function undockNavigator() {
	//fix for NMHGSLMS-1281
	dojo.addOnLoad(function () {			
		parent.publishEvent("/accordion/refreshsearchfolders");					
	});
    if (isNavUndocked()) return;
    addNavToSplit();
    dojo.publish("/arrowKeyNavigation/enable");
   	hideSideBar();
}

function openAccordionTab(evt) {
    if (evt != null) {
        if (evt.tab != null) {
            dijit.byId(evt.tab)._onTitleClick();
        }
        else if (evt.node != null) {
        	evt.node._onTitleClick();
        }
    }
}

function removeNavFromSplit() {
    _iNavShare = wNav.sizeShare;
    _iNavWidth = wNav.domNode.style.width;
    wNav.sizeShare = 0;
    wSplit.removeChild(wNav);
    wRootLayoutContainer._layoutChildren("split",0);
}

function addNavToSplit() {
    wNav.sizeShare = _iNavShare;
    wSplit.addChild(wNav, 0);
    wRootLayoutContainer._layoutChildren("split",_iNavWidth.replace('px',''));
}

function isNavDocked() {
	//return dojo.byId("navigator").getAttribute("sizeShare") == 0;
    return wNav.sizeShare == 0;
}

function isNavUndocked() {
	//return dojo.byId("navigator").getAttribute("sizeShare") != 0;
    return wNav.sizeShare != 0;
}