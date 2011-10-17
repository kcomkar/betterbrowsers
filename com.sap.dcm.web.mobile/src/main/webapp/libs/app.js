/*global $, _, */
/**
 * @fileoverview This file is used for define the Mobile Web Framework
 * @author Jackson Tian
 * @version 0.1
 */

(function (global) {

    /**
     * @description The Framework's top object. all components will be register under it.
     * @namespace Top namespace. Why named as App, we salute the V8 project.
     * The voice means it contains power in Chinese also.
     * @requires Underscore, jQuery/Zepto.
     * @name App
     */
    var App = function () {};

    /**
     * Mixin EventProxy.prototype into App.prototype, make it has bind, unbind, trigger methods.
     */
    _.extend(App, EventProxy.prototype);

    /**
     * @description Lets callback execute at a safely moment.
     * @param {function} callback The callback method that will execute when document is ready.
     */
    App.ready = function (callback) {
        if (document.readyState === "complete") {
            callback();
        } else {
            var ready = function () {
                if (document.readyState === "complete") {
                    callback();
                    // Remove the callback listener from readystatechange event.
                    $(document).unbind("readystatechange", ready);
                }
            };
            //  Bind callback to readystatechange
            $(document).bind("readystatechange", ready);
        }
    };

    /**
    * @description Gets the App mode, detects the App runing in which devices.
    * There are two modes current, phone or tablet.
    */
    App.mode = window.innerWidth < 768 ? "phone" : "tablet";

    /**
    * @description Default viewport reference. Viewport could contains many view columns, it's detected by mode.
    */
    App.viewport = null;

    /**
     * @description Startups App framework.
     */
    App.init = function () {
        App.ready(function () {
            App.viewport = $("#container");
            App.setOrientation();
            // Disable touch move events for integrate with iScroll.
            window.addEventListener("touchmove", function (e) {e.preventDefault(); }, false);
            // Use popstate to handle history go/back.
            window.addEventListener('popstate', function (event) {
                var params = event.state;
                if (params) {
                    var args = params.split("/");
                    var currentHash = args.shift();
                    console.log("Hash Changed: " + currentHash);
                    if (App.hashHistory.length) {
                        console.log(App.hashHistory);
                        if (currentHash !== undefined) {
                            var topHash = _.map(App.hashMap, function (val, key) {
                                return _.last(val);
                            });
                            if (_.include(topHash, params)) {
                                console.log("changed, but no action.");
                            } else {
                                var hashStack = _.compact(_.map(App.hashMap, function (val, key) {
                                    return _.include(val, currentHash) ? key : null;
                                }));
                                console.log(hashStack);
                                App.hashMap[hashStack[0]].pop();
                                App.trigger("openView", currentHash, _.indexOf(App.columns, hashStack[0]));
                                console.log("Forward or back");
                            }
                        }
                    }
                }
            }, false);

            // Handle refresh case or first visit.
            if (App.hashHistory.length === 0) {
                var map = App.hashMap;
                if (_.size(map)) {
                    // Restore view from session.
                    console.log("Restore from session.");
                    App.restoreViews();
                } else {
                    // Init view.
                    console.log("Init view.");
                    App.initView();
                }
            }
        });
    };

    var body = $("body");
    /**
     * @description Handle orient change events.
     */
    App.setOrientation = function () {
        var _setOrientation = function () {
            var orient = Math.abs(window.orientation) === 90 ? 'landscape' : 'portrait';
            var aspect = orient === 'landscape' ? 'portrait' : 'landscape';
            body.removeClass(aspect).addClass(orient);
        };
        _setOrientation();
        window.addEventListener('orientationchange', _setOrientation, false);
    };

    /**
     * @description Cache the page html.
     */
    App._pageCache = {};

    /**
     * @description Predefined view columns.
     */
    App.columns = ["alpha", "beta", "gamma"];
    /**
     * @description Predefined viewport's state.
     */
    App.columnModes = ["single", "double", "triple"];

    App.bind("openView", function (viewName, effectColumn, args, viewport) {
        if (App.mode === "phone") {
            effectColumn = 0;
        }
        args = args || [];
        viewport = viewport || App.viewport;
        App.displayView(viewName, effectColumn, args, viewport);
        var hash = [viewName].concat(args).join("/");
        history.pushState(hash, viewName, "#" + hash);
    });

    /**
     * @description Initializes views when first time visit.
     */
    App.initView = function () {
        App.trigger("openView", "hitlist", 0);
    };

    /**
     * @description Restores views from session storage.
     */
    App.restoreViews = function () {
        var map = App.hashMap;
        console.log(map);
        _.each(map, function (viewNames, columnName) {
            var hash = viewNames.pop();
            var args = hash.split("/");
            App.trigger("openView", args.shift(), _.indexOf(App.columns, columnName), args, App.viewport);
        });
    };

    /**
     * @description Gets View from cache or server. If the view file come from server, 
     * the callback will be executed async, and cache it.
     * @param {string} viewName View name.
     * @param {boolean} enableL10N Flag whether this view's localization enabled.
     * If true, will generate view with localization resources.
     * @param {function} callback Callback function, will be called after got the view from cache or server.
     */
    App.getView = function (viewName, enableL10N, callback) {
        var _pageCache = App._pageCache;
        var page = App._pages[viewName];
        var proxy = new EventProxy();
        proxy.assign("l10n", "view", function (l10n, view) {
            var html = App.localize(view, l10n);
            page.resources = l10n;
            callback($(html));
        });

        if (_pageCache[viewName]) {
            proxy.trigger("view", _pageCache[viewName]);
        } else {
            $.get("pages/" + viewName + ".page?_=" + new Date().getTime(), function (text) {
                // Save into cache.
                _pageCache[viewName] = text;
                proxy.trigger("view", _pageCache[viewName]);
            });
        }

        // Fetch the localize resources.
        if (page.enableL10N) {
            App.fetchL10N(viewName, function () {
                proxy.trigger("l10n", App.L10N[App.langCode][viewName]);
            });
        } else {
            proxy.trigger("l10n", {});
        }
    };

    /**
     * @description Display view in view column.
     * @private
     * @param {string} hash View hash, view name.
     * @param {number} effectColumn View column's index.
     * @param {array} args Parameters of view.
     * @param {object} viewport Which viewport, if don't set, will use default viewport.
     */
    App.displayView = function (hash, effectColumn, args, viewport) {
        var columnName = App.columns[effectColumn];
        var column = viewport.find("." + columnName);
        if (column.size() < 1) {
            column = $("<div><div class='column_loading'><div class='loading_animation'></div></div></div>");
            column.addClass(columnName);
            viewport.append(column);
        }

        if (viewport === App.viewport) {
            if (App.hashMap[columnName]) {
                App.hashMap[columnName].push([hash].concat(args).join("/"));
            } else {
                App.hashMap[columnName] = [[hash].concat(args).join("/")];
            }
            App.hashHistory.push([hash].concat(args));
        }

        var page = App._pages[hash];
        if (page) {
            var previousPage = column.find("section.page.active").removeClass("active");
            if (previousPage.length) {
                var id = previousPage.attr('id');
                var previous = App._pages[id];
                if (previous && id !== hash) {
                    previous.shrink();
                }
            }

            var loadingNode = column.find(".column_loading").removeClass("hidden");
            var page = App._pages[hash];
            App.getView(hash, page.enableL10N, function (view) {
                loadingNode.addClass("hidden");
                if (viewport === App.viewport) {
                    viewport.attr("class", App.columnModes[_.size(App.hashMap) - 1]);
                }
                page.columnIndex = _.indexOf(App.columns, columnName);
                if (!page.initialized) {
                    column.append(view);
                    page.node = view;
                    page.node.addClass("active");
                    page.initialize.apply(page, args);
                    page.initialized = true;
                } else {
                    if (page.parameters.toString() !== args.toString()) {
                        page.destroy();
                        page.node.remove();
                        page.initialized = false;
                        App.getView(hash, page.enableL10N, arguments.callee);
                        return;
                    } else {
                        page.node.addClass("active");
                        page.reappear();
                    }
                }

                page.parameters = args;
                page.viewport = viewport;
            });
        } else {
            throw hash + " module doesn't be defined.";
        }
    };

    /**
     * @description History implementation. Stores history actions.
     */
    App.hashHistory = [];

    /**
     * @description Store hash and keep in session storage.
     */
    App.hashMap = (function () {
        var session = getStorage("session");
        var hashMap = session.get("hashMap");
        if (!hashMap) {
            hashMap = {};
        } else {
            session.remove("hashMap");
        }
        $(window).bind("unload", function () {
            session.put("hashMap", App.hashMap);
        });
        return hashMap;
    }());

    global.App = App;
}(window));

