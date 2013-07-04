Ext.QuickTips.init();
Ext.define('jibu.layout.applike.startpanel', {
        extend : 'Ext.Panel',
        mainMenuText:'Main',
        alias : 'widget.startpanel',
        config : {
            // 常用模块的字符名称
            usedModName : []
        },
        constructor : function(config) {
            // 正确初始化类config
            this.initConfig(config);
            var startPanel = {
                layout : 'border',
                baseCls : 'x-plain',
                items : [ {
                    region : 'east',
                    width : 130,
                    border : false,
                    baseCls : 'x-plain',
                    items : {
                        xtype : 'logo'
                    }
                }, {
                    region : 'center',
                    border : false,
                    plain : true,
                    baseCls : 'x-plain',
                    layout : {
                        type : 'hbox',
                        padding : '2',
                        pack : 'start',
                        align : 'stretch'
                    },
                    defaults : {
                        margins : '0 2 0 0'
                    },
                    items : [ this.getStartMenu() ],
                    listeners : {
                        scope : this,
                        boxready : function(comp, opt) {
                            this.addUsedModule(comp);
                        }
                    }
                } ]
            };
            Ext.apply(this, config, startPanel);
            this.callParent(arguments);
        },
        loadModule : function(btn) {
            var tab = GX.items.items[1];
            if (!Ext.ClassManager.getByAlias('widget.'
                                             + btn.modName)) {
                Ext.Msg.alert('Tips', 'Module is constructing!.');
                return;
            }
            if (!tab.getComponent(btn.modName)) {
                tab.add({
                    xtype : btn.modName,
                    id : btn.modName,
                    title : btn.text,
                    closable : true,
                    border : false
                });
                if (btn.modName != 'common.setting') {
                    this._saveModuleClick(btn);
                }
            }
            tab.setActiveTab(btn.modName);
        },
        /**
         * 开始菜单的组装
         * 
         * @return {}
         */
        getStartMenu : function() {
            var items = [], usedModName = [], handler = this.loadModule, scope = this;
            var getSubItem = function(obj, subItems) {
                var arr = [];
                Ext.Array.each(obj, function(subObj, idx) {
                    arr[arr.length] = {
                        modName : subObj.url,
                        text : subObj.text
                    };
                    if (!subObj.leaf) {
                        getSubItem(subObj.children,
                                   arr[arr.length - 1]);
                    } else {
                        usedModName[subObj.url] = subObj.text;
                        Ext.apply(arr[arr.length - 1], {
                            scope : scope,
                            handler : handler
                        });
                    }
                });
                Ext.apply(subItems, {
                    menu : arr
                });
            };
            Ext.Array.each(jibu.navData, function(obj, idx) {
                items[items.length] = {
                    modName : obj.url,
                    text : obj.text
                };
                if (!obj.leaf) {
                    getSubItem(obj.children,
                               items[items.length - 1]);
                } else {
                    usedModName[subObj.url] = subObj.text;
                    Ext.apply(arr[arr.length - 1], {
                        scope : scope,
                        handler : handler
                    });
                }
                
            });
            this.setUsedModName(usedModName);
            return {
                xtype : 'splitbutton',
                text : this.mainMenuText,
                menu : new Ext.menu.Menu({
                    ignoreParentClicks : true,
                    items : items
                }),
                handler : function(btn, e) {
                    btn.showMenu();
                }
            };
        },
        _saveModuleClick : function(item) {
            var allItem = this._getCookieObject();
            
            var value = allItem[item.modName];
            if (Ext.isEmpty(value)) {
                value = 1;
            } else {
                value = parseInt(value) + 1;
            }
            allItem[item.modName] = value;
            allItem = this._cookieObjectToString(allItem);
            Ext.util.Cookies.set('gx_mod_statics', allItem,
                                 Ext.Date.add(new Date(), Ext.Date.YEAR, 1));
        },
        addUsedModule : function(comp) {
            var tmpArray = [], scope = this;
            Ext.Object.each(this._getCookieObject(),
                            function(k, v) {
                                tmpArray[tmpArray.length] = [ k,
                                                              parseInt(v) ];
                            });
            // 对模块根据点击次数排序
            for ( var i = 0; i < tmpArray.length; i++) {
                for ( var j = tmpArray.length - 1; j > i; j--) {
                    if (tmpArray[j][1] < tmpArray[j - 1][1]) {
                        temp = tmpArray[j];
                        tmpArray[j] = tmpArray[j - 1];
                        tmpArray[j - 1] = temp;
                    }
                }
            }
            // 设置常用模块数量，目前为5
            var modnum = 5, tConfig = [];
            // 生成常用模块配置项
            for ( var i = tmpArray.length - 1; i > -1; i--, modnum--) {
                if (modnum == 0) {
                    break;
                }
                if (scope.getUsedModName()[tmpArray[i][0]]) {
                    tConfig[tConfig.length] = {
                        modName : tmpArray[i][0],
                        text : scope.getUsedModName()[tmpArray[i][0]],
                        handler : scope.loadModule,
                        scope : scope
                    };
                }
            }
            // + '(' + tmpArray[i][1] + ')'
            if (!Ext.isEmpty(tConfig)) {
                comp.add({
                    xtype : 'buttongroup',
                    baseCls : 'group-btn ',
                    defaults : {
                        scale : 'medium'
                    },
                    items : tConfig
                });
            }
            
        },
        /**
         * 将cookie中取到得字符串转换为对象
         * 
         * @return {Object} 包含所有使用过模块的统计对象
         */
        _getCookieObject : function() {
            var rec = Ext.util.Cookies.get('gx_mod_statics');
            var res = {};
            if (!Ext.isEmpty(rec)) {
                Ext.each(Ext.decode(rec).split(';'),
                         function(item, idx) {
                             if (!Ext.isEmpty(item)) {
                                 res[item.split(':')[0]] = item
                                     .split(':')[1];
                             }
                         });
            }
            return res;
        },
        
        /**
         * 将cookie对象转换为字符串
         * 
         * @param {Object}
         *            含有cookie记录的对象
         * @return {Object} 格式化的字符串
         */
        _cookieObjectToString : function(allItem) {
            var res = '';
            Ext.Object.each(allItem, function(key, value, myself) {
                res += key + ':' + value + ';';
            });
            return Ext.encode(res);
        }
    });

