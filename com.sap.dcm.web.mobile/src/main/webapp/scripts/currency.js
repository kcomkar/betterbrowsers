App.registerPage("currency", function () {
    var initialize = function () {
        var page = this;
        var currencyView = new App.View(page.node);
        var proxy = new EventProxy();
        
        
        proxy.bind("data", function (data) {
            var module = new App.SelectModule(data);
            module.apply(currencyView, null);
        });
        //reform the server data
        var reformData = function () {
        	var preference = App.Model.preference;
            var panel = {};
            var originData = preference.currencySearchHelp;
            panel.data = [];
            _.each(originData, function (val, prop) {
                var item = {};
                item.value = val;
                item.code = val;
                panel.data.push(item);
            });
            panel.selectItem = preference.preferences.currency;
            if (!(panel.selectItem instanceof Array)) {
                panel.selectItem = panel.selectItem.split(",");
            }
            panel.sType = "single";
            proxy.trigger("data", panel);
        };
        reformData();
        
        currencyView.bind("closePanel", function () {
            var selectItem = currencyView.$(".item.select");
            var value = selectItem.text().trim();
            var prefPage = App._pages["preference"];
            prefPage.postMessage("changeValue", "currency/" + value);
            page.closeViewport();
        });
        
       currencyView.delegateEvents({
            "click .back": "closePanel",
            "click .item": "select"
        });
    };
    
    return {
        initialize: initialize
    };
});