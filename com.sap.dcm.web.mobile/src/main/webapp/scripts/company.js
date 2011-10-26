App.registerPage("company", function () {
    var initialize = function () {
        var page = this;
        var companyView = new App.View(page.node);
        var proxy = new EventProxy();
        
        
        proxy.bind("data", function (panel) {
            var module = new App.SelectModule(panel);
            module.apply(companyView, null);
        });
        //reform the server data
        companyView.reformData = function () {
            var preference = App.preference;
            var panel = {};
            panel.data = [];
            var originData = preference.companyCodeSearchHelp;
            _.each(originData, function (old) {
                var item = {};
                item.code = old.companyCode;
                item.value = old.companyCode + "-" + old.companyName;
                panel.data.push(item);
            });
            panel.selectItem = preference.preferences.companyCodes || [];
            panel.sType = "multiple";
            proxy.trigger("data", panel);
        };
        companyView.reformData();
        
        companyView.bind("closePanel", function () {
            var selectItem = companyView.$(".item.select");
            var value = '';
            _.each(selectItem, function (dom) {
                value = value.concat("/" + $(dom).data("code"));
            });
            var prefPage = App._pages["preference"];
            prefPage.postMessage("changeValue", "companyCodes" + value);
            page.closeViewport();
        });
        
        companyView.delegateEvents({
            "click .back": "closePanel",
            "click .item": "select"
        });
    };
    
    return {
        initialize: initialize
    };
});