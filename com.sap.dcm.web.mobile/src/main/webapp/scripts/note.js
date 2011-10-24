App.registerPage("note", function () {
    var initialize = function (itemId) {
        var page = this;
        var noteView = new App.View(page.node);
        var proxy = new EventProxy();
        
        var originalData;
        //fetch the notes' data
        noteView.fetchData = function (url) {
            $.getJSON("ajax/"+ url +".json", function (data) {
               originalData = data.noteListResponse;
               proxy.trigger("data", originalData);
            });
        };
        noteView.fetchData("notes");
        
        //fetch the template for notes
        App.getTemplate("note", function (template) {
            proxy.trigger("template", template);
        });
        
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
        
        noteView.bind("add", function () {
            /*
             * POST HEADER: content-type: application/x-www-form-urlencoded
             * BODY: contact=wangchenchang&text=aaaaaaaa
             * 
             * */
        });
        
        noteView.bind("done", function () {
            page.closeViewport();
        });
        
        noteView.delegateEvents({
            "click .add": "add",
            "click .done": "done"
        });
        
        
        
    };
    return {
        initialize: initialize
    };
});