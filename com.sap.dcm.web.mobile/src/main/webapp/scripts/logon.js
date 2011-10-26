App.registerPage("logon", function () {
    var initialize = function (itemId) {
        var page = this;
        var logonView = new App.View(page.node);
        
        logonView.bind("logonclick", function (event) {
            var logonButton = $(event.currentTarget);
            if (logonButton.data("ajax") === "pending") {
                return;
            }
            logonView.$("#errormessage")[0].innerHTML= "<div class='loading'><div class='loading_animation'></div></div>";
            var username = logonView.$("#username").val();
            var password = logonView.$("#password").val();
            if (username && password) {
                logonButton.data("ajax", "pending");
                $.ajax({
                	type:'POST',
                	url:'rest/mobile/mobLoginService/login',
                	data:{"username":username,"password":password },
                	dataType:'json',
                	success:function (data) {
                        logonButton.data("ajax", "complete");
                        if (data.mobLoginStatus.loggedIn == true) {
                            	location.href = "index.html";
                            }
                            else{
                            	logonView.$("#errormessage")[0].innerText= data.mobLoginStatus.loginMessage;
                            }
                        console.log(data); 
                    }
                });
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
        initialize: initialize,
        enableL10N: true
    };
});