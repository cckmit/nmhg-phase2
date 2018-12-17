/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.GoogleStaticMap"]){dojo._hasResource["twms.widget.GoogleStaticMap"]=true;dojo.provide("twms.widget.GoogleStaticMap");dojo.require("twms.widget.GoogleMap");dojo.declare("twms.widget.GoogleStaticMap",twms.widget.AbstractGoogleMapWidget,{cssClass:"googleStaticMap",_mapStaticBaseUrl:"http://maps.google.com/staticmap?",postCreate:function(){this.inherited("postCreate",arguments);this._processDimensions();this.renderMap();},renderMap:function(){this.inherited("renderMap",arguments);var _1={maptype:this.mapType,size:this.width+"x"+this.height,key:this.apiKey};if(this._markersArray.length>0){_1.markers=this._markersArray.join("|");}else{_1.center=this.center;_1.zoom=this.zoom;}var _2=dojo.objectToQuery(_1);dojo.style(this.domNode,"backgroundImage","url("+this._mapStaticBaseUrl+_2+")");},_processDimensions:function(){if(this.width>512){this.width=512;}if(this.height>512){this.height=512;}}});}