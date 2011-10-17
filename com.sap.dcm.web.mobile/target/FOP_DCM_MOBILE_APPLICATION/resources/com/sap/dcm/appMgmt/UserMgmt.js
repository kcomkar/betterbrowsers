jQuery.sap.declare("com.sap.dcm.appMgmt.UserMgmt");

// list of dependencies of this module
jQuery.sap.require("sap.ui.core.Core");
jQuery.sap.require("jquery.sap.resources");

// create the 'main' object of the module
com.sap.dcm.appMgmt.UserMgmt = {
	content : undefined,
	roleTable : undefined,
	roleTableModel : undefined,
	roleMessageBar : undefined,
	userTable : undefined, 
	userTableModel : undefined, 
	userMessageBar : undefined,
	appMgmtBundle :undefined,
	/**
	 * Function to get the content of the screen
	 * @returns
	 */
	getContent : function() {
		if (typeof this.content == "undefined") {
			this.content = this.init();
		}
		this.refresh();
		return this.content;
	},
	/**
	 * Function to clean up the module
	 */
	reset : function(){
		if(typeof this.userMessageBar != "undefined")
			this.userMessageBar.deleteAllMessages();
	},
	/**
	 * Function to initialize the screen
	 * @returns {sap.ui.commons.Panel}
	 */
	init : function() {
		var module = this;
		// TODO user specific language
		var locale = sap.ui.getCore().getConfiguration().getLanguage();
		var appMgmtBundle = this.appMgmtBundle = jQuery.sap.resources({
			url : "resources/com/sap/dcm/appMgmt/uiTexts.properties",
			locale : locale
		});

		var panel = new sap.ui.commons.Panel();
		panel.setTitle(new sap.ui.commons.Title({
			text : appMgmtBundle.getText("lbl.appMgmt.userMgmt.headLine")
		}));
		panel.addContent(new sap.ui.commons.TextView({
			text : appMgmtBundle.getText("help.appMgmt.userMgmt")
		}));

		/*
		 * User management
		 */
		var userMgmt = new sap.ui.commons.layout.MatrixLayout();
		var userToolBar = new sap.ui.commons.Toolbar();
		var addUserButton = new sap.ui.commons.Button({text: appMgmtBundle.getText("btn.appMgmt.userMgmt.create"), icon : "images/create.gif", press : function(){ module.handleUserCreateClicked(); }});
		userToolBar.addItem(addUserButton);
		
		var editUserButton = new sap.ui.commons.MenuButton({text: appMgmtBundle.getText("btn.appMgmt.userMgmt.edit"), icon : "images/edit.gif" });
		var editUserMenu = new sap.ui.commons.Menu();
		var delUserButton = new sap.ui.commons.MenuItem({text: appMgmtBundle.getText("btn.appMgmt.userMgmt.delete"), icon : "images/delete.gif" });
		delUserButton.attachSelect( function(){ module.deleteUser( ); });
		editUserMenu.addItem(delUserButton);
		var assignRoleButton = new sap.ui.commons.MenuItem({text: appMgmtBundle.getText("btn.appMgmt.userMgmt.assignRole"), icon : "images/role.gif"});
		assignRoleButton.attachSelect( function(){ module.assignRoles(); });
		editUserMenu.addItem(assignRoleButton);
		var resetPwdButton = new sap.ui.commons.MenuItem({text: appMgmtBundle.getText("btn.appMgmt.userMgmt.resetPwd"), icon : "images/changePassword.gif"});
		resetPwdButton.attachSelect( function(){ module.resetUserPassword(); });
		editUserMenu.addItem(resetPwdButton);
		editUserButton.setMenu(editUserMenu);
		userToolBar.addItem(editUserButton);
		
		var refreshButton = new sap.ui.commons.Button({text: appMgmtBundle.getText("btn.appMgmt.userMgmt.refresh"), icon : "images/refresh.gif", press : function(){ module.refresh(); }});
		userToolBar.addItem(refreshButton);
		var userMsgBoxHtml = new sap.ui.core.HTML({ content : "<span id='appMgmt-user-msgBox'></span>" });
		userToolBar.addItem(userMsgBoxHtml);
		userMgmt.createRow(userToolBar);

		var userTable = this.userTable = new sap.ui.table.DataTable( "appMgmt-userMgmt-userTable", { selectionMode : sap.ui.table.SelectionMode.Single });
		var userTableModel = this.userTableModel = new sap.ui.model.json.JSONModel();
		userTable.setModel(userTableModel);
		userTable.bindRows("userData");
		userTable.attachRowSelect(function(event) {
			handleRowSelection(event);
		});

		var userTableHeaders = [ "name", "lastSuccessfulConnect", "invalidConnectAttempts",
				"adminGivenPassword", "passwordChangeNeeded", "userDeativated" ];
		$.each(userTableHeaders, function(index, value) {
			if(value == "adminGivenPassword" || value == "passwordChangeNeeded" || value == "userDeativated")
				addCheckBoxColumn(userTable, value, "lbl.appMgmt.users.");
			else
				addTextViewColumn(userTable, value, "lbl.appMgmt.users.");
		});
			
		userMgmt.createRow(userTable);

		var userMessageBar = this.userMessageBar = new sap.ui.commons.MessageBar();
		userMessageBar.setMaxToasted(3);
		userMessageBar.setMaxListed(7);
		userMessageBar.setAnchorID("appMgmt-user-msgBox");
		userMessageBar.setAnchorSnapPoint("begin top");
		
		
		//details panel
		var detailsLayout = new sap.ui.commons.layout.MatrixLayout({ columns : 5,  widths : ['200px', '250px', '150px', '300px']});
		var detailsHeadLabel = new sap.ui.commons.Label();
		detailsHeadLabel.setText(coreBundle.getText("lbl.panel.details"));
		detailsHeadLabel.setDesign(sap.ui.commons.LabelDesign.Bold);
		detailsLayout.createRow(detailsHeadLabel);
		
		var nameDetailsField = new sap.ui.commons.TextField();
		nameDetailsField.setEditable(false);
		var assignedRolesDetailsList = new sap.ui.commons.ListBox({ width : '250px', height : '250px'});
		assignedRolesDetailsList.setEditable(false);
		var row = new sap.ui.commons.layout.MatrixLayoutRow();
		var cell = new sap.ui.commons.layout.MatrixLayoutCell();
		cell.addContent(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.name") }));
		row.addCell(cell);
		cell = new sap.ui.commons.layout.MatrixLayoutCell();
		cell.addContent(nameDetailsField);
		row.addCell(cell);
		cell = new sap.ui.commons.layout.MatrixLayoutCell({ rowSpan : 8, vAlign : sap.ui.commons.layout.VAlign.Top });
		cell.addContent(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.assignedroles") }));
		row.addCell(cell);
		cell = new sap.ui.commons.layout.MatrixLayoutCell({ rowSpan : 8, vAlign : sap.ui.commons.layout.VAlign.Top });
		cell.addContent(assignedRolesDetailsList);
		row.addCell(cell);
		detailsLayout.addRow(row);
			
		var lastSuccessfulConnectDetailsField = new sap.ui.commons.TextField();
		lastSuccessfulConnectDetailsField.setEditable(false);
		detailsLayout.createRow(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.lastsuccessfulconnect") }), lastSuccessfulConnectDetailsField);
		
		var invalidConnectAttemptsDetailsField = new sap.ui.commons.TextField();
		invalidConnectAttemptsDetailsField.setEditable(false);
		detailsLayout.createRow(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.invalidconnectattempts") }), invalidConnectAttemptsDetailsField);
		
		var adminGivenPasswordDetailsCheck = new sap.ui.commons.CheckBox();
		adminGivenPasswordDetailsCheck.setEditable(false);
		detailsLayout.createRow(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.admingivenpassword") }), adminGivenPasswordDetailsCheck);
		
		var passwordChangeNeededDetailsBox = new sap.ui.commons.CheckBox();
		passwordChangeNeededDetailsBox.setEditable(false);
		detailsLayout.createRow(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.passwordchangeneeded") }), passwordChangeNeededDetailsBox);
		
		var userDeativateddDetailsBox = new sap.ui.commons.CheckBox();
		userDeativateddDetailsBox.setEditable(false);
		detailsLayout.createRow(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.userdeativated") }), userDeativateddDetailsBox);
		
		var creatorDetailsField = new sap.ui.commons.TextField();
		creatorDetailsField.setEditable(false);
		detailsLayout.createRow(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.creator") }), creatorDetailsField);
		
		var createTimeDetailsField = new sap.ui.commons.TextField();
		createTimeDetailsField.setEditable(false);
		detailsLayout.createRow(new sap.ui.commons.Label({ text : this.appMgmtBundle.getText("lbl.appMgmt.users.createtime") }), createTimeDetailsField);
		userMgmt.createRow(detailsLayout);
		
		panel.addContent(userMgmt);

		/* ------------------------------------------------- */
		/* Table Row selection binding */
		/* ------------------------------------------------- */

		/**
		 * Function to fill the details panel of dunnings
		 */
		function handleRowSelection(e) {
			var ctx = e.getParameters().rowContext;
			if (ctx != null && typeof ctx != 'undefined') {
				//get the model
				var model = userTable.getModel().getProperty(ctx);
				
				//update all fields from the model
				nameDetailsField.setValue(model.name);
				creatorDetailsField.setValue(model.creator);
				createTimeDetailsField.setValue(model.createTime);
				lastSuccessfulConnectDetailsField.setValue(model.lastSuccessfulConnect);
				invalidConnectAttemptsDetailsField.setValue(model.invalidConnectAttempts);
				adminGivenPasswordDetailsCheck.setChecked(model.adminGivenPassword);
				passwordChangeNeededDetailsBox.setChecked(model.passwordChangeNeeded);
				userDeativateddDetailsBox.setChecked(model.userDeativated);
				
				// get all roles of the user and update the list of roles
				var url = getRestRoot("index.html") + "/appMgmt/assignedRoles";
				showBusyIndicator();
				$.ajax({
					url : url,
					data : "username="+model.name,
					dataType : 'json',
					success : function(data) {
						sap.ui.core.BusyIndicator.hide();
						var assignedRole = data.assignedRoles;
						if(assignedRole != null && typeof assignedRole != 'undefined' && assignedRole != ""){
							var items = [];
							if(assignedRole.roles instanceof Array){
								$.each(assignedRole.roles, function(index,value){ items.push( new sap.ui.core.ListItem({text : value.name})); } );
							}else{
								items.push( new sap.ui.core.ListItem({text : assignedRole.roles.name}));
							}
							assignedRolesDetailsList.setItems(items, true);
						}else{
							assignedRolesDetailsList.setItems( [ new sap.ui.core.ListItem({text : appMgmtBundle.getText("lbl.appMgmt.users.noRoles")}) ], true );
						}
					},
					error : function(jqxhr) {
						sap.ui.core.BusyIndicator.hide();
						assignedRolesDetailsList.setItems( [ new sap.ui.core.ListItem({text : appMgmtBundle.getText("lbl.appMgmt.users.assigndeRoleRetrieveFailed")}) ],true );
					}
				});
				
			}else{
				nameDetailsField.setValue("");
				creatorDetailsField.setValue("");
				createTimeDetailsField.setValue("");
				lastSuccessfulConnectDetailsField.setValue("");
				invalidConnectAttemptsDetailsField.setValue("");
				adminGivenPasswordDetailsCheck.setChecked(false);
				passwordChangeNeededDetailsBox.setChecked(false);
				userDeativateddDetailsBox.setChecked(false);
				assignedRolesDetailsList.destroyItems();
			}
		}

		/**
		 * Adds the message of the backend response to the javascript messages
		 * array
		 * 
		 * @param array =
		 *            the javascript messages array
		 * @param messageObj =
		 *            the backend message
		 */
		function addMessageToArray(array, messageObj) {
			if (messageObj.errorIndicator == true) {
				var msg = new sap.ui.commons.Message({
					type : sap.ui.commons.MessageType.Error,
					text : messageObj.messageText
				});
				array.push(msg);
			} else {
				var msg = new sap.ui.commons.Message({
					type : sap.ui.commons.MessageType.Success,
					text : messageObj.messageText
				});
				array.push(msg);
			}
		}

		/**
		 * function to add a text view column to the data table
		 * 
		 * @param table
		 *            the table
		 * @param columnName
		 *            the column name
		 * @param uiTextPrefix
		 *            ui text prefix
		 */
		function addTextViewColumn(table, columnName, uiTextPrefix) {
			var control = new sap.ui.commons.TextView().bindProperty("text",
					columnName);
			table.addColumn(new sap.ui.table.Column({
				label : new sap.ui.commons.Label({
					text : appMgmtBundle.getText(uiTextPrefix
							+ columnName.toLowerCase())
				}),
				template : control,
				sortProperty : columnName,
				filterProperty : columnName
			}));
		}
		
		/**
		 * function to add a checkbox column to the data table
		 * 
		 * @param table
		 *            the table
		 * @param columnName
		 *            the column name
		 * @param uiTextPrefix
		 *            ui text prefix
		 */
		function addCheckBoxColumn(table, columnName, uiTextPrefix) {
			var control = new sap.ui.commons.CheckBox().bindProperty("checked",
					columnName);
			table.addColumn(new sap.ui.table.Column({
				label : new sap.ui.commons.Label({
				text  : appMgmtBundle.getText(uiTextPrefix
							+ columnName.toLowerCase())
				}),
				template : control,
				sortProperty : columnName,
				filterProperty : columnName,
				hAlign: "Center"
			}));
		}
		return panel;
	},
	/**
	 * Function to refresh the screen
	 */
	refresh : function(){
		this.refreshUserTable();
		this.userMessageBar.deleteAllMessages();
	},
	/**
	 * Function to refresh user table content
	 */
	refreshUserTable : function() {
		if(typeof this.userTable != "undefined" && typeof this.userTableModel != "undefined" && typeof this.userMessageBar != "undefined"){
			// prepare url
			var url = getRestRoot("index.html") + "/appMgmt/users";
	
			var that = this;
			showBusyIndicator();
			$.ajax({
				url : url,
				dataType : 'json',
				success : function(data) {
					sap.ui.core.BusyIndicator.hide();
					if (data.users.length > 0) {
						// set the table data
						that.userTableModel.setData({
							userData : data.users
						});
	
						that.userTable.setSelectedIndex(0);
						that.userTable.fireRowSelect({
							rowIndex : 0,
							rowContext : that.userTable.getContextByIndex(0)
						});
					} else {
						var messageArray = new Array(1);
						messageArray[0] = new sap.ui.commons.Message({
							type : sap.ui.commons.MessageType.Success,
							text : that.appMgmtBundle
									.getText("msg.userMgmt.noUsersFound")
						});
						that.userMessageBar.addMessages(messageArray);
					}
				},
				error : function(jqxhr) {
					sap.ui.core.BusyIndicator.hide();
					var messageArray = new Array(1);
					messageArray[0] = new sap.ui.commons.Message({
						type : sap.ui.commons.MessageType.Error,
						text : coreBundle.getText("msg.fatalError")
					});
					that.userMessageBar.addMessages(messageArray);
				}
			});
		}
	},
	/**
	 * Function to open the create user dialog
	 */
	handleUserCreateClicked : function (){	
		var baseUrl = getRestRoot("index.html");
					
		var createUserDialog = new sap.ui.commons.Dialog();
		createUserDialog.setTitle(this.appMgmtBundle.getText("lbl.appMgmt.userMgmt.create.head"));
			        
		var layout = new sap.ui.commons.layout.MatrixLayout({width:"350px", columns : 2, widths : [ '150px', '200px' ]});
				        
		var usernameFieldLabel = new sap.ui.commons.Label({text: coreBundle.getText("lbl.username")});
		var usernameField = new sap.ui.commons.TextField();
		usernameFieldLabel.setLabelFor(usernameField);
		layout.createRow(usernameFieldLabel,usernameField);
				        
		var passwordFieldLabel = new sap.ui.commons.Label({text: coreBundle.getText("lbl.password")});
		var passwordField = new sap.ui.commons.PasswordField();
		passwordFieldLabel.setLabelFor(passwordField);
		layout.createRow(passwordFieldLabel,passwordField);
				        
		var passwordRepeatFieldLabel = new sap.ui.commons.Label({text: coreBundle.getText("lbl.pwdChg.passwordRep")});
		var passwordRepeatField = new sap.ui.commons.PasswordField();
		passwordRepeatFieldLabel.setLabelFor(passwordRepeatField);
		layout.createRow(passwordRepeatFieldLabel,passwordRepeatField);
		
		var createMsgHtml = new sap.ui.core.HTML({	content : "<span id='appMgmt-user-create-msg'></span>" });
		layout.createRow(createMsgHtml);
		 
		var createMessageBar = new sap.ui.commons.MessageBar();
		createMessageBar.setMaxToasted(3);
		createMessageBar.setMaxListed(7);
		createMessageBar.setAnchorID("appMgmt-user-create-msg");
		createMessageBar.setAnchorSnapPoint("begin top");
		 
		var createButton = new sap.ui.commons.Button({text : coreBundle.getText("btn.create"), press : function(){ create(false); } });
		var createCloseButton = new sap.ui.commons.Button({text : coreBundle.getText("btn.createAndClose"), press : function(){
			create(true);
		} });
		createUserDialog.addButton(createButton);
		createUserDialog.addButton(createCloseButton);
		 
		createUserDialog.attachClosed( function(){
			createMessageBar.deleteAllMessages();
		});
				        
		createUserDialog.addContent(layout);
		layout.attachBrowserEvent("keypress", function(event){
			if(event.keyCode=='13') create(false);
		});
		
		createUserDialog.open();
		
		/**
		 * fucntion to create a new user
		 * @param closeAfterSuccess true if the dialog should be closed after successful create or false if not
		 */
		function create(closeAfterSuccess){
			var formParams = { username: usernameField.getValue(), password: passwordField.getValue(), passwordRepeat: passwordRepeatField.getValue()};
			showBusyIndicator();
			$.ajax({
				type : "POST",
				url : baseUrl + '/appMgmt/createUser',
				data : formParams,
				dataType : 'json',
				success : function( data ){	
					sap.ui.core.BusyIndicator.hide();
					var msg = com.sap.dcm.appMgmt.UserMgmt.appMgmtBundle.getText("msg.userMgmt.create.success");
					msg = msg.replace("{0}", usernameField.getValue());
					var msgArray = [ new sap.ui.commons.Message({ type : sap.ui.commons.MessageType.Success, text : msg }) ];
					
					createMessageBar.deleteAllMessages();
					usernameField.setValue("");
					passwordField.setValue("");
					passwordRepeatField.setValue("");
					if(closeAfterSuccess == true){
						com.sap.dcm.appMgmt.UserMgmt.userMessageBar.addMessages(msgArray);
						createUserDialog.close();
					}else{
						createMessageBar.addMessages(msgArray);
					}
					com.sap.dcm.appMgmt.UserMgmt.refreshUserTable();
				},
				error : function( jqxhr ){
					sap.ui.core.BusyIndicator.hide();
					createMessageBar.deleteAllMessages();
					var response = $.parseJSON(jqxhr.response);
					addServiceResponseToMessageBox( response, createMessageBar );
				}
						
			 });
		}
	},
	/**
	 * Function to delete a user
	 */
	deleteUser : function(){
		if( typeof this.userTable != 'undefined' && typeof this.userMessageBar != 'undefined'){
			//get the name of the selected user
			var user = this.getSelectedUser();
			if( user != null && typeof user != "undefined" ){
				var msg = this.appMgmtBundle.getText("msg.userMgmt.delete.conf");
				msg = msg.replace("{0}", user.name);
				sap.ui.commons.MessageBox.confirm(msg, function(result) {
					if (result == true) {
						com.sap.dcm.appMgmt.UserMgmt.userMessageBar.deleteAllMessages();
						var baseUrl = getRestRoot("index.html");
						var formParams = { username: user.name };
						showBusyIndicator();
						$.ajax({
							type : "DELETE",
							url : baseUrl + '/appMgmt/user',
							data : formParams,
							dataType : 'json',
							success : function( data ){
								sap.ui.core.BusyIndicator.hide();
								com.sap.dcm.appMgmt.UserMgmt.refreshUserTable();
								var msg = com.sap.dcm.appMgmt.UserMgmt.appMgmtBundle.getText("msg.userMgmt.delete.success");
								msg = msg.replace("{0}", user.name);
								var msgArray = [ new sap.ui.commons.Message({ type : sap.ui.commons.MessageType.Success, text : msg }) ];
								com.sap.dcm.appMgmt.UserMgmt.userMessageBar.addMessages(msgArray);
							},
							error : function( jqxhr ){
								sap.ui.core.BusyIndicator.hide();
								var response = $.parseJSON(jqxhr.response);
								addServiceResponseToMessageBox( response, com.sap.dcm.appMgmt.UserMgmt.userMessageBar );
							}
									
						 });
					}
				}, this.appMgmtBundle.getText("lbl.appMGmt.userMgmt.delUser.head"));
			}
		}
	},
	/**
	 * Function to get the current selected user
	 * @returns
	 */
	getSelectedUser : function(){
		if(typeof this.userTable != 'undefined'){
			var index = this.userTable.getSelectedIndex( );
			if( index >= 0 ){
				var ctx = this.userTable.getContextByIndex(index);
				return this.userTable.getModel().getProperty(ctx);;
			}
		}
	},
	/**
	 * Function to reset the password of the current selected user
	 */
	resetUserPassword : function (){	
		var user = this.getSelectedUser();
		if(user != null && typeof user != 'undefined'){
			var baseUrl = getRestRoot("index.html");
						
			var createResetPwdDialog = new sap.ui.commons.Dialog();
			createResetPwdDialog.setTitle(this.appMgmtBundle.getText("lbl.appMGmt.userMgmt.resetPwd.head"));
				        
			var layout = new sap.ui.commons.layout.MatrixLayout({width:"350px", columns : 2, widths : [ '150px', '200px' ]});
			var msg = this.appMgmtBundle.getText("help.appMGmt.userMgmt.resetPwd");
			msg = msg.replace("{0}", user.name);
			var resetText = new sap.ui.commons.TextView({text : msg});
			createResetPwdDialog.addContent(resetText);
					        
			var passwordFieldLabel = new sap.ui.commons.Label({text: coreBundle.getText("lbl.password")});
			var passwordField = new sap.ui.commons.PasswordField();
			passwordFieldLabel.setLabelFor(passwordField);
			layout.createRow(passwordFieldLabel,passwordField);
					        
			var passwordRepeatFieldLabel = new sap.ui.commons.Label({text: coreBundle.getText("lbl.pwdChg.passwordRep")});
			var passwordRepeatField = new sap.ui.commons.PasswordField();
			passwordRepeatFieldLabel.setLabelFor(passwordRepeatField);
			layout.createRow(passwordRepeatFieldLabel,passwordRepeatField);
			
			var resetPwdMsgHtml = new sap.ui.core.HTML({	content : "<span id='appMgmt-user-resetPwd-msg'></span>" });
			layout.createRow(resetPwdMsgHtml);
			 
			var resetPwdMessageBar = new sap.ui.commons.MessageBar();
			resetPwdMessageBar.setMaxToasted(3);
			resetPwdMessageBar.setMaxListed(7);
			resetPwdMessageBar.setAnchorID("appMgmt-user-resetPwd-msg");
			resetPwdMessageBar.setAnchorSnapPoint("begin top");
			 
			var resetButton = new sap.ui.commons.Button({text : this.appMgmtBundle.getText("btn.appMgmt.userMgmt.resetPwd.exec"), press : function(){ resetPwd(); } });
			createResetPwdDialog.addButton(resetButton);
			 
			createResetPwdDialog.attachClosed( function(){
				resetPwdMessageBar.deleteAllMessages();
			});
					        
			createResetPwdDialog.addContent(layout);
			layout.attachBrowserEvent("keypress", function(event){
				if(event.keyCode=='13') resetPwd();
			});
			createResetPwdDialog.open();
			/**
			 * Function to reset the user password
			 */			
			function resetPwd(){
				var formParams = { username: user.name, password: passwordField.getValue(), passwordRepeat: passwordRepeatField.getValue()};
				showBusyIndicator();
				$.ajax({
					type : "POST",
					url : baseUrl + '/appMgmt/restPassword',
					data : formParams,
					dataType : 'json',
					success : function( data ){	
						sap.ui.core.BusyIndicator.hide();
						var msg = com.sap.dcm.appMgmt.UserMgmt.appMgmtBundle.getText("msg.userMgmt.resetPwd.success");
						msg = msg.replace("{0}", user.name);
						var msgArray = [ new sap.ui.commons.Message({ type : sap.ui.commons.MessageType.Success, text : msg }) ];
						
						resetPwdMessageBar.deleteAllMessages();
						passwordField.setValue("");
						passwordRepeatField.setValue("");
						resetPwdMessageBar.addMessages(msgArray);
						com.sap.dcm.appMgmt.UserMgmt.refreshUserTable();
					},
					error : function( jqxhr ){
						sap.ui.core.BusyIndicator.hide();
						resetPwdMessageBar.deleteAllMessages();
						var response = $.parseJSON(jqxhr.response);
						addServiceResponseToMessageBox( response, resetPwdMessageBar );
					}
							
				 });
			}
		}
	},
	/**
	 * Function to assign roles to a certain user
	 */
	assignRoles : function (){
		var module = this;
		var user = this.getSelectedUser();
		
		if(user != null && typeof user != 'undefined'){
			var baseUrl = getRestRoot("index.html");
						
			var assignDialog = new sap.ui.commons.Dialog({width : "450px"});
			assignDialog.setTitle(this.appMgmtBundle.getText("lbl.appMGmt.userMgmt.assignRoles.head").replace("{0}", user.name));
			
			var help = new sap.ui.commons.TextView({text : this.appMgmtBundle.getText("help.appMGmt.userMgmt.roleAssignment")});
			assignDialog.addContent(help);
			
			var roleAssignmentTable = new sap.ui.table.DataTable({ selectionMode : sap.ui.table.SelectionMode.Single, visibleRowCount : 5 });
			var roleAssignmentModel = new sap.ui.model.json.JSONModel();
			roleAssignmentTable.setModel(roleAssignmentModel);
			roleAssignmentTable.bindRows("assignment");
			roleAssignmentTable.attachRowSelect(function(event) {
				handleRowSelection(event);
			});
			var checkedColumn = new sap.ui.commons.CheckBox().bindProperty("checked", "assigned");
			roleAssignmentTable.addColumn(new sap.ui.table.Column({
				label : new sap.ui.commons.Label({ text  : this.appMgmtBundle.getText("lbl.appMgmt.users.roleAssign.assigned") }),
				template : checkedColumn,
				sortProperty : "assigned",
				filterProperty : "assigned",
				hAlign: "Center"
			}));
			
			var roleColumn = new sap.ui.commons.Label().bindProperty("text", "role/name");
			roleAssignmentTable.addColumn(new sap.ui.table.Column({
				label : new sap.ui.commons.Label({ text  : this.appMgmtBundle.getText("lbl.appMgmt.users.roleAssign.role") }),
				template : roleColumn,
				sortProperty : "role/name",
				filterProperty : "role/name",
			}));
			roleAssignmentTable.setEditable(true);
				
			assignDialog.addContent(roleAssignmentTable);
			
			var msgHtml = new sap.ui.core.HTML({	content : "<span id='appMgmt-user-assignRole-msg'></span>" });
			assignDialog.addContent(msgHtml);
			 
			var msgBar = new sap.ui.commons.MessageBar();
			msgBar.setMaxToasted(3);
			msgBar.setMaxListed(7);
			msgBar.setAnchorID("appMgmt-user-assignRole-msg");
			msgBar.setAnchorSnapPoint("begin top");
			 
			var save = new sap.ui.commons.Button({text : coreBundle.getText("btn.saveClose"), press : function(){ assign(); } });
			assignDialog.addButton(save);
			var cancel = new sap.ui.commons.Button({text : coreBundle.getText("btn.cancel"), press : function(){ assignDialog.close(); } });
			assignDialog.addButton(cancel);
			 
			assignDialog.attachClosed( function(){
				msgBar.deleteAllMessages();
			});
					        
			assignDialog.open();
			
			/**
			 * Function to load the users of a certain user
			 */
			function loadData(){
				showBusyIndicator();
				$.ajax({
					url : baseUrl + '/appMgmt/roleAssignments',
					data : "username="+user.name,
					dataType : 'json',
					success : function( data ){		
						sap.ui.core.BusyIndicator.hide();
						msgBar.deleteAllMessages();
						roleAssignmentModel.setData( { assignment : data.roleassignments });
					},
					error : function( jqxhr ){
						sap.ui.core.BusyIndicator.hide();
						msgBar.deleteAllMessages();
						var response = $.parseJSON(jqxhr.response);
						addServiceResponseToMessageBox( response, msgBar );
					}
							
				 });
			}
			
			/**
			 * Function to execute the role assignment
			 */
			function assign(){
				
				var obj = { userRole : {
					user : user,
					rolesAssignments : roleAssignmentModel.getProperty("assignment")
				}};
				
				var data = serializeJSON(obj);
				showBusyIndicator();
				$.ajax({
					url : baseUrl + '/appMgmt/assignRoles',
					type:"POST",
					data: data,
					contentType:"application/json; charset=utf-8",
					dataType:"json",
					success : function( data ){	
						sap.ui.core.BusyIndicator.hide();
						msgBar.deleteAllMessages();
						var msg = com.sap.dcm.appMgmt.UserMgmt.appMgmtBundle.getText("msg.appMgmt.users.roleAssign.success");
						msg = msg.replace("{0}", user.name);
						var msgArray = [ new sap.ui.commons.Message({ type : sap.ui.commons.MessageType.Success, text : msg }) ];
						module.userMessageBar.addMessages(msgArray);
						module.refreshUserTable();
						assignDialog.close();
					},
					error : function( jqxhr ){
						sap.ui.core.BusyIndicator.hide();
						msgBar.deleteAllMessages();
						var response = $.parseJSON(jqxhr.response);
						addServiceResponseToMessageBox( response, msgBar );
					}
							
				 });
			}
			
			loadData();
		}
	}
};
