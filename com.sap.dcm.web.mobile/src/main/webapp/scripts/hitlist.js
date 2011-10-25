App.registerPage("hitlist", function () {
    var initialize = function () {
        var page = this;
        var listView = new App.View(page.node);
        var proxy = new EventProxy();
        
        //fetch data from the given url
        listView.fetchData = function (url) {
          /*$.getJSON("ajax/"+ url +".json", function (data) {
                proxy.trigger("data", data.collectionOverviewResponse);
            });*/
        	 $.ajax({
             	type:'GET',
             	url:'rest/mobile/collectionOverview/getOverview',
             	dataType:'json',
             	success:function (data) {
                    proxy.trigger("data", data.collectionOverviewResponse);
             	}
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
            var tabs = ["dsoList", "openAmountList", "interestLossList"];
            _.each(tabs, function(prop) {
                var module = {};
                module.context = page;
                module.data = data[prop] || [];
                modules.push(new App.TabModule(module));
            });
            
            var navTabView = new AppUI.TabView(tabviewNode, modules);
        });
        
        
        listView.bind("showDetail", function (event) {
            var target = $(event.currentTarget);
            var itemId = target.data("id");
            console.log("show detail--id=" + itemId);
            page.openView("detail/" + itemId, false);
        });
        
        listView.delegateEvents({
            "click li.item": "showDetail"
        });
        
    };
    return {
        initialize: initialize
    };
});