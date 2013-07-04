Ext.define('jibu.security.authority.Form',{
    extend:'Ext.form.Panel',
    alias:'widget.system.administration.authform',
    authDetailText:'Authority Detail',
    nameText:'Name',
    valueText:'Value',
    submitText:'Submit',
    cancelText:'Cancel',
    waitMsgText:'Submitting...',
    initComponent:function() {
        Ext.apply(this,{
            frame: true,
            items : [{
                xtype: 'fieldset',
                labelWidth: 100,
                title: this.authDetailText,
                collapsible: true,
                defaults: {width: 350},
                defaultType: 'textfield',
                autoHeight: true,
                items: [{
                    xtype: 'hidden',
                    name: 'Authority.id',
                    allowBlank:true
                },{
                    fieldLabel: this.nameText,
                    name: 'Authority.name',
                    allowBlank:false
                },{
                    fieldLabel: this.valueText,
                    name: 'Authority.value',
                    allowBlank:false
                }]
            }],
            buttonAlign:'center',
            buttons: [{
                text: this.submitText,
                scope:this,
                formBind:true,
                handler: this.submitFn
            },{
                text: this.cancelText,
                handler: function() {
                    Ext.WindowManager.getActive().close();            
                }
            }]
        });
        this.callParent(arguments);
    },

    submitFn: function() {
        var authid = this.getForm().findField('Authority.id').getValue();
        var url;
        if (authid.length > 0) {
            url = 'Authority.z?ci=authUpdate';
        } else {
            url = 'Authority.z?ci=authAdd';
        }
        
        this.getForm().submit(
            {
                url: url,
                method: 'POST',
                disabled:true,
                waitMsg: this.waitMsgText,
                success: function(form, action) {
                    Ext.Msg.alert('Success', action.result.message);
                },
                failure: function(form, action) {
                }
            }
        );
    }
});


Ext.define('jibu.security.authority.Model',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int'},
        {name: 'name', type: 'string'},
        // 资源文件处理过后的菜单显示名
        {name: 'display', type: 'string'},
        {name: 'value', type: 'string'}]
});

