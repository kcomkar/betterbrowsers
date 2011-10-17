/**
 * Page initialization
 */
$(document).ready(function() {
	//core UI texts
	coreBundle = getBundle("../resources/core.properties");
	
	var title = coreBundle.getText("lbl.headApp")+" - "+coreBundle.getText("lbl.headHana");
    var header = new sap.ui.commons.ApplicationHeader("appHeader"); 
    header.setLogoSrc("../images/SAP_new.png");
    header.setLogoText(title);   
    header.setDisplayWelcome(false);
    header.setDisplayLogoff(false);	
	header.placeAt("header");
	
	var user = "";
	var vars = getUrlVars();
	if(typeof vars != "undefined"){
		user = vars.username;
	}
	
	changePwdMessageBar = new sap.ui.commons.MessageBar('dcm-changePwdMsgBar');
	changePwdMessageBar.setMaxToasted(3);
	changePwdMessageBar.setMaxListed(7);
	
	var content = new com.sap.dna.ui5.ChangePassword({
		userName : user,
		userText : coreBundle.getText("lbl.username"),
		oldPasswordText : coreBundle.getText("lbl.pwdChg.passwordOld"),
		newPasswordText : coreBundle.getText("lbl.pwdChg.passwordNew"),
		newRepeatPasswordText : coreBundle.getText("lbl.pwdChg.passwordRep"),
		changePasswordButtonText : coreBundle.getText("btn.pwdChg.pwdChg"),
		messageBar : changePwdMessageBar,
		changePassword : changePassword
	});
	
	var panel = new sap.ui.commons.Panel({	width: "500px" });
	panel.setTitle(new sap.ui.commons.Title({text: coreBundle.getText("lbl.pwdChg.headPage")}));
	panel.addContent(content);	
	panel.placeAt("data");
	
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
});

/**
 * Event handler of the change password event
 * 
 * @param oEvent
 *            the event
 */
function changePassword(oEvent) {
	changePwdMessageBar.deleteAllMessages();

	// Prepare ajax call
	var url = getRestRoot("/changePassword/") + "/user/changePwd";
	var serializedData = "username=" + oEvent.getParameter("user")
			+ "&oldPassword=" + oEvent.getParameter("passwordOld")
			+ "&newPassword=" + oEvent.getParameter("passwordNew")
			+ "&newPasswordRepeat=" + oEvent.getParameter("passwordRepeat")
			+ "&locale=" + getLocale();
	showBusyIndicator();
	
	// post change password information
	$.ajax({
		url : url,
		data : serializedData,
		dataType : 'json',
		type : 'POST',
		success : function(data) {
			sap.ui.core.BusyIndicator.hide();
			// show success message
			var messageArray = new Array(1);
			messageArray[0] = new sap.ui.commons.Message({
				type : sap.ui.commons.MessageType.Success,
				text : coreBundle.getText("msg.pwdChg.success")
			});
			changePwdMessageBar.addMessages(messageArray);
		},
		error : function(data) {
			sap.ui.core.BusyIndicator.hide();
			// show error message
			var response = $.parseJSON(data.response);
			addServiceResponseToMessageBox(response,changePwdMessageBar);
		}
	});
}