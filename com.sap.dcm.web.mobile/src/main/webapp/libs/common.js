/*global App, AppUI, Base64, Url, _, $, Backbone, OData, iScroll, window */

App.config = {
    "DataURLPrefix": "https://ldcigkd.wdf.sap.corp:44333/sap/opu/sdata",
    "due_time" : {
        "red" : 86400,
        "yellow" : 172800
    },
    "outbox_size" : 10
};

App.util = (function () {
    var buildBaseAuth = function (user, pass) {
            var tok = user + ':' + pass;
            var hash = Base64.encode(tok);
            return "Basic " + hash;
        };

    var buildRequest = function (uri) {
            uri = uri.replace(/\s/g, "%20");
            return {
                headers: {
                    'Authorization': buildBaseAuth("guix", "welcome3")
                },
                //requestUri: "AjaxProxy?_=" + new Date().getTime() + "&url=" + Url.encode(uri)
                requestUri: "AjaxProxy?url=" + Url.encode(uri)
            };
        };

    var buildAjaxRequest = function (uri) {
            uri = uri.replace(/\s/g, "%20");
            return {
                headers: {
                    'Authorization': buildBaseAuth("guix", "welcome3")
                },
                //url: "AjaxProxy?_=" + new Date().getTime() + "&url=" + Url.encode(uri)
                url: "AjaxProxy?url=" + Url.encode(uri)
            };
        };

    var convertToMetadataURL = function (uri) {
            var path = uri.path.split("/");
            path.pop();
            return [uri.protocol, "//", uri.authority, path.join("/"), "/$metadata", "?",
                    [uri.query, "sap-client=400", "sap-language=" + App.lang].join("&")].join("");
        };

    var convertToCollectionURL = function (uri) {
            return uri.source.replace(/\(.*\)/g, "").replace(uri.query, "") + "sap-client=400&sap-language=" + App.lang;
        };

    var getServiceName = function (uri) {
            var path = uri.path.split("/");
            path.pop();
            return path.pop();
        };

    var getCollectionName = function (uri) {
            var file = uri.file;
            return file.replace(/\(.*\)/g, "");
        };

    var getEntitySetByName = function (schema, name) {
            var sets = schema.entityContainer[0].entitySet;
            var entitySet =  _.detect(sets, function (value) {
                return value.name === name;
            });
            return entitySet;
        };

    var getEntityTypeByName = function (schema, name) {
            var entitySet = getEntitySetByName(schema, name);
            var typeName = entitySet.entityType;
            var entityType = _.detect(schema.entityType, function (value) {
                return [schema.namespace, value.name].join(".") === typeName;
            });
            return entityType;
        };

    var getKeys = function (entityType) {
            var keys = _.map(entityType.key.propertyRef, function (val, index) {
                return val.name;
            });
            return keys;
        };

    var remapEntityType = function (entityType) {
            var remap = {};
            _.each(entityType.property, function (val, key) {
                remap[val.name] = {};
                remap[val.name].name = val.name;
                _.each(val.extensions, function (ext, index) {
                    remap[val.name][ext.name] = ext.value;
                });
            });
            return remap;
        };

    var getProps = function (entityType) {
            var remap = {};
            _.each(entityType.property, function (item) {
                remap[item.name] = {};
                _.each(item, function (value, key) {
                    if (typeof value !== "object") {
                        remap[item.name][key] = value;
                    }
                });
            });
            return remap;
        };

    // Gets navigations by header entity
    var getNavigations = function (metadata, entityType, uri, detail) {
            console.log(metadata);
            var navs = _.map(entityType.navigationProperty, function (nav, index) {
                    var relationship = nav.relationship;
                    // Gets association.
                    var association = _.detect(metadata.association, function (ass) {
                        return [metadata.namespace, ass.name].join(".") === relationship;
                    });
                    // Gets entity type by association ends.
                    var navType = _.detect(association.end, function (end) {
                        return end.role === nav.toRole;
                    });
                    var ret = {};
                    var entity = _.detect(metadata.entityType, function (type) {
                        return [metadata.namespace, type.name].join(".") === navType.type;
                    });
                    ret.entityType = _.sortBy(App.util.remapEntityType(entity), function (item) {
                        return item["prop-display-order"];
                    });
                    ret.props = App.util.getProps(entity);
                    ret.keys = App.util.getKeys(entity);
                    _.each(association.extensions, function (ext, index) {
                        ret[ext.name] = ext.value;
                    });
                    ret.url = [uri.protocol, "//", uri.authority, uri.path, "/", nav.name].join("");
                    ret.data = detail[nav.name].results || null;
                    return ret;
                });

            return navs;
        };

    return {
        buildBaseAuth: buildBaseAuth,
        buildRequest: buildRequest,
        buildAjaxRequest: buildAjaxRequest,
        convertToMetadataURL: convertToMetadataURL,
        getServiceName: getServiceName,
        getCollectionName: getCollectionName,
        getEntitySetByName: getEntitySetByName,
        getEntityTypeByName: getEntityTypeByName,
        remapEntityType: remapEntityType,
        getNavigations: getNavigations,
        getKeys: getKeys,
        getProps: getProps,
        convertToCollectionURL: convertToCollectionURL
    };
}());

