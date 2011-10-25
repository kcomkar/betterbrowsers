App.registerPage("note", function () {
    var initialize = function (itemId) {
        var page = this;
        var noteView = new App.View(page.node);
        var proxy = new EventProxy();
        
        var originalData;
        
        noteView.render = function(template, data) {
        	var html = _.template(template, {notes: data["notes"] || []});
            noteView.$(".notes_set").html(html);
            var iscroll = new iScroll(noteView.$(".notes_set")[0], {
                useTransform : false,
                onBeforeScrollStart : function (e) {
                    var target = e.target;
                    while (target.nodeType !== 1) {
                        target = target.parentNode;
                    }
                    if (target.tagName !== 'SELECT' && target.tagName !== 'INPUT'
                            && target.tagName !== 'TEXTAREA') {
                        e.preventDefault();
                    }
                },
                scrollbarClass : 'myScrollbar'
            });
        };

        proxy.assignAll("template", "data", function (template, data) {
            noteView.render(template, data);
         });
        //fetch the notes' data
        noteView.fetchData = function (url) {
           /* $.getJSON("ajax/"+ url +".json", function (data) {
               originalData = data.noteListResponse;
               proxy.trigger("data", originalData);
            }); */
     	
     	 $.ajax({
           	type:'GET',
           	url:'rest/mobile/collectionOverview/getCustomer/0000105511/notes',
           	dataType:'json',
           	success:function (data) {
           		originalData = data.noteListResponse;
           		 proxy.trigger("data", originalData);
           	}
               });
        };
        noteView.fetchData("notes");
        
        //fetch the template for notes
        App.getTemplate("note", function (template) {
            proxy.trigger("template", template);
        });

        var actionButton = noteView.$(".action");
        var noteSet = noteView.$(".notes_set");
        var noteAdd = noteView.$(".notes_add");
        noteView.bind("action", function () {
            /*
             * POST HEADER: content-type: application/x-www-form-urlencoded
             * BODY: contact=wangchenchang&text=aaaaaaaa
             * 
             * */
        	if (actionButton.hasClass("add")) {
        		// TODO
        		noteAdd.removeClass("hidden");
        		noteSet.addClass("hidden");
        		actionButton.removeClass("add").addClass("submit");
        	} else if (actionButton.hasClass("submit")) {
        		// TODO
        		$.ajax({
                	type:'POST',
                	url:'rest/mobile/collectionOverview/getCustomer/0000105511/notes',
                	data:{"contact":"wangchenchang","text":noteView.$("textarea").val() },
                	dataType:'json',
                	success:function (data) {
                		noteView.fetchData("notes");
                		noteAdd.addClass("hidden");
                		noteSet.removeClass("hidden");
                		actionButton.removeClass("submit").addClass("add");
                	},
                	error:function(data)
                	{
                		//todo;
                	}
                    });
        	}
        });
        
        noteView.bind("done", function () {
            page.closeViewport();
        });
        
        noteView.delegateEvents({
            "click .action": "action",
            "click .done": "done"
        });
        
        
        
    };
    return {
        initialize: initialize
    };
});