// Dynamically loading the ga.js from google-analytics site. As this was giving problem
// in preview pane. using pussh command syntax so that we need not wait for ga.js to load.
var _gaq = _gaq || [];

if (!self.googleAnalyticsScriptId) { // check if exists
	var head = document.getElementsByTagName("head")[0];
	googleAnalyticsScript = document.createElement('script');
	googleAnalyticsScript.id = 'googleAnalyticsScriptId';
	googleAnalyticsScript.type = 'text/javascript';
	googleAnalyticsScript.src = "http://www.google-analytics.com/ga.js";
	head.appendChild(googleAnalyticsScript);
}

dojo.addOnLoad(function(){
	_gaq.push(['_setAccount', dojoConfig.googleAnalyticsCode]);
	if(document.title.toUpperCase().indexOf('ERROR') == -1){
		_gaq.push(['_trackPageview']);
		var totalPageLoadTime = (new Date()).getTime() - pageLoadStartTime;
		_gaq.push(['_trackEvent','RESPONSE_TIME_FOR_USER',document.location.pathname,dojoConfig.loggedInUserId,totalPageLoadTime]);
	}else{
		_gaq.push(['_trackPageview',"/pages/error.jsp?page=" + document.location.pathname + document.location.search + "&from=" + document.referrer]);
		_gaq.push(['_trackEvent','ERROR_FOR_USER',document.location.pathname + document.location.search,dojoConfig.loggedInUserId, 1]);

	}
});
