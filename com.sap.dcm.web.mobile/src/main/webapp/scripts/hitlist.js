App.registerPage("hitlist", function () {
    var initialize = function () {
        var page = this;
        var listView = new App.View(page.node);
        var proxy = new EventProxy();
        
        //produce tab modules here
        proxy.assign("data", "template", function (data, template) {
            var modules = [];
            _.each(data, function (module) {
                var nav = data[module];
                modules.push(new AppUI.TabModule(nav));
            });
            proxy.assign("modules", modules);
            //TODO
            //use AppUI.TabView(node, modules) to create tab view and render it to $(listView.el).(".collections")
        });
        
        //fetch data from the given url

        listView.fetchData = function (url) {
            $.getJSON("ajax/"+ url +".json", function (data) {
                proxy.trigger("data", data);
            });
        };
        
        //fetch hitlist.tmpl here
        
        
        
    };
    return {
        initialize: initialize
    };
});