App.registerPage("note", function () {
    var initialize = function (itemId) {
        var page = this;
        var noteView = new App.View(page.node);
        var proxy = new EventProxy();
        
        
        console.log(noteView.$("article"));
        
        //fetch the notes' data
        noteView.fetchData = function (url) {
            $.getJSON("ajax/"+ url +".json", function (data) {
               proxy.trigger("data", data);
            });
        };
        noteView.fetchData("notes");
        
        //fetch the template for notes
        App.getTemplate("note", function (template) {
            proxy.trigger("template", template);
        });
        
        proxy.assign("template", "data", function (template, data) {
           var html = _.template(template, {notes: data.results});
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
        });
        
        
    };
    return {
        initialize: initialize
    };
});