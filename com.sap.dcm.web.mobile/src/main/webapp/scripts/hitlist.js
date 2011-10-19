App.registerPage("hitlist", function () {
    var initialize = function () {
        var page = this;
        var listView = new App.View(page.node);
        var proxy = new EventProxy();
        
        //fetch data from the given url
        listView.fetchData = function (url) {
            $.getJSON("ajax/"+ url +".json", function (data) {
                proxy.trigger("data", data.results);
            });
        };
        listView.fetchData("hitlist");
        //fetch hitlist.tmpl here
        App.getTemplate("hitlist", function (template) {
            proxy.trigger("template:hitlist", template);
        });
        
        proxy.assign("data", "template:hitlist", function (data, template) {
            console.log(data);
            var html = _.template(template, {"navs": data});
            listView.$(".collection").html(html);
            var tabviewNode = listView.$(".collection").addClass("tab-panel");
            
            var modules = [];
            _.each(data, function (module) {
                module.context = page;
                modules.push(new App.TabModule(module));
            });
            var navTabView = new AppUI.TabView(tabviewNode, modules);
        });
        
        
        listView.bind("showDetail", function (event) {
            var target = $(event.currentTarget);
            var itemId = target.data("id");
            page.openView("detail/" + itemId, false);
        });
        
        listView.delegateEvents({
            "li.item": "showDetail"
        });
        
    };
    return {
        initialize: initialize
    };
});