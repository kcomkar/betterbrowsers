App.registerPage("detail", function () {
    var initialize = function (itemId) {
        var page = this;
        var detailView = new App.View(page.node);
        var indicator = detailView.$(".indicator");
        var proxy = new EventProxy();

        page.scroll = new iScroll(detailView.$("article")[0], {
            snap: true,
            momentum: false,
            hScrollbar: false,
            onScrollEnd: function () {
                indicator.find("li.active").removeClass("active");
                indicator.find("li:nth-child(" + (this.currPageX+1) + ")").addClass("active");
            }
        });

        $.getJSON("ajax/detail.json", function (data) {
            data.collectionDetailResponse.invoiceHeaders = data.collectionDetailResponse.invoiceHeaders || [];
            proxy.trigger("data", data.collectionDetailResponse);
        });

        // Render customer information
        proxy.assign("data", "template_customer_info", function (data, template) {
            var html = _.template(template, data);
            detailView.$(".customer_info").html(html);
        });

        App.getTemplate("customer_info", function (tmpl) {
            proxy.trigger("template_customer_info", tmpl);
        });

        // Render AR overview
        proxy.assign("data", "template_ar_overview", function (data, template) {
            var html = _.template(template, data);
            detailView.$(".overview").html(html);
        });

        App.getTemplate("ar_overview", function (tmpl) {
            proxy.trigger("template_ar_overview", tmpl);
        });

        // Render financial detail
        proxy.assign("data", "template_fin_detail", function (data, template) {
            var html = _.template(template, data);
            detailView.$(".financial").html(html);
            detailView.$(".ratio_indicator").css("paddingLeft", parseFloat(detailView.$(".ratio_value").text()) * 100 + "%");
        });

        App.getTemplate("financial_detail", function (tmpl) {
            proxy.trigger("template_fin_detail", tmpl);
        });

        // Render AR overview
        proxy.assign("data", "template_invoices", function (data, template) {
            var html = _.template(template, data);
            detailView.$(".invoices .invoices_scroller").html(html);
            var scroll = new iScroll(detailView.$(".invoices")[0], {
                hScrollbar: false,
                vScrollbar: false
            });
            scroll.refresh();
            
        });

        App.getTemplate("invoices", function (tmpl) {
            proxy.trigger("template_invoices", tmpl);
        });
    };

    return {
        initialize: initialize
    };
});