/*
App.TabModule = function (nav) {
    return function () {
        var tab = this;
        console.log(tab);
        tab.bind("initialize", function () {
            var util = App.util;
            var proxy = new App.EventProxy();
            proxy.assignAll("template", "items", "metadata", "props", function (template, items, metadata, props) {
                nav.props = props;
                var html = _.template(template, {
                        "data": items,
                        "metadata": metadata,
                        "keys": nav.keys,
                        "props": props,
                        "readOnly": nav.readOnly
                    });
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
            var widget = nav.ui_widget || "table";
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
            } else {
                // Get items data.
                var itemsRequest = util.buildRequest(nav.url);
                OData.read(itemsRequest, function (data) {
                    nav.data = data.results;
                    proxy.trigger("items", data.results);
                }, function (error) {
                    proxy.trigger("error");
                    console.log(error);
                });
            }
            if (nav.entityType) {
                proxy.trigger("metadata", nav.entityType);
                proxy.trigger("props", nav.props);
            } else {
                var uri = Url.parseUri(nav.url);
                var metadataRequest = util.buildRequest(util.convertToMetadataURL(uri));
                var serviceName = util.getServiceName(uri);
                var collectionName = util.getCollectionName(uri);
                OData.read(metadataRequest, function (data) {
                    // Gets schema by service namespace
                    var metadata = _.detect(data.dataServices.schema, function (schema) { 
                            return schema.namespace === serviceName; 
                        });
                    var entityType = util.getEntityTypeByName(metadata, collectionName);
                    proxy.trigger("metadata", _.sortBy(util.remapEntityType(entityType), function (item) {
                        return item["prop-display-order"];
                    }));
                    proxy.trigger("props", util.getProps(entityType));
                }, function (error) {
                    proxy.trigger("error");
                    console.log(error);
                }, OData.metadataHandler);
            }

            // Delegate update events
            var keys = nav.keys;
            var handler = function (event) {
                var target = $(event.currentTarget);
                var value = target.val();
                var key = target.closest(".column").data("key");
                // Valid the value.
                var prop = nav.props[key];
                var valid = true;
                if (prop.nullable === "false" && !value) {
                    valid = false;
                }
                if (valid) {
                    switch (prop.type) {
                    case "Edm.Decimal":
                        if (!_.isNumber(parseFloat(value))) {
                            valid = false;
                        }
                        break;
                    default:
                        
                        break;
                    }
                }
                if (valid) {
                    var row = target.closest(".row");
                    var primaryKeys = {};
                    _.each(keys, function (val, index) {
                        primaryKeys[val] = row.data(val);
                    });
                    var currentItem = _.isArray(nav.originalData) ? _.detect(nav.originalData, function (item, index) {
                        return _.all(primaryKeys, function (val, key) {
                            return item[key] === val;
                        });
                    }) : nav.originalData;

                    currentItem[key] = value;
                    nav.context.modified = true;
                    target.removeClass("error");
                } else {
                    target.addClass("error");
                }
            };
            tab.panel.delegate("input", "change", handler);
            tab.panel.delegate("select", "change", handler);

            var sortMap = {};
            tab.panel.delegate("th", "click", function (event) {
                var items = nav.data;
                var sortBy = $(event.currentTarget).data("name");

                var ordered = _.sortBy(items, function (item) {
                        return item[sortBy];
                    });
                if (sortMap[sortBy]) {
                    sortMap[sortBy] = false;
                    ordered = ordered.reverse();
                } else {
                    sortMap[sortBy] = true;
                }
                proxy.trigger("items", ordered);
            });

        });
        tab.bind("active", function () {
            console.log("active");
        });
    };
};
*/
App.TabModule = function (nav) {
    return function () {
        var tab = this;
        console.log(tab);
        tab.bind("initialize", function () {
            var util = App.util;
            var proxy = new EventProxy();
            proxy.assignAll("template", "items", function (template, items) {
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
            } else {
                // Get items data.
                var itemsRequest = util.buildRequest(nav.url);
                OData.read(itemsRequest, function (data) {
                    nav.data = data.results;
                    proxy.trigger("items", data.results);
                }, function (error) {
                    proxy.trigger("error");
                    console.log(error);
                });
            }


            // Delegate update events
          

        });
        tab.bind("active", function () {
            console.log("active");
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