/**
 * View
 */
(function (global) {
    /**
     * @description A factory method to generate View object. Packaged on Backbone.View.
     * @param {node} node a $(Zepto/jQuery) element node.
     * @returns {View} View object, based on Backbone.View.
     */
    App.View = function (node) {
        var View = Backbone.View.extend({
            el: node,
            bind: function (name, method) {
                this[name] = method;
            },
            undelegateEvents: function () {
                $(this.el).unbind();
            }
        });
        return new View();
    };

}(window));
/**
 * Templates
 */
(function (global) {
    var App = global.App;
    App._templates = {};

    /**
     * @description templateMode, optimized or normal.
     */
    App.templateMode = "normal";

    var getTemplateNormally = function (name, callback) {
        var template = App._templates[name];
        if (template) {
            callback(template);
        } else {
            $.get("templates/" + name + ".tmpl?_=" + new Date().getTime(), function (templ) {
                App._templates[name] = templ;
                callback(templ);
            });
        }
    };

    var getTemplateOptimized = function (name, callback) {
        var template = App._templates[name];
        if (template) {
            callback(template);
        } else {
            $.get("templates/optimized_combo.tmpl?_=" + new Date().getTime(), function (templ) {
                $(templ).find("script").each(function (index, script) {
                    var templateNode = $(script);
                    var id = templateNode.attr("id");
                    App._templates[id] = templateNode.html();
                });
                callback(App._templates[name]);
            });
        }
    };

    /**
     * @description Fetch the template file.
     */
    App.getTemplate = function (name, callback) {
        if (App.templateMode === "normal") {
            getTemplateNormally(name, callback);
        } else {
            getTemplateOptimized(name, callback);
        }
    };

}(window));

