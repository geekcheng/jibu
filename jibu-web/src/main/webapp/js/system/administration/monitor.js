Ext.define('jibu.security.monitor.Panel', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.system.administration.monitor',
    maxActive : 'Max Active',
    numActive : 'Current Active',
    numIdle : 'Current Idle',
    numSession : 'Current Session',
    totalMemory : 'Total Mem (MB)',
    usedMemory : 'Used Mem (MB)',
    mems : 'Memory',
    dbconns : 'Active',
    sessions : 'Session',
    store : Ext.create('Ext.data.JsonStore', {
        fields : ['time', 'session', 'max', 'active', 'idle',
                  'session', 'totalMemory', 'usedMemory', 'memoryPer',
                  'activePer']
    }),
    times :0,

    initComponent : function() {
        var v1 = [{
                xtype : 'fieldset',
            width : 180,
            title : this.dbconns,
            collapsible : false,
            defaultType : 'displayfield',
            items : [{
                fieldLabel : this.maxActive,
                id : 'maxActive',
                readOnly : true
            }, {
                fieldLabel : this.numActive,
                id : 'numActive',
                readOnly : true
            }, {
                fieldLabel : this.numIdle,
                id : 'numIdle',
                readOnly : true
            }]
        }, {
            xtype : 'chart',
            flex : 2,
            store : this.store,
            shadow : false,
            id : 'monitor-chart1',
            axes : [{
                title : this.dbconns + '%',
                type : 'Numeric',
                position : 'left',
                minimum : 0,
                maximum : 99,
                majorTickSteps : 0,
                minorTickSteps : 9,
                fields : ['activePer']
            }, {
                type : 'Numeric',
                position : 'bottom',
                fields : 'time',
                minimum : 0,
                maximum : 60,
                majorTickSteps : 0,
                minorTickSteps : 59
            }],
            series : [{
                type : 'line',
                showMarkers : false,
                axis : ['left', 'bottom'],
                xField : 'time',
                yField : ['activePer'],
                style : {
                    fill : 'red',
                    'stroke-width' : 2
                }
            }]
        }];

        var v2 = [{
            xtype : 'fieldset',
            width : 180,
            title : this.sessions,
            collapsible : false,
            defaultType : 'displayfield',
            items : [{
                fieldLabel : this.numSession,
                id : 'numSession',
                readOnly : true
            }]
        }, {
            xtype : 'chart',
            flex:2,
            store : this.store,
            shadow : false,
            id : 'monitor-chart2',
            axes : [{
                type : 'Numeric',
                title : this.sessions,
                position : 'left',
                minimum : 0,
                maximum : 99,
                majorTickSteps : 0,
                minorTickSteps : 9,
                fields : ['session']
            }, {
                type : 'Numeric',
                position : 'bottom',
                fields : 'time',
                minimum : 0,
                maximum : 60,
                majorTickSteps : 0,
                minorTickSteps : 59
            }],
            series : [{
                type : 'line',
                showMarkers : false,
                axis : ['left', 'bottom'],
                xField : 'time',
                yField : ['session'],
                style : {
                    fill : 'red',
                    'stroke-width' : 2
                }
            }]
        }];
        var v3 = [{
            xtype : 'fieldset',
            width : 180,
            title : this.mems,
            collapsible : false,
            defaultType : 'displayfield',
            items : [{
                fieldLabel : this.totalMemory,
                id : 'totalMemory',
                readOnly : true
            }, {
                fieldLabel : this.usedMemory,
                id : 'usedMemory',
                readOnly : true
            }]
        }, {
            xtype : 'chart',
            flex:2,
            store : this.store,
            shadow : false,
            id : 'monitor-chart3',
            axes : [{
                type : 'Numeric',
                title : this.mems + '%',
                position : 'left',
                minimum : 0,
                maximum : 99,
                majorTickSteps : 0,
                minorTickSteps : 9,
                fields : ['memoryPer']
            }, {
                type : 'Numeric',
                position : 'bottom',
                fields : 'time',
                minimum : 0,
                maximum : 60,
                majorTickSteps : 0,
                minorTickSteps : 59
            }],
            series : [{
                type : 'line',
                showMarkers : false,
                axis : ['left', 'bottom'],
                xField : 'time',
                yField : ['memoryPer'],
                style : {
                    fill : 'red',
                    'stroke-width' : 2
                }
            }]
        }];
        Ext.apply(this, {
            bodyStyle : 'padding:5px 5px 0',
            frame : true,
            cls : 'form-frame',
            layout : {
                type : 'vbox',
                align : 'stretch'
            },
            items : [{
                flex : 1,
                xtype : 'container',
                layout : {
                    type : 'hbox',
                    align : 'stretch'
                },
                items : v1
            }, {
                flex : 1,
                xtype : 'container',
                layout : {
                    type : 'hbox',
                    align : 'stretch'
                },
                items : v2
            }, {
                flex : 1,
                xtype : 'container',
                layout : {
                    type : 'hbox',
                    align : 'stretch'
                },
                items : v3
            }],
            listeners : {
                show : function() {
                    var intervalID,me=this;
                    function ajax() {
                        var comp = Ext.getCmp('system.administration.monitor');
                        if (comp && (comp == comp.ownerCt.getActiveTab())) {
                            Ext.Ajax.request({
                                url : 'Monitor.z?ci=getCount',
                                method : 'POST',
                                success : function(r, a) {
                                    var rs = Ext.JSON.decode(r.responseText);
                                    var store = comp.store;
                                    Ext.getCmp('maxActive').setValue(rs.max);
                                    Ext.getCmp('numActive').setValue(rs.active);
                                    Ext.getCmp('numIdle').setValue(rs.idle);
                                    Ext.getCmp('numSession')
                                        .setValue(rs.session);
                                    Ext.getCmp('totalMemory')
                                        .setValue(rs.totalMemory);
                                    Ext.getCmp('usedMemory')
                                        .setValue(rs.usedMemory);

                                    store.loadData([{
                                        session : rs.session,
                                        userdMemory : rs.usedMemory,
                                        totalMemory : rs.totalMemory,
                                        memoryPer : Math
                                            .round(rs.usedMemory
                                                   / rs.totalMemory
                                                   * 100),
                                        activePer : Math
                                            .round(rs.active
                                                   / rs.max * 100),
                                        active : rs.active,
                                        max : rs.max,
                                        time : me.times
                                    }], true
                                                  );
                                    if (me.times > 59) {
                                        store.data.removeAt(0);
                                        store.data.each(function(item, key) {
                                            item.data.time = key;
                                        });
                                        me.times--;
                                    }
                                    me.times++;
                                },
                                scope : this
                            }
                                            );
                        } else {
                            clearInterval(intervalID);
                        }
                    };
                    intervalID = setInterval(ajax, 2000);
                },
                close:function(panel){
                    this.store.removeAll();
                    Ext.destroy(Ext.getCmp('monitor-chart1'));
                    Ext.destroy(Ext.getCmp('monitor-chart2'));
                    Ext.destroy(Ext.getCmp('montior-chart3'));
                }
            }
        }
                 );
        this.callParent(arguments);
    }
});

if(jibu.locale=='zh_CN') {
    Ext.override(jibu.security.monitor.Panel, {
        maxActive : '最大活动连接',
        numActive : '当前活动连接',
        numIdle : '当前空闲连接',
        numSession: '当前Session',
        totalMemory : '总内存 (MB)',
        usedMemory: '已用内存 (MB)',
        mems: '内存',
        dbconns: '数据库连接',
        sessions:'Session'
    });
}

