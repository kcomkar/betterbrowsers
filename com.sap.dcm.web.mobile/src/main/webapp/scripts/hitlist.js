App.registerPage("hitlist", function () {

    var initialize = function () {
        var page = this;
        var listView = new App.View(page.node);
        var proxy = new EventProxy();
        
        proxy.assignAll("data:all", "template:hitlist", function (data, template) {
            var preference = data.preference, hitlist = data.hitlist; 
            var html = _.template(template, {"navs": hitlist});
            listView.$(".collection").html(html);
            var tabviewNode = listView.$(".collection").addClass("tab-panel");
            
            var modules = [];
            var tabs = ["dsoList", "openAmountList", "interestLossList"];
            _.each(tabs, function(prop) {
                var module = {};
                module.context = page;
                module.data = hitlist[prop] || [];
                _.each(module.data, function (item) {
                    item.unit = preference.preferences.currency;
                    if(prop === "dsoList") {
                        item.unit = "DAYS";
                    }
                });
                modules.push(new App.TabModule(module));
            });
            var navTabView = new AppUI.TabView(tabviewNode, modules);
        });
        
        //fetch hitlist.tmpl here
        App.getTemplate("hitlist", function (template) {
            proxy.trigger("template:hitlist", template);
        });

        //fetch data from the given url
        listView.fetchData = function (url, type, proxy) {
            $.ajax({
                type:'GET',
                url: url,
                dataType:'json',
                success:function (data) {
                    if (type === "hitlist") {
                        proxy.trigger("data:hitlist", data.collectionOverviewResponse);
                    } else {
                        App.Model.preference = data.preferencesResponse;
                        App.Model.preference.preferences.companyCodes = App.Model.preference.preferences.companyCodes || [];
                        proxy.trigger("data:preference", App.Model.preference);
                    }
                },
                error: function (msg) {
                    proxy.trigger("error", msg);
                }
            });
        };
            
        listView.fetchAllData = function () {
            var _proxy = new EventProxy();
            _proxy.assign("data:preference", "data:hitlist", function (preference, hitlist) {
                var data = {};
                data.preference = preference;
                data.hitlist = hitlist;
                proxy.trigger("data:all", data);
        });
            listView.fetchData('rest/mobile/collectionOverview/getOverview', "hitlist", _proxy);
            listView.fetchData("rest/mobile/preferences", "preference", _proxy);
        };
        listView.fetchAllData();
        
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
        
        page.onMessage("refresh", function (msg) {
            var html = "<div class='column_loading'><div class='loading_animation'></div></div>";
            listView.$(".collection").html(html);
            listView.fetchAllData();
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