jQuery.sap.declare("com.sap.dcm.ilm.CustomerData");

// list of dependencies of this module
jQuery.sap.require("sap.ui.core.Core");
jQuery.sap.require("jquery.sap.resources");

// create the 'main' object of the module
com.sap.dcm.ilm.CustomerData = {
	content : undefined,
	customerInput : undefined,
	companyInput : undefined,
	serachMsgBar : undefined,
	delDate : undefined,
	delMsgBar : undefined,
	resultPanel : undefined,
	delPanel : undefined,
	getContent : function() {
		if (typeof this.content == "undefined") {
			this.content = this.init();
		}
		return this.content;
	},
	/**
	 * Function to clean up the module
	 */
	reset : function(){
		if (typeof this.customerInput != "undefined") {
			this.customerInput.setValue("");
		}
		if (typeof this.companyInput != "undefined") {
			this.companyInput.setValue("");
		}
		if (typeof this.delDate != "undefined") {
			this.delDate.setValue("");
		}
		if (typeof this.serachMsgBar != "undefined") {
			this.serachMsgBar.deleteAllMessages("");
		}
		if (typeof this.delMsgBar != "undefined") {
			this.delMsgBar.deleteAllMessages("");
		}
		if (typeof this.resultPanel != "undefined") {
			this.resultPanel.setVisible(false);
		}
		if (typeof this.delPanel != "undefined") {
			this.delPanel.setVisible(false);
		}
	},
	init : function() {
		// TODO user specific language
		var locale = sap.ui.getCore().getConfiguration().getLanguage();
		var ilmBundle = jQuery.sap.resources({
			url : "resources/com/sap/dcm/ilm/uiTexts.properties",
			locale : locale
		});

		var panel = new sap.ui.commons.Panel();
		panel.setTitle(new sap.ui.commons.Title({
			text : ilmBundle.getText("lbl.ilm.custData.headLine")
		}));
		panel.addContent(new sap.ui.commons.TextView({
			text : ilmBundle.getText("help.ilm.custdata")
		}));

		/* ------------------------------------------------- */
		/* Search Panel to search for cutomer data */
		/* ------------------------------------------------- */
		var searchPanel = new sap.ui.commons.Panel();
		searchPanel.setTitle(new sap.ui.commons.Title({
			text : coreBundle.getText("lbl.panel.search")
		}));

		var searchForm = new sap.ui.commons.layout.MatrixLayout({
			columns : 4,
			widths : [ '150px', '200px', '80px' ]
		});
		
		var customerInput = this.customerInput = new sap.ui.commons.TextField(
				"ilm-customerData-customerId");
		var customerLabel = new sap.ui.commons.Label({
			text : ilmBundle.getText("lbl.ilm.custData.custID"),
			labelFor : customerInput
		});
		searchForm.createRow(customerLabel, customerInput);

		var companyInput = this.companyInput = new sap.ui.commons.TextField(
				"ilm-customerData-companyId");
		var companyLabel = new sap.ui.commons.Label({
			text : ilmBundle.getText("lbl.ilm.custData.compID"),
			labelFor : companyInput
		});
		var searchButton = new sap.ui.commons.Button({
			text : coreBundle.getText("btn.search"),
			icon : "images/search.gif"
		});
		searchButton.attachPress(searchPersonalData);
		var searchMsgHtml = new sap.ui.core.HTML("html1", {
			content : "<span id='ilm-customerData-search-msg'></span>"
		});
		searchForm.createRow(companyLabel, companyInput, searchButton,
				searchMsgHtml);

		// create the search message box so that it can be used in button
		// execution
		var searchMessageBar = this.serachMsgBar = new sap.ui.commons.MessageBar(
				'ilm-customerData-search-msgBar');
		searchMessageBar.setMaxToasted(3);
		searchMessageBar.setMaxListed(7);
		searchMessageBar.setAnchorID("ilm-customerData-search-msg");
		searchMessageBar.setAnchorSnapPoint("begin top");

		searchPanel.addContent(searchForm);
		searchForm.attachBrowserEvent("keypress", function(event){
			if(event.keyCode=='13') searchPersonalData();
		});
		panel.addContent(searchPanel);

		/* ------------------------------------------------- */
		/* result panel */
		/* ------------------------------------------------- */
		// dunning Tab
		/* ------------------------------------------------- */
		// 1)the dunning table
		/* ------------------------------------------------- */
		var dunningForm = new sap.ui.commons.layout.MatrixLayout();
		var dunningTable = new sap.ui.table.DataTable(
				"ilm-customerData-dunningTable", {
					selectionMode : sap.ui.table.SelectionMode.Single
				});
		initTableColumns(dunningTable);
		tableModel = new sap.ui.model.json.JSONModel();
		dunningTable.setModel(tableModel);
		dunningTable.bindRows("dunningData");
		dunningTable.attachRowSelect(function(event) {
			handleDunningRowSelection(event);
		});
		dunningForm.createRow(dunningTable);

		/* ------------------------------------------------- */
		// 2) Details area
		/* ------------------------------------------------- */
		var detailsForm = new sap.ui.commons.layout.MatrixLayout({
			columns : 5,
			widths : [ '200px', '200px', '200px', '200px' ]
		});
		var detailsHeadLabel = new sap.ui.commons.Label();
		detailsHeadLabel.setText(coreBundle.getText("lbl.panel.details"));
		detailsHeadLabel.setDesign(sap.ui.commons.LabelDesign.Bold);
		detailsForm.createRow(detailsHeadLabel);

		var t_label_1 = new sap.ui.commons.Label();
		t_label_1.setText(ilmBundle.getText("tbl.dunning_invoices.system_id"));
		var systemIdDetailsField = new sap.ui.commons.TextField();
		systemIdDetailsField.setEditable(false);
		t_label_1.setLabelFor(systemIdDetailsField.getId());

		var t_label_2 = new sap.ui.commons.Label();
		t_label_2.setText(ilmBundle.getText("tbl.dunning_invoices.mandt"));
		var mandtDetailsField = new sap.ui.commons.TextField();
		mandtDetailsField.setEditable(false);
		t_label_2.setLabelFor(mandtDetailsField.getId());

		detailsForm.createRow(t_label_1, systemIdDetailsField, t_label_2,
				mandtDetailsField);

		t_label_1 = new sap.ui.commons.Label();
		t_label_1.setText(ilmBundle
				.getText("tbl.dunning_invoices.document_number"));
		var docNumDetailsField = new sap.ui.commons.TextField();
		docNumDetailsField.setEditable(false);
		t_label_1.setLabelFor(docNumDetailsField.getId());

		t_label_2 = new sap.ui.commons.Label();
		t_label_2
				.setText(ilmBundle.getText("tbl.dunning_invoices.fiscal_year"));
		var fiscalYearDetailsField = new sap.ui.commons.TextField();
		fiscalYearDetailsField.setEditable(false);
		t_label_2.setLabelFor(fiscalYearDetailsField.getId());

		detailsForm.createRow(t_label_1, docNumDetailsField, t_label_2,
				fiscalYearDetailsField);

		t_label_1 = new sap.ui.commons.Label();
		t_label_1.setText(ilmBundle.getText("tbl.dunning_invoices.line_item"));
		var lineItemDetailsField = new sap.ui.commons.TextField();
		lineItemDetailsField.setEditable(false);
		t_label_1.setLabelFor(lineItemDetailsField.getId());

		t_label_2 = new sap.ui.commons.Label();
		t_label_2.setText(ilmBundle
				.getText("tbl.dunning_invoices.dunning_level"));
		var dunLevelDetailsField = new sap.ui.commons.TextField();
		dunLevelDetailsField.setEditable(false);
		t_label_2.setLabelFor(dunLevelDetailsField.getId());

		detailsForm.createRow(t_label_1, lineItemDetailsField, t_label_2,
				dunLevelDetailsField);

		t_label_1 = new sap.ui.commons.Label();
		t_label_1.setText(ilmBundle
				.getText("tbl.dunning_invoices.invoice_number"));
		var invNumDetailsField = new sap.ui.commons.TextField();
		invNumDetailsField.setEditable(false);
		t_label_1.setLabelFor(invNumDetailsField.getId());

		t_label_2 = new sap.ui.commons.Label();
		t_label_2.setText(ilmBundle
				.getText("tbl.dunning_invoices.dunning_date"));
		var dunDateDetailsField = new sap.ui.commons.DatePicker();
		dunDateDetailsField.setEditable(false);
		t_label_2.setLabelFor(dunDateDetailsField.getId());

		detailsForm.createRow(t_label_1, invNumDetailsField, t_label_2,
				dunDateDetailsField);

		t_label_1 = new sap.ui.commons.Label();
		t_label_1.setText(ilmBundle
				.getText("tbl.dunning_invoices.document_date"));
		var docDateDetailsField = new sap.ui.commons.DatePicker();
		docDateDetailsField.setEditable(false);
		t_label_1.setLabelFor(docDateDetailsField.getId());

		t_label_2 = new sap.ui.commons.Label();
		t_label_2.setText(ilmBundle.getText("tbl.dunning_invoices.due_date"));
		var dueDateDetailsField = new sap.ui.commons.DatePicker();
		dueDateDetailsField.setEditable(false);
		t_label_2.setLabelFor(dueDateDetailsField.getId());

		detailsForm.createRow(t_label_1, docDateDetailsField, t_label_2,
				dueDateDetailsField);

		t_label_1 = new sap.ui.commons.Label();
		t_label_1.setText(ilmBundle.getText("tbl.dunning_invoices.currency"));
		var currencyDetailsField = new sap.ui.commons.TextField();
		currencyDetailsField.setEditable(false);
		t_label_1.setLabelFor(currencyDetailsField.getId());

		t_label_2 = new sap.ui.commons.Label();
		t_label_2.setText(ilmBundle.getText("tbl.dunning_invoices.amount"));
		var amountDetailsField = new sap.ui.commons.TextField();
		amountDetailsField.setEditable(false);
		t_label_2.setLabelFor(amountDetailsField.getId());

		detailsForm.createRow(t_label_1, currencyDetailsField, t_label_2,
				amountDetailsField);

		t_label_1 = new sap.ui.commons.Label();
		t_label_1.setText(ilmBundle
				.getText("tbl.dunning_invoices.dunning_clerk"));
		var dunClerckDetailsField = new sap.ui.commons.TextField();
		dunClerckDetailsField.setEditable(false);
		t_label_1.setLabelFor(dunClerckDetailsField.getId());

		t_label_2 = new sap.ui.commons.Label();
		t_label_2.setText(ilmBundle
				.getText("tbl.dunning_invoices.letter_issued"));
		var letterIssuedDetailsField = new sap.ui.commons.CheckBox();
		letterIssuedDetailsField.setEditable(false);
		t_label_2.setLabelFor(letterIssuedDetailsField.getId());

		detailsForm.createRow(t_label_1, dunClerckDetailsField, t_label_2,
				letterIssuedDetailsField);

		t_label_1 = new sap.ui.commons.Label();
		t_label_1.setText(ilmBundle.getText("tbl.dunning_invoices.user_name"));
		var userDetailsField = new sap.ui.commons.TextField();
		userDetailsField.setEditable(false);
		t_label_1.setLabelFor(userDetailsField.getId());

		t_label_2 = new sap.ui.commons.Label();
		t_label_2.setText(ilmBundle
				.getText("tbl.dunning_invoices.changed_datetime"));
		var lastChangeDetailsField = new sap.ui.commons.TextField();
		lastChangeDetailsField.setEditable(false);
		t_label_2.setLabelFor(lastChangeDetailsField.getId());

		detailsForm.createRow(t_label_1, userDetailsField, t_label_2,
				lastChangeDetailsField);
		dunningForm.createRow(detailsForm);

		/* ------------------------------------------------- */
		// 3)the delete form
		/* ------------------------------------------------- */
		var dunningDelForm = new sap.ui.commons.layout.MatrixLayout({
			columns : 5,
			widths : [ '200px', '200px', '100px', '50px' ]
		});
		var lastDunningDatePicker = this.delDate = new sap.ui.commons.DatePicker(
				"ilm-customerData-dun-dunDate");
		lastDunningDatePicker.setLocale(locale);
		var lastDunningDateLabel = new sap.ui.commons.Label({
			text : ilmBundle.getText("lbl.ilm.custData.delKeyDate"),
			labelFor : lastDunningDatePicker
		});
		var dunningDelButton = new sap.ui.commons.Button({
			text : coreBundle.getText("btn.delete"),
			icon : "images/delete.gif"
		});
		dunningDelButton.attachPress(triggerDeleteDunnings);

		var dunningDelMsgHtml = new sap.ui.core.HTML("html2", {
			content : "<span id='ilm-customerData-dun-del-msg'></span>"
		});

		dunningDelForm.createRow(lastDunningDateLabel, lastDunningDatePicker,
				dunningDelButton, dunningDelMsgHtml);
		
		var dunningMessageBar = this.delMsgBar = new sap.ui.commons.MessageBar(
				'ilm-customerData-dun-del-msgBar');
		dunningMessageBar.setMaxToasted(3);
		dunningMessageBar.setMaxListed(7);
		dunningMessageBar.setAnchorID("ilm-customerData-dun-del-msg");
		dunningMessageBar.setAnchorSnapPoint("begin top");

		// dunning del panel
		var dunningDelPanel = this.delPanel = new sap.ui.commons.Panel();
		dunningDelPanel.setTitle(new sap.ui.commons.Title({
			text : ilmBundle.getText("lbl.ilm.custData.delDunning")
		}));
		dunningDelPanel.addContent(new sap.ui.commons.TextView({
			text : ilmBundle.getText("help.ilm.custdata.del.dun")
		}));
		dunningDelPanel.addContent(dunningDelForm);
		dunningDelForm.attachBrowserEvent("keypress", function(event){
			if(event.keyCode=='13') triggerDeleteDunnings();
		});

		dunningDelPanel.setVisible(false);
		dunningForm.createRow(dunningDelPanel);

		var tabStrip = new sap.ui.commons.TabStrip();
		tabStrip.createTab(ilmBundle.getText("lbl.ilm.custData.resultDunning"),
				dunningForm);
		
		
		var resultPanel = this.resultPanel = new sap.ui.commons.Panel(
				"ilm-customerData-resultPanel");
		resultPanel.addContent(tabStrip);
		resultPanel.setVisible(false);
		panel.addContent(resultPanel);

		/* ------------------------------------------------- */
		/* Table Row selection binding */
		/* ------------------------------------------------- */

		/**
		 * Function to fill the details panel of dunnings
		 */
		function handleDunningRowSelection(e) {
			var ctx = e.getParameters().rowContext;
			if (ctx != '') {
				// get the model
				var model = dunningTable.getModel().getProperty(ctx);
				// the set content

				systemIdDetailsField.setValue(model.SYSTEM_ID);
				mandtDetailsField.setValue(model.MANDT);
				docNumDetailsField.setValue(model.DOCUMENT_NUMBER);
				fiscalYearDetailsField.setValue(model.FISCAL_YEAR);
				lineItemDetailsField.setValue(model.LINE_ITEM);
				dunLevelDetailsField.setValue(model.DUNNING_LEVEL);
				invNumDetailsField.setValue(model.INVOICE_NUMBER);
				dunDateDetailsField.setYyyymmdd(model.DUNNING_DATE.replace(
						/-/g, ""));
				docDateDetailsField.setYyyymmdd(model.DOCUMENT_DATE.replace(
						/-/g, ""));
				dueDateDetailsField.setYyyymmdd(model.DUE_DATE
						.replace(/-/g, ""));
				currencyDetailsField.setValue(model.CURRENCY);
				amountDetailsField.setValue(model.AMOUNT);
				dunClerckDetailsField.setValue(model.DUNNING_CLERK);
				if (model.LETTER_ISSUED == '1') {
					letterIssuedDetailsField.setChecked(true);
				} else {
					letterIssuedDetailsField.setChecked(false);
				}
				userDetailsField.setValue(model.USER_NAME);
				lastChangeDetailsField.setValue(model.CHANGED_DATETIME);
			}
		}

		/**
		 * Function to clear the dunning detail panel
		 */
		function clearDunningDetails() {
			systemIdDetailsField.setValue("");
			mandtDetailsField.setValue("");
			docNumDetailsField.setValue("");
			fiscalYearDetailsField.setValue("");
			lineItemDetailsField.setValue("");
			dunLevelDetailsField.setValue("");
			invNumDetailsField.setValue("");
			dunDateDetailsField.setValue("");
			docDateDetailsField.setValue("");
			dueDateDetailsField.setValue("");
			currencyDetailsField.setValue("");
			amountDetailsField.setValue("");
			dunClerckDetailsField.setValue("");
			letterIssuedDetailsField.setChecked(false);
			userDetailsField.setValue("");
			lastChangeDetailsField.setValue("");
		}

		/* ------------------------------------------------- */
		/* Button behavior binding */
		/* ------------------------------------------------- */
		/**
		 * callback function of the search button
		 */
		function searchPersonalData() {
			// set the title
			resultPanel.setTitle(new sap.ui.commons.Title({
				text : ilmBundle.getText("lbl.ilm.custData.resultHead")
						.replace("{0}", customerInput.getValue()).replace(
								"{1}", companyInput.getValue())
			}));

			// clear old messages
			searchMessageBar.deleteAllMessages();
			dunningMessageBar.deleteAllMessages();
			// execute the search
			executeSearch(customerInput.getValue(),companyInput.getValue());
		}

		/**
		 * Function to execute the search 
		 * @param customer the customer name
		 * @param company the company name
		 */
		function executeSearch(customer, company) {
			var serializedData = "customerId=" + customer + "&companyId=" + company;
			var url = getRestRoot("index.html") + "/ilm/customerData";
			// subit ajax call
			showBusyIndicator();
			$.ajax({
				url : url,
				data : serializedData,
				dataType : 'json',
				success : function(data) {
					sap.ui.core.BusyIndicator.hide();
					handleSearchResponse(data);
				},
				error : function(jqxhr) {
					sap.ui.core.BusyIndicator.hide();
					clearDunningDetails();
					resultPanel.setVisible(false);
					dunningDelPanel.setVisible(false);
					
					var response = undefined;
					if(jqxhr.response != null && typeof jqxhr.response != 'undefined' && typeof jqxhr.response != 'null')
						response = $.parseJSON(jqxhr.response);
					if(typeof response.customerDataResponse != "undefined"){
						addMessagesToTextBox(response.customerDataResponse.messages, searchMessageBar);
					}else{
						addMessagesToTextBox(null, searchMessageBar);
					}
					
				}
			});
		}

		/**
		 * Function to handle the response of the search
		 * 
		 * @param data
		 *            the search response.
		 */
		function handleSearchResponse(data) {
			var response = $.parseJSON(data.customerDataResponse.dunnings);
			if (response.count > 0) {
				// set the table data
				tableModel.setData({
					dunningData : response.data
				});

				dunningTable.setSelectedIndex(0);
				dunningTable.fireRowSelect({
					rowIndex : 0,
					rowContext : dunningTable.getContextByIndex(0)
				});
				// show content
				resultPanel.setVisible(true);
				if (data.customerDataResponse.deleteAllowed == true)
					dunningDelPanel.setVisible(true);
				else
					dunningDelPanel.setVisible(false);
				$("div.result").show();
			} else {
				// hide data a show message
				clearDunningDetails();
				resultPanel.setVisible(false);
				dunningDelPanel.setVisible(false);
				var messageArray = new Array(1);
				messageArray[0] = new sap.ui.commons.Message({
					type : sap.ui.commons.MessageType.Success,
					text : ilmBundle.getText("msg.ilm.custdata.noDataFound")
							.replace("{0}",
									$("#ilm-customerData-customerId").text())
							.replace("{1}",
									$("#ilm-customerData-companyId").text())
				});
				searchMessageBar.addMessages(messageArray);
			}
		}

		/**
		 * triggers the delete request (show message box)
		 */
		function triggerDeleteDunnings() {
			var msg = ilmBundle.getText("msg.ilm.custdata.deleteConfirmation");
			msg = msg.replace("{0}", customerInput.getValue());
			msg = msg.replace("{1}", companyInput.getValue());
			msg = msg.replace("{2}", lastDunningDatePicker.getValue());
			sap.ui.commons.MessageBox.confirm(msg, function(result) {
				if (result == true) {
					deleteDunnings();
				}
			}, ilmBundle.getText("lbl.ilm.custData.delDunning"));
		}

		/**
		 * execute the deletion of dunnings
		 */
		function deleteDunnings() {
			searchMessageBar.deleteAllMessages();
			dunningMessageBar.deleteAllMessages();

			var d = lastDunningDatePicker.getYyyymmdd();
			var date = formatDateToISO(d);
			var url = getRestRoot("index.html") + "/ilm/customerData/dunnings";
			var serializedData = "customerId=" + customerInput.getValue()
					+ "&companyId=" + companyInput.getValue()
					+ "&lastDunningDate=" + date;

			showBusyIndicator();
			$.ajax({
				type : "DELETE",
				url : url,
				data : serializedData,
				dataType : "json",
				success : function(data) {
					sap.ui.core.BusyIndicator.hide();
					handleDeleteResponse(data);
				},
				error : function(data) {
					sap.ui.core.BusyIndicator.hide();
					handleDeleteResponse(data);
				}
			});
		}

		/**
		 * callback function to handle the response of delete
		 */
		function handleDeleteResponse(data) {
			addServiceResponseToMessageBox(data, dunningMessageBar);
			executeSearch(customerInput.getValue(),companyInput.getValue());
		}

		/**
		 * Function to add the table columns to the data table
		 * 
		 * @param table =
		 *            the data table
		 */
		function initTableColumns(table) {
			var columns = [ "SYSTEM_ID", "MANDT", "DOCUMENT_NUMBER",
					"FISCAL_YEAR", "LINE_ITEM", "DUNNING_LEVEL",
					"INVOICE_NUMBER", "DUNNING_DATE", "CURRENCY", "AMOUNT",
					"LETTER_ISSUED" ];
			for ( var i = 0; i < columns.length; i++) {
				addColumn(table, columns[i]);
			}
		}

		/**
		 * function to add a column to the data table
		 * 
		 * @param table
		 *            the table
		 * @param columnName
		 *            the column name
		 */
		function addColumn(table, columnName) {
			var control = new sap.ui.commons.TextView().bindProperty("text",
					columnName);
			table.addColumn(new sap.ui.table.Column({
				label : new sap.ui.commons.Label({
					text : ilmBundle.getText("tbl.dunning_invoices."
							+ columnName.toLowerCase())
				}),
				template : control,
				sortProperty : columnName,
				filterProperty : columnName
			}));
		}

		return panel;
	}
};
