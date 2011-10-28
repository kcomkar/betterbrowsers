App.registerPage("invoice", function () {
    var initialize = function (companyCode, customerId, invoiceId) {
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

        $.ajax({
            type: "GET",
            url: "rest/mobile/collectionOverview/getCustomer/" + companyCode + "/" + customerId +"/invoices/" + invoiceId,
            dataType: 'json',
            success: function (data) {
                data = data || {};
                var index = _.indexOf(App.Model.invoiceItems, invoiceId);
                data.invoiceId = invoiceId;
                data.previousId = App.Model.invoiceItems[index - 1];
                data.nextId = App.Model.invoiceItems[index + 1];
                data.invoiceDetail = data.invoiceDetail || null;
                proxy.trigger("data", data);
            },
            error: function(xhr, type) {
                // TODO error handling.
            }
        });

        view.bind("closeViewport", function (event) {
            page.closeViewport();
        });

        view.bind("forward", function (event) {
            var target = $(event.currentTarget);
            var forwardId = target.data("invoice_id");
            page.openView(["invoice", companyCode, customerId, forwardId].join("/"));
        });

        view.delegateEvents({
            "click header .action": "closeViewport",
            "click article .previous": "forward",
            "click article .next": "forward"
        });
    };

    return {
        initialize: initialize
    };
});