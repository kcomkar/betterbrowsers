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

    };

    return {
        initialize: initialize
    };
});