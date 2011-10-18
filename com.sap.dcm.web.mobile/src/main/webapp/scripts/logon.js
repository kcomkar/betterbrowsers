App.registerPage("logon", function () {
    var initialize = function (itemId) {
        var page = this;
        var logonView = new App.View(page.node);
        
        logonView.bind("logonclick", function (event) {
            var logonButton = $(event.currentTarget);
            if (logonButton.data("ajax") === "pending") {
                return;
            }
            var username = logonView.$("#username").val();
            var password = logonView.$("#password").val();
            if (username && password) {
                logonButton.data("ajax", "pending");
                $.post("rest/mobile/mobLoginService/login",
                		"username="+username+"&&password="+password,
                		function (data) {
                    logonButton.data("ajax", "complete");
                    if (data.loggedIn = "true") {
//                        location.hash = "hitlist";
                    	page.openView("hitlist/"+ username + "/" + password, false);
                    }
                    console.log(data); 
                }, "application/x-www-form-urlencoded");
            } else {
//                if(! dialog) {
//                    dialog = new Widget.Dialog();
//                }
//                dialog.show();
            }
            
        });
        
        logonView.delegateEvents({
            "click #logon_button": "logonclick",
        });
        
    };
    return {
        initialize: initialize
    };
});