/*global App, AppUI, Base64, Url, _, $, Backbone, OData, iScroll, window */
App.TabModule = function (nav) {
    return function () {
        var tab = this;
        console.log(tab);
        tab.bind("initialize", function () {
            var util = App.util;
            var proxy = new EventProxy();
            proxy.assign("template", "items", function (template, items) {
                var html = _.template(template, {"data": items});
                tab.panel.html(html);
                var iscroll = new iScroll(tab.panel[0], {
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

            // Fetch table template
            var widget = nav.ui_widget || "collection";
            App.getTemplate(widget.toLowerCase(), function (template) {
                proxy.trigger("template", template);
            });

            proxy.bind("items", function (data) {
                nav.originalData = data;
            });

            proxy.once("error", function () {
                tab.panel.empty();
                var resources = {content: nav.context.resources.netError, ok: nav.context.resources.ok};
                var dialog = new AppUI.Dialog({className: "alert", modal: true}, resources, function () {
                    dialog.close().destroy();
                }, AppUI.Dialog.noticeTemplate);
                dialog.open();
            });

            if (nav.data) {
                proxy.trigger("items", nav.data);
            }
            
        });
        tab.bind("active", function () {
            console.log("active");
        });
    };
};

App.SelectModule = function (panel) {
    return function () {
        var view = this;
        var proxy = new EventProxy();
        proxy.assign("template", "data", function (template, data) {
            var html = _.template(template, {options: data});
            view.$(".scroll_wrapper").html(html);
            var iscroll = new iScroll(view.$(".scroll_wrapper")[0], {
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
        
        App.getTemplate("select", function (template) {
            proxy.trigger("template", template);
        });
        
        panel.data = panel.data || [];
        //select type: "single", "multiple"
        panel.sType = panel.sType || "single";
        proxy.trigger("data", panel);
        
        view.bind("select", function (event) {
            var target = $(event.currentTarget);
            if (panel.sType === "multiple") {
                if (target.hasClass("select")) {
                    target.removeClass("select");
                } else {
                   target.addClass("select");
                }
            } else {
                if (target.hasClass("select")) {
                    //do nothing
                } else {
                    var previous = view.$(".item.select");
                    previous.removeClass("select");
                    target.addClass("select");
                }
            };
        });
        
    };
};

App.format = function (value, converter, formatType) {
    if (converter) {
        var c = converter.split('.'),
            f = window[c.shift()],
            i;
        for (i in c) {
            f = f[c[i]];
        }

        if (f && typeof f["format"] === "function") {
            try {
                return f["format"](value, formatType);
            } catch (e) {
                console.log("format value:" + value + "  " + e.name + ": " + e.message);
                return value;
            }
        } else {
            return value;
        }
    } else {
        return value; 
    }
};

/**
 * @return 
 *        result - converted value.
 *        message - validation failed reason.
 *        code - "0" convert sucessfully, "1" validation failed, "2" conversion failed.
 */
App.convert = function (value, converter) {
    var rs = {
            result: value,
            message: "",
            code: 0
        };
    try {
        if (converter) {
            var c = converter.split('.');
            var f = window[c.shift()];
            var i;
            for (i in c) {
                f = f[c[i]];
            }
            if (f && typeof f["convert"] === "function") {
                return f["convert"](value);
            } else {
                return rs;
            }
        } else {
            return rs; 
        }
    } catch (e) {
        console.log(e.name + ': ' + e.message);
        rs.code = 2;
        rs.message = e.message;
        return rs;
    }
};

App.conversion = {};
App.conversion.date = {};
App.conversion.date.format = function (date, type) {
    var obj = App.conversion.date;
    var a = date.split(/[-T:]/);
    if(a[0] == "0000"){
        return "N/A";
    }
    date = new Date(a[0],a[1]-1,a[2],a[3],a[4],a[5]);
    
    var pattern = obj["datetime"]();
    
    if (type && typeof obj[type] === "function") {
        pattern = obj[type]();
    }
    return date.format(pattern);
};

App.conversion.date.datetime = function (date) {
    var pattern;
    if (App.langCode === "en-us") {
        pattern = "yyyy-mm-dd HH:MM:ss";
    } else {
        pattern = "yyyy-mm-dd HH:MM:ss";
    }
    return pattern;
};

App.conversion.date.time = function (date) {
    var pattern;
    if (App.langCode === "en-us") {
        pattern = "HH:MM:ss";
    } else {
        pattern = "HH:MM:ss";
    }
    return pattern;
}

App.conversion.date.date = function (date) {
    var pattern = "";
    if (App.langCode === "en-us") {
        pattern = "yyyy-mm-dd";
    } else {
        pattern = "yyyy-mm-dd";
    }
    return pattern;
}

App.conversion.DueDateIcon = {};

App.conversion.DueDateIcon.format = function (remain_seconds) {
    var d = App.config.due_time;
    if( !remain_seconds || remain_seconds < 0)
        return "green";
    if(remain_seconds < d.red)
        return "red";
    if(remain_seconds < d.yellow)
        return "yellow";
    return "green";
};