App.registerPage("invoice", function () {
    var initialize = function () {
        var page = this;
        var scroll;
        var view = new App.View(page.node);
        var proxy = new EventProxy();

        proxy.assign("data", "template", function (data, template) {
            var html = _.template(template, data);
            view.$(".detail").html(html);
            scroll = new iScroll(view.$("article")[0], {
            onScrollEnd: function () {
                
                }
            });
        });

        App.getTemplate("invoice_detail", function (tmpl) {
            proxy.trigger("template", tmpl);
        });

        // TODO
        // $.getJSON("invoice_detail", function (data) {
            // proxy.trigger("data", data);
        // });
        
        proxy.trigger("data", {});
        
        view.bind("closeViewport", function (event) {
            page.closeViewport();
        });

        view.delegateEvents({
            "click header .action": "closeViewport"
        });
    };

    return {
        initialize: initialize
    }
});