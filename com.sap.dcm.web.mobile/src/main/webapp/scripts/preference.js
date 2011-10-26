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
                App.preference = data.preferencesResponse;
                App.preference.preferences.companyCodes = App.preference.preferences.companyCodes || [];
                proxy.trigger("data", data.preferencesResponse);
            });
        };
        prefView.fetchData("preference");
        
        App.getTemplate("settings", function (template) {
            proxy.trigger("template", template);
        });
        
        prefView.bind("showOptions", function (event) {
            var target = $(event.currentTarget);
            var type = target.data("type");
            page.openViewport(type);
        });
        
        prefView.bind("done", function (){
            var pref = App.preference.preferences;
            var newCurrency = pref.currency;
            var newCompCode = pref.companyCodes;
            if (newCompCode.length === 0 ) {
                //TODO invalid post
            } else {
                var str1 = "currency=" + newCurrency;
                var str2="";
                _.each(newCompCode, function (code) {
                    str2 = str2.concat("&companyCodes=" + code);
                });
                var postData = str1 + str2;
                $.ajax({
                    type: 'POST',
                    url: 'rest/mobile/preferences',
                    data: postData,
//                    data: {"companyCodes": ["001", "002", "003"], "currency": "EUR"},
                    dataType: "JSON",
                    success:function () {
                        page.closeViewport();
                    }
                });
            }
        });
        
        page.onMessage("changeValue", function (args) {
            var pref = App.preference;
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
