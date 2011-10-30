App.registerPage("preference", function () {
    var initialize = function () {
        var page = this;
        var prefView = new App.View(page.node);
        var proxy = new EventProxy();
        
        prefView.render = function (template, data) {
            var html = _.template(template, {preferences: data.preferences});
            prefView.$(".setting").html(html);
        };
        
        proxy.assignAll("data", "template", function (data, template) {
            prefView.render(template, data);
        });
        
        prefView.fetchData = function (url) {
            $.getJSON("rest/mobile/preferences", function (data) {
                var pref = App.Model.preference = data.preferencesResponse;
                pref.preferences.companyCodes = pref.preferences.companyCodes || [];
                proxy.trigger("data", pref);
            });
        };
        if (App.Model.preference) {
            proxy.trigger("data", App.Model.preference);
        } else {
        prefView.fetchData("preference");
        }
        
        App.getTemplate("settings", function (template) {
            proxy.trigger("template", template);
        });
        
        prefView.bind("showOptions", function (event) {
            var target = $(event.currentTarget);
            var type = target.data("type");
            page.openViewport(type);
        });
        
        prefView.bind("done", function (){
            var pref = App.Model.preference.preferences;
            var newCurrency = pref.currency;
            var newCompCode = pref.companyCodes;
            if (newCompCode.length === 0 ) {
                //invalid post
                var dialog = new AppUI.Dialog({className: "failed", modal: true}, {
                    "title": "Warning!",
                    "content": "No company codes were selected!",
                    "ok": "OK"
                }, function () {
                    dialog.close().destroy();
                }, AppUI.Dialog.messageTemplate);
                dialog.open();
            } else {
                var postData = "currency=" + newCurrency;
                _.each(newCompCode, function (code) {
                    postData = postData.concat("&companyCodes=" + code);
                });
                var saveDialog = new AppUI.Dialog({
                    className: "saving",
                    modal: true
                }, {
                    "loading": "Saving..."
                }, null, AppUI.Dialog.loadingTemplate);
                saveDialog.open();
                $.ajax({
                    type: 'POST',
                    url: 'rest/mobile/preferences',
                    data: postData,
                    dataType: "JSON",
                    success:function () {
                        saveDialog.close().destroy();
                        var hitlistPage = App._pages["hitlist"];
                        hitlistPage.postMessage("refresh", null);
                        page.closeViewport();
                    }
                });
            }
        });
        
        page.onMessage("changeValue", function (args) {
            var pref = App.Model.preference;
            var newValue = args.split("/");
            var type = newValue.shift();
            prefView.$(".record." + type +" .value").text(newValue.join(","));
            pref.preferences[type] = newValue;
            
        });
        prefView.delegateEvents({
            "click .reporting .record": "showOptions",
            "click .done": "done"
        });
    };
    return {
        initialize: initialize
    };
});
