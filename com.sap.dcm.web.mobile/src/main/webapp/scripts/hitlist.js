App.registerPage("hitlist", function () {

    var initialize = function () {
        var page = this;
        var listView = new App.View(page.node);
        var proxy = new EventProxy();
        
        //fetch data from the given url
        listView.fetchData = function () {
            $.ajax({
                type:'GET',
                url:'rest/mobile/collectionOverview/getOverview',
                dataType:'json',
                success:function (data) {
                    proxy.trigger("data", data.collectionOverviewResponse);
                },
                error: function (msg) {
                    proxy.trigger("error", msg);
                }
            });

        };
        listView.fetchData();
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
            var tabs = ["dsoList", "openAmountList", "interestLossList"];
            _.each(tabs, function(prop) {
                var module = {};
                module.context = page;
                module.data = data[prop] || [];
                modules.push(new App.TabModule(module));
            });
            
            var navTabView = new AppUI.TabView(tabviewNode, modules);
        });
        
        proxy.assign("error", function (msg) {
            listView.$(".collection").html("Eror occurs!");
        });
        
        listView.bind("showDetail", function (event) {
            var target = $(event.currentTarget);
            var itemId = target.data("id");
            console.log("show detail--id=" + itemId);
            var itemCode = target.data("code");
            page.openView("detail/" + itemCode + "/" + itemId, false);
        });
        
        listView.bind("showPreference", function (event) {
            page.openViewport("preference");
        });
        
        listView.delegateEvents({
            "click li.item": "showDetail",
            "click .preference": "showPreference"
        });
        
    };
    
    var reappear = function () {
        var page = this;
        page.initialize();
    };
    
    return {
        initialize: initialize,
        reapear: reappear
    };
});