Ext.define('jibu.security.authority.Grid',{
    extend:'Ext.grid.Panel',
    alias:'widget.system.administration.authgrid',
    nameText:'Name',
    valueText:'Value',
    selectColumnText:'Select Within',
    searchTooltip: 'Search',
    starTooltip:'Load binded data.',
    addTooltip:'Add Authority',
    editTooltip:'Edit Authority',
    delTooltip:'Delete Authority',
    authAddTitle:'Authority Add',
    authEditTitle:'Authority Edit',
    delMsgTitle:'Delete',
    delMsgText:'Are you sure you want to permanently delete the data?',
    initComponent:function() {
        Ext.apply(this,{
            store : Ext.create('Ext.data.JsonStore',{
                autoLoad: true,
                pageSize: 50,
                model:'jibu.security.authority.Model',
                remoteSort : true,
                proxy:{
                    type:'ajax',
                    url: 'Authority.z?ci=authFind',
                    reader:{
                        root: 'data'                
                    },
                    simpleSortMode: true
                },
                sorters: [{
                    property:'name', direction:'ASC'
                }]
            }),

            selModel: Ext.create('Ext.selection.CheckboxModel',{
                mode: 'SINGLE',
                allowDeselect: true,
                showHeaderCheckbox: false
            }),

            columns: [
                {xtype: 'rownumberer'},
                // 显示时用权限资源文件的 value
                {header: this.nameText,  width: 200, sortable: true, dataIndex: 'display'},
                {header: this.valueText, width: 100, sortable: true,  dataIndex: 'value'}
            ],

            dockedItems:[{
                xtype:'toolbar',
                dock:'top',
                items:[{
                    xtype : 'combo',
                    name : 'searchType',
                    itemId:'searchType',
                    width: 110,
                    editable:false,
                    emptyText: this.selectColumnText,
                    store: new Ext.data.ArrayStore({
                        fields: ['prop', 'displayText'],
                        data: [
                            ['Authority.value', this.valueText]
                        ]
                    }),
                    valueField : 'prop',
                    displayField:'displayText',
                    mode: 'local',
                    scope: this,
                    triggerAction: 'all'
                },'  ',{
                    xtype:'displayfield',
                    html: '<b>=</b>'
                },'  ',{
                    xtype: 'textfield',
                    width: 160,
                    name: 'searchValue',
                    itemId:'searchValue',
                    scope: this
                },{
                    xtype: 'button',
                    tooltip: this.searchTooltip,
                    scope: this,
                    iconCls :'search-icon',
                    handler : this.searchAuthFn
                },'->',{
                    itemId: 'auth-star-button',
                    iconCls: 'star-off-icon',
                    disabled:true,
                    tooltip: this.starTooltip
                }]
            },{
                xtype:'toolbar',
                dock:'bottom',
                items: [{ 
                    iconCls: 'add-icon',
                    tooltip: this.addTooltip,
                    handler: this.authAddFn,
                    scope: this
                },{
                    itemId:'auth-edit-button',
                    tooltip: this.editTooltip,
                    iconCls: 'edit-icon',
                    scope: this,
                    disabled:true,
                    handler: this.authEditFn 
                },{
                    itemId:'auth-delete-button',
                    tooltip: this.delTooltip,
                    iconCls: 'delete-icon',
                    scope: this,
                    disabled:true,
                    handler: this.authDeleteFn
                }]
            }]
        });
        this.callParent(arguments);
    },

    searchAuthFn : function(btn,event){
        var key = this.getDockedItems()[0].getComponent('searchType').getValue();
        var value = this.getDockedItems()[0].getComponent('searchValue').getValue();
        var params = {};
        params[key]=value;
        this.getStore().load({
            Scope:this,
            params:params,
            callback:function(records,o,success){
            }
        });
    },

    authAddFn: function(btn,event){
        Ext.createWidget('window',{ 
            title: this.authAddTitle,
            width:500,
            height:300,
            border:false,
            plain: true,
            layout:'fit',
            items:[{
                xtype:'system.administration.authform',
                waitMsgTarget:true
            }],
            listeners:{
                show:function(){
                    this.addPanel = this.getComponent(0);
                }
            }
        }).show();
        
    },
    authEditFn: function(btn,event){
        var record = this.getSelectionModel().getLastSelected();
        Ext.createWidget('window',{ 
            title: this.authEditTitle,
            width:500,
            height:300,
            border:false,
            plain: true,
            layout:'fit',
            items:[{
                xtype:'system.administration.authform',
                waitMsgTarget:true
            }],
            listeners:{
                show:function(){
                    var data = [{id:'Authority.id',value:record.get('id')},
                                // 进行编辑的时候只能权限编辑资源文件的 key
                                {id:'Authority.name',value:record.get('name')},
                                {id:'Authority.value',value:record.get('value')}
                               ];
                    this.getComponent(0).getForm().setValues(data);
                }
            }
        }).show();
        
    },
    authDeleteFn: function(btn,event){
        var record = this.getSelectionModel().getLastSelected();
        var store = this.getStore();
        var authDelAjaxFn = function(btn) {
            if (btn == 'yes') {
                Ext.Ajax.request(
                    {
                        url:'Authority.z?ci=authDelete',
                        params:{
                            'id':record.get('id')
                        },
                        method:'POST',
                        success: function(r,a){
                            store.load();
                        },
                        failure: function(r,o){
                        },
                        scope:this
                    }
                );
            }
            
        };
        
        Ext.MessageBox.show({
            title:this.delMsgTitle,
            msg: this.delMsgText,
            buttons: Ext.MessageBox.YESNO,
            fn: authDelAjaxFn,
            icon: Ext.MessageBox.WARNING
        });
    }
});

if(jibu.locale=='zh_CN') {
    Ext.override(jibu.security.authority.Form, {
        authDetailText:'权限资源信息',
        nameText:'名称',
        valueText:'值',
        submitText:'提交',
        cancelText:'取消',
        waitMsgText:'提交中...'
    });
}

if(jibu.locale=='zh_CN') {
    Ext.override(jibu.security.authority.Grid, {
        nameText:'名称',
        valueText:'值',
        selectColumnText:'选择要查询的列',
        searchTooltip: '查询',
        starTooltip:'加载已绑定数据',
        addTooltip:'增加权限资源',
        editTooltip:'修改权限资源',
        delTooltip:'删除权限资源',
        authAddTitle:'增加权限资源',
        authEditTitle:'修改权限资源',
        delMsgTitle:'删除',
        delMsgText:'确定要永久的删除此数据？'
    });
}


