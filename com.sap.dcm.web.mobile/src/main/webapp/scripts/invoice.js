App.registerPage("invoice", function () {
    var initialize = function (customerId, invoceId) {
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

        proxy.assign("nodata", "template", function (data, template) {
            // TODO
        });

        App.getTemplate("invoice_detail", function (tmpl) {
            proxy.trigger("template", tmpl);
        });

        $.ajax({
            type: "GET",
            url: "rest/mobile/collectionOverview/getCustomer/"+ customerId +"/invoices/" + invoceId,
            dataType: 'json',
            success: function (data) {
                if (data) {
                    proxy.trigger("data", data);
                } else {
                    proxy.trigger("nodata");
                }
            },
            error: function(xhr, type) {
                // TODO error handling.
            }
        });

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