// Ext.state.Manager.setProvider(new Ext.state.CookieProvider({
// expires : new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 1))
// }));

Ext.define('jibu.layout.applike.MainPanel', {
    extend : 'Ext.container.Viewport',
    accountText:'Account',
    accountTooltip:'Account Setting',
    exitText:'Exit',
    centerPanelText:'Welcome',
    constructor : function() {
        var config = {
            layout : 'border',
            baseCls : 'x-plain',
            items : [ {
                region : 'north',
                autoHeight : false,
                height : 35,
                border : false,
                xtype : 'startpanel'
            }, {
                region : 'center',
                xtype : 'tabpanel',
                // stateEvents : ['add'],
                // stateful : true,
                // stateId:'tabs',
                plain : true,
                activeTab : 0,
                items : {
                    title : this.centerPanelText,
                    loader : {
                        url : 'html/welcome.html',
                        autoLoad : true
                    }
                },
                bbar : {
                    xtype : 'statusbar',
                    baseCls : 'x-plain',
                    id : 'msg-statusbar',
                    items : [ '->', {
                        xtype : 'button',
                        text : this.accountText,
                        iconCls : 'user',
                        scope : this,
                        modName : 'common.setting',
                        handler : function(btn) {
                            this.down('startpanel').loadModule(btn);
                        }
                    }, {
                        xtype : 'button',
                        iconCls : 'exit',
                        text : this.exitText,
                        handler : function() {
                            window.location = 'Login.x?ci=logout';
                        }
                    } ]
                }
            } ]

        };
        Ext.apply(this, config);
        this.callParent(arguments);
    }
});

Ext.onReady(function() {
    GX = Ext.create('jibu.layout.applike.MainPanel');
});
