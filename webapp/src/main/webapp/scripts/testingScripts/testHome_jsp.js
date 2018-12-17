function isTabLoaded(id) {
	var tab = dijit.byId(id);
	if(tab != null) {
		return tab.loaded;
	}
	return false;
}