//create the message bar of the login screen
$(document).ready(
		function() {
			messageBar = new sap.ui.commons.MessageBar('dcm-login-msgBar');
			messageBar.setMaxToasted(3);
			messageBar.setMaxListed(7);

			// create the message bar of the password change screen
			changePwdMessageBar = new sap.ui.commons.MessageBar(
					'dcm-login-changePwdMsgBar');
			changePwdMessageBar.setMaxToasted(3);
			changePwdMessageBar.setMaxListed(7);

			coreBundle = getBundle("../resources/core.properties");

			// position the login screen
			var oScreen = new com.sap.dna.ui5.LoginScreen({
				backgroundImage : "../images/loginBg.jpg",
				applicationLogo : "../images/SAP_new.png",
				title : coreBundle.getText("lbl.headApp"),
				subTitle : coreBundle.getText("lbl.headHana"),
				login : loginClicked,
				changePassword : changePasswordClicked,
				messageBar : messageBar,
				userText : coreBundle.getText("lbl.username"),
				passwordText : coreBundle.getText("lbl.password"),
				logonButtonText : coreBundle.getText("btn.logon"),
				changePasswordText : coreBundle.getText("lbl.pwdChg.headPage")
			});

			oScreen.placeAt("body");
			
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
 * function for the click event of the login button
 * 
 * @param oEvent
 *            the click event
 */
function loginClicked(oEvent) {
	var user = oEvent.getParameter("user");
	var password = oEvent.getParameter("password");

	// hide errors if active
	messageBar.deleteAllMessages();

	var serializedData = "username=" + user + "&password=" + password;
	var url = getRestRoot("/logon/logon.html") + "/login";
	// send post request to log in
	showBusyIndicator();
	$.ajax({
		url : url,
		data : serializedData,
		dataType : 'json',
		type : 'POST',
		success : function(data) {
			sap.ui.core.BusyIndicator.hide();
			handleLoginResponse(data);
		},
		error : function(data) {
			sap.ui.core.BusyIndicator.hide();
			var messageArray = new Array(1);
			messageArray[0] = new sap.ui.commons.Message({
				type : sap.ui.commons.MessageType.Error,
				text : coreBundle.getText("msg.fatalError")
			});
			messageBar.addMessages(messageArray);
		}
	});
}

/**
 * Handling of the response after login was triggered
 * 
 * @param data
 */
function handleLoginResponse(data) {
	if (data.loginStatus.loggedIn == true) {
		window.location.replace(data.loginStatus.redirectUrl);
	} else {
		// Error failed
		var messageArray = new Array(1);
		messageArray[0] = new sap.ui.commons.Message({
			type : sap.ui.commons.MessageType.Error,
			text : data.loginStatus.loginMessage
		});
		messageBar.addMessages(messageArray);
	}
}

/**
 * callback function for the change password click
 */
function changePasswordClicked() {
	// create the modal dialog
	var pwdChangeDialog = new sap.ui.commons.Dialog({
		modal : true,
		width : '380px'
	});
	pwdChangeDialog.setTitle(coreBundle.getText("lbl.pwdChg.headPage"));
	
	var user = this.oUserField.getValue();
	// position the change password control
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

	pwdChangeDialog.addContent(content);
	pwdChangeDialog.attachClosed( function() {
			changePwdMessageBar.deleteAllMessages();
		}
	);
	pwdChangeDialog.open();
}

/**
 * Event handler of the change password event
 * 
 * @param oEvent
 *            the event
 */
function changePassword(oEvent) {
	changePwdMessageBar.deleteAllMessages();

	// Prepare ajax call
	var url = getRestRoot("/logon/logon.html") + "/user/changePwd";
	var serializedData = "username=" + oEvent.getParameter("user")
			+ "&oldPassword=" + oEvent.getParameter("passwordOld")
			+ "&newPassword=" + oEvent.getParameter("passwordNew")
			+ "&newPasswordRepeat=" + oEvent.getParameter("passwordRepeat")
			+ "&locale=" + getLocale();

	// post change password information
	showBusyIndicator();
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