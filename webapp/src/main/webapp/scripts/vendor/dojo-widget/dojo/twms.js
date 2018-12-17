/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


require(['dojo/_base/kernel', 'dojo/_base/loader'], function(dojo){
  // Register "lib" to be a peer to Dojo's parent folder.
  // Make sure the module path does *not* end in a slash.
  dojo.registerModulePath("twms", "../twms");

});