Ext.define('jibu.layout.classic.HeaderPanel', {
    extend: 'Ext.panel.Panel',
    accountText:'Account',
    accountTooltip:'Account Setting',
    exitText:'Exit',

    initComponent: function(){
        Ext.apply(this, {
            layout: 'border',
            margins:"0 5 0 5",
            border:false,
            region:"north",
            height:40,
            items:[
                {
                    cls:"main-header",
                    border:false,
                    region:'west',
                    width:225,
                    baseCls : 'x-plain',
                    items : {
                        xtype : 'logo'
                    }
                },{
                    cls:"main-header",
                    region:'center',
                    border:false,
                    tbar:Ext.create('Ext.ux.StatusBar',{
                        style:'line-height:16px;',
                        baseCls:'x-plain',
                        id:'msg-statusbar',
                        items : [
                            {
                                xtype:'button',
                                text:this.accountText,
                                tooltip: this.accountTooltip,
                                iconCls :'user',
                                handler : function() {
                                    mainPanel.loadModule('common.setting', this.getText());
                                }
                            },'-',{
                                xtype:'button',
                                text:this.exitText,
                                tooltip: this.exitText,
                                iconCls :'exit',
                                handler : function() {
                                    window.location = 'Login.x?ci=logout';
                                }
                            }]
                    })

                }]

        });
        this.callParent(arguments);
    }
});

Ext.define('jibu.layout.classic.NavPanel', {
    extend: 'Ext.tree.Panel',
    expandText:'Expand All',
    collapseText:'Collapse All',

    initComponent: function(){
        Ext.apply(this, {
            region:"west",
            split:true,
            header:false,
            width:220,
            minSize:175,
            maxSize:500,
            collapsible:true,
            margins:"0 0 5 5",
            cmargins:"0 0 0 0",
            rootVisible:false,
            autoScroll:true,
            collapseMode:"mini",
            collapseFirst:false,
            store:Ext.create('Ext.data.TreeStore', {
                root: {
                    expanded: true,
                    children: jibu.navData
                }
            }),
            tbar:[' ',' ','->',
                  {
                      iconCls:'icon-expand-all',
                      tooltip:this.expandText,
                      handler: function(){
                          this.expandAll();
                      },
                      scope: this
                  },'-',{
                      iconCls:'icon-collapse-all',
                      tooltip:this.collapseText,
                      handler: function(){
                          this.collapseAll();
                      },
                      scope: this
                  }],
            listeners:{
                itemclick: function(view,re){
                    if(re.isLeaf()){
                        mainPanel.loadModule(re.raw.url, re.data.text);
                    }
                },
                boxready: function() {
                    var root = this.store.getRootNode();
                    root.eachChild(function(n) {
                        n.expand();
                    });
                }
            }
        });
        this.callParent(arguments);
    }
});

Ext.define('jibu.layout.classic.MainPanel', {
    extend: 'Ext.tab.Panel',
    centerPanelText:'Welcome',

    initComponent: function(){
        Ext.apply(this, {
            region:'center',
            margins:'0 5 5 0',
            enableTabScroll:true,
            minTabWidth:75,
            activeTab:0,
            plain: true,
            items:[{
                title: this.centerPanelText,
                loader:{
                    url:'html/welcome.html',
                    autoLoad:true
                },
                autoScroll:true
            }]
        });
        this.callParent(arguments);
    },

    loadModule : function(moduleName,moduleTitle){
            var tab;
        if(!(tab = this.getComponent(moduleName))){
            tab = [{
                xtype: moduleName,
                id:moduleName,
                title: moduleTitle,
                closable:true,
                border:false
            }];
            this.add(tab);
        }
        this.setActiveTab(moduleName);
    }
});

Ext.onReady(function(){
    Ext.tip.QuickTipManager.init();
    mainPanel = new jibu.layout.classic.MainPanel();
    Ext.create('Ext.container.Viewport',{
        layout: 'border',
        items: [
            Ext.create('jibu.layout.classic.HeaderPanel'),
            Ext.create('jibu.layout.classic.NavPanel'),
            mainPanel
        ]
    });
});
