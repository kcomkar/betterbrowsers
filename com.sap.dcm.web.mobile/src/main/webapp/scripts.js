// Get the workcenters and then build the shell
$(document).ready(function() {
	//core UI texts
	coreBundle = getBundle("resources/core.properties");
	/*************** Busy indicator *************/
	var bBusyAnimate = false;
	var iBusyLeft = 0;
	var iBusyDelta = 60;
	var iBusyTimeStep = 50;
	var iBusyWidth = 500;
	var iBusyPageWidth;
	var $Busy;
	
	if (sap.ui.getCore().getConfiguration().getTheme() == "sap_goldreflection") {
		sap.ui.core.BusyIndicator.attachOpen(function(oEvent) {
			$Busy = oEvent.getParameter("$Busy");
			iBusyPageWidth = jQuery(document.body).width();
			$Busy.css("top", "0").css("width", iBusyPageWidth + "px");
			bBusyAnimate = true;
			iBusyLeft = $Busy[0].offsetLeft;
			window.setTimeout(animationStep, iBusyTimeStep);
		});
		sap.ui.core.BusyIndicator.attachClose(function(oEvent) {
			bBusyAnimate = false;
		});
	}
	
	function animationStep() {
		if (bBusyAnimate) {
			iBusyLeft += iBusyDelta;
			if (iBusyLeft > iBusyPageWidth) {
				iBusyLeft = -iBusyWidth;
			}
			$Busy.css("background-position", iBusyLeft + "px 0px");
			window.setTimeout(animationStep, iBusyTimeStep);
		}
	}
		
	var url = getRestRoot("/index.html") + "/ui/init";
	showBusyIndicator();
	$.ajax({
		url : url,
		dataType : 'json',
		success : function(data) {
			sap.ui.core.BusyIndicator.hide();
			buildShell(data.uiInitializationResponse);
		},
		error : function(data) {
			sap.ui.core.BusyIndicator.hide();
			handleErrors(data);
		}
	});
});

currentActiveModule = undefined;

/**
 * Create a default page in case of errors
 * @param response the page service response
 */
function handleErrors(response) {
	jQuery.sap.require("sap.ui.commons.MessageBox");
	sap.ui.commons.MessageBox.show(coreBundle.getText("msg.fatalError"),
			sap.ui.commons.MessageBox.Icon.CRITICAL, "Fatal Error",
			[ sap.ui.commons.MessageBox.Action.ABORT ], function() {
				window.location.replace(getRootUrl("index.html")
						+ "j_spring_security_logout");
			});

}

/**
 * Creats a new shell for the given lists of workcenters
 * 
 * @param workspaces
 *            the list of work centers
 */
function buildShell(init) {
	//WORKAROUND - otherwise the checkboxes in user management would lead to an endless loop of rerendering
	if(jQuery.sap.require("sap.ui.core.UIArea")) {
		sap.ui.core.UIArea.MAX_INVALIDATED_CHILDREN = 10000;
	}
	
	// create a workcenter and the workcenter view for each paga and subpagge
	var pages = new Array();
	if (typeof init.pages != "undefined" ) {
		if(typeof init.pages.length != "undefined"){
			//array of pages
			for ( var i = 0; i < init.pages.length; i++) {
				var page = init.pages[i];
				handlePage(page, pages);
			}
		}else{
			//single page
			handlePage(init.pages, pages);
		}
	} else {
		// No workcenters assigned
		pages.push( new sap.ui.ux3.NavigationItem({
			key : "errorPage",
			text : "Error"
		}));
	}

	// Create the ux3 Shell
	// ...fill several properties and aggregations in JSON syntax; alternatively
	// they could also be set one by one
	var title = coreBundle.getText("lbl.headApp")+" - "+coreBundle.getText("lbl.headHana");
	var oShell = new sap.ui.ux3.Shell("myShell", {
		appIcon : "images/SAP_new.png",
		appTitle : title,
		showSearchTool : false,
		showInspectorTool : false,
		showFeederTool : false,
		worksetItems : pages,
		logout : function() { // create a handler function and attach it to
								// the "logout" event
			var url = getRootUrl("index.html") + "j_spring_security_logout";
			document.location.href = url;
		}
	});
	//Add user name into shell header
	oShell.addHeaderItem( new sap.ui.commons.TextView({text : init.user.name}));

	// when the user selects a workset, put the respective content into the
	// shell's main area
	oShell.attachWorksetItemSelected(function(oEvent) {
		var key = oEvent.getParameter("key");
		if(currentActiveModule != null && typeof currentActiveModule != "null" && typeof currentActiveModule != "undefined" && typeof currentActiveModule.reset == "function")
			currentActiveModule.reset();
		oShell.setContent(getContent(key));
	});

	// set the initial content of the Shell - the Home Overview is selected
	// initially
	oShell.setContent(getContent(getKeyOfFirstPage(pages)));

	// place the Shell into the <div> element defined below
	oShell.placeAt("shellArea");
}

/**
 * Function to build the pages of the shell
 * @param page the backend page json object
 * @param arrayUINavigationItems array of ui navigation items
 */
function handlePage(page, arrayUINavigationItems){
	if (page.subPages != null && typeof page.subPages != "undefined") {

		// page with sub pages
		if (page.subPages.id != "") {
			arrayUINavigationItems.push( new sap.ui.ux3.NavigationItem({
				key : page.id,
				text : coreBundle.getText(page.uiText),
				subItems : [ new sap.ui.ux3.NavigationItem({
					key : page.subPages.id,
					text : coreBundle.getText(page.subPages.uiText)
				}) ]
			}));
		} else {
			var subPages = new Array(page.subPages.length);
			for ( var k = 0; k < page.subPages.length; k++) {
				var subPage = page.subPages[k];
				subPages[k] = new sap.ui.ux3.NavigationItem({
					key : subPage.id,
					text : coreBundle.getText(subPage.uiText)
				});
			}
			arrayUINavigationItems.push( new sap.ui.ux3.NavigationItem({
				key : page.id,
				text : coreBundle.getText(page.uiText),
				subItems : subPages
			}));
		}
	} else {
		// page without sub pages
		arrayUINavigationItems.push( new sap.ui.ux3.NavigationItem({
			key : page.id,
			text : coreBundle.getText(page.uiText)
		}));
	}
}

/**
 * Function to determin the key of the first page (default page)
 * @param pages the list of pages
 * @returns the key of the first page
 */
function getKeyOfFirstPage(pages){
	var subitems = pages[0].getSubItems();
	if(typeof subitems != "undefined" && subitems.length > 0){
		return subitems[0].getKey();
	}else{
		return pages[0].getKey();
	}
}

/**
 * Function to get the content of the page
 * 
 * @param key =
 *            the key of the page
 * @returns the page content
 */
function getContent(key) {
	if (key != "errorPage") {
		jQuery.sap.require(key);
		var o = currentActiveModule = jQuery.sap.getObject(key);
		var c = o.getContent();
		return c;
	} else {
		var textView = new sap.ui.commons.TextView({
			text : coreBundle.getText("msg.logon.roleMis"),
			semanticColor: sap.ui.commons.TextViewColor.Negative, 
			design: sap.ui.commons.TextViewDesign.Bold
			});
		return textView;
	}
}