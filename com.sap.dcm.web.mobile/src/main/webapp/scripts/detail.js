App.registerPage("detail", function () {
    var initialize = function (itemId) {
    	var page = this;
        var detailView = new App.view(page.node);
        var proxy = new EventProxy();
        
    };
    
    return {
        initialize: initialize
    };
});