/**
 * Page defined
 */
(function (global) {
    var App = global.App;
    /**
     * @description Page namespace. All page module will be stored at here.
     * @namespace 
     * @private
     */
    App._pages = {};

    /**
     * @description Register a page to App.
     * @param {string} name Page id, used as key, App framework find page element by this name
     * @param {function} The module object.
     */
    App.registerPage = function (name, module) {
        if (typeof module === "function") {
            App._pages[name] = new App.Page(module());
        }
    };

    var Page = function (module) {
        // Mixin the eventproxy's prototype
        _.extend(this, EventProxy.prototype);
        /**
         * @description The Initialize method.
         * @field {function} initialize
         */
        this.initialize = function () {};
        /**
         * @description The Shrink method, will be invoked when hide current view.
         * @field {function} initialize
         */
        this.shrink = function () {};
        /**
         * @description The Reappear method, when View reappear after shrink, this function will be invoked.
         * @field {function} reappear
         */
        this.reappear = function () {};
        /**
         * @description The Destroy method, should be invoked manually when necessary.
         * @field {function} reappear
         */
        this.destroy = function () {};
        /**
         * @description Parameters, store the parameters, for check the view whether changed.
         * @field {Array} parameters
         */
        this.parameters = null;
        /**
         * @description Flag whether enable localization.
         */
        this.enableL10N = false;
        // Merge the module's methods
        _.extend(this, module);
    };

    /**
     * @description Open a view from current column or next column.
     * @memberOf Page.prototype
     */
    Page.prototype.openView = function (hash, blank) {
        var effectColumn;
        if (blank) {
            effectColumn = this.columnIndex + 1;
        } else {
            effectColumn = this.columnIndex;
        }
        var args = hash.split("/");
        var viewName = args.shift();
        App.trigger("openView", viewName, effectColumn, args);
    };

    /**
     * @description Open a viewport and display a page.
     */
    Page.prototype.openViewport = function (hash) {
        var args = hash.split("/");
        var view  = args.shift();
        var viewport = $("<div></div>").addClass("viewport");
        $(document.body).append(viewport);
        App.trigger("openView", view, 0, args, viewport);
    };

    /**
     * @description Destroy current page and close current viewport.
     */
    Page.prototype.closeViewport = function (hash) {
        this.destroy();
        this.node.remove();
        delete this.node;
        this.initialized = false;
        this.viewport.remove();
        delete this.viewport;
    };

    /**
     * @description Call a common module.
     */
    Page.prototype.call = function (moduleId) {
        var module = App._modules[moduleId];
        if (module) {
            module.Apply(this, []);
        } else {
            throw moduleId + " Module doesn't exist";
        }
    };

    /**
     * @description Post message to Page.
     */
    Page.prototype.postMessage = function (event, data) {
        this.trigger("page:" + event, data);
        return this;
    };

    /**
     * @description Bind message event.
     */
    Page.prototype.onMessage = function (event, callback) {
        this.bind("page:" + event, callback);
        return this;
    };

    /**
     * @description Define a page component. Page will be displayed in a view colomn.
     * @param {function} module Module object.
     * @class Represents a page.
     * @constructor App.Page.
     * @memberOf App
     */
    App.Page = Page;
}(window));

/**
 * Module
 */
(function (global) {
    var App = global.App;

    App._modules = {};

    /**
     * @description Register a common module.
     */
    App.registerModule = function (moduleId, module) {
        App._modules[moduleId] = module;
    };

}(window));

/**
 * Localization
 */
(function (global) {
    var App = global.App;

    /**
     * @description Local code.
     */
    App.langCode = "en-US";

    /**
     * @description All localization resources will be stored at here by locale code.
     * @namespace Localization resources namespace.
     */
    App.L10N = {};

    /**
     * @description Gets localization resources by view name.
     * @param {string} viewName View name
     * @param {function} callback Callback that will be invoked when sources is got.
     */
    App.fetchL10N = function (viewName, callback) {
        var code = App.langCode;
        $.getJSON("languages/" + viewName + "_" + code + ".lang?_=" + new Date().getTime(), function (data) {
            // Sets l10n resources to App.L10N
            App.L10N[code] = App.L10N[code] || {};
            _.extend(App.L10N[code], data);
            callback(App.L10N[code]);
        });
    };

    /**
     * @desciption A wrapper method to localize template with the resources. 
     * @param {string} template template string.
     * @param {object} resources resources object.
     * @returns rendered html string.
     */
    App.localize = function (template, resources) {
        var settings = {
                interpolate : /\{\{(.+?)\}\}/g
            };
        var compiled = _.compile(template, settings);
        return compiled(resources);
    };

}(window));

/**
 * App message mechanism.
 */
(function (global) {
    var App = global.App;
    App.postMessage = function (hash, event, data) {
        var page = App._pages[hash];
        if (page) {
            page.postMessage(event, data);
        }
    };
}(window));
