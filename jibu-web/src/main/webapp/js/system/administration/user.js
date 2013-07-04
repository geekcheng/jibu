Ext.define('jibu.security.user.Form',{
    extend:'Ext.form.Panel',
    alias:'widget.system.administration.userform',
    userDetailText: 'User Detail',
    usernameText: 'Username',
    fullnameText:'Full Name',
    emailText: 'E-mail',
    passwordText:'Password',
    userTypeText:'Type',
    enabledText:'Enabled',
    submitText:'Submit',
    cancelText:'Cancel',
    waitMsg:'Submitting...',
    initComponent:function() {
        Ext.apply(this,{
            frame: true,
            items : [{
                xtype: 'fieldset',
                labelWidth: 150,
                title: this.userDetailText,
                collapsible: true,
                defaults: {width: 310},
                defaultType: 'textfield',
                autoHeight: true,
                items: [{
                    xtype: 'hidden',
                    name: 'User.id',
                    allowBlank:true
                },{
                    fieldLabel: this.usernameText,
                    name: 'User.username',
                    allowBlank:false
                },{
                    fieldLabel: this.fullnameText,
                    name: 'User.fullname',
                    allowBlank:false
                },{
                    fieldLabel: this.emailText,
                    name: 'User.emailaddress',
                    vtype:'email'
                },{
                    fieldLabel: this.passwordText,
                    name: 'User.password',
                    inputType: 'password'
                },{
                    fieldLabel: this.userTypeText,
                    name: 'User.type',
                    xtype: 'combo',
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'id',
                    editable:false,
                    store:Ext.create('Ext.data.Store', {
                        fields: ['id', 'name'],
                        data : [
                            {id:0, name:jibu.status.usertype.get(0)},
                            {id:1, name:jibu.status.usertype.get(1)}
                        ]
                    })
                },{
                    xtype: 'radiogroup',
                    fieldLabel: this.enabledText,
                    name: 'User.enabled',
                    items: [{
                        inputValue: 'true',
                        boxLabel: 'Yes',
                        checked:true,
                        name: 'User.enabled'

                    }, {
                        inputValue: 'false',
                        boxLabel: 'No',
                        name: 'User.enabled'
                    }],
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
        var uid = this.getForm().findField('User.id').getValue();
        var url;
        if (uid.length > 0) {
            url = 'User.z?ci=userUpdate';
        } else {
            url = 'User.z?ci=userAdd';
        }

        this.getForm().submit(
            {
                url: url,
                method: 'POST',
                disabled:true,
                waitMsg: this.waitMsg,
                success: function(form, action) {
                    Ext.Msg.alert('Success', action.result.message);
                },
                failure: function(form, action) {
                }
            }
        );
    }
});

Ext.define('jibu.security.user.Model',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int'},
        {name: 'username', type: 'string'},
        {name: 'password', type: 'string'},
        {name: 'fullname', type: 'string'},
        {name: 'emailaddress', type: 'string'},
        {name: 'type', type: 'int'},
        {name: 'enabled', type: 'bool'}
    ]
});

Ext.define('jibu.security.user.Grid', {
    extend: 'Ext.grid.Panel',
    alias:'widget.system.administration.usergrid',
    usernameText: 'Username',
    fullnameText:'Full Name',
    emailaddressText:'E-mail',
    enabledText:'Enabled',
    selectColumnText:'Select Within',
    searchTooltip: 'Search',
    starTooltip:'Load binded data.',
    addTooltip:'Add User',
    editTooltip:'Edit User',
    delTooltip:'Delete User',
    userAddTitle:'User Add',
    userEditTitle:'User Edit',
    delMsgTitle:'Delete',
    userTypeText:'Type',
    delMsgText:'Are you sure you want to permanently delete the data?',

    initComponent:function() {

            var store = Ext.create('Ext.data.JsonStore',{
                autoLoad: true,
                pageSize: 50,
                model:'jibu.security.user.Model',
                remoteSort : true,
                proxy:{
                    type:'ajax',
                    url: 'User.z?ci=userFind',
                    reader:{
                        root: 'data'
                    },
                    simpleSortMode: true
                },
                sorters: [{
                    property:'username', direction:'ASC'
                }]
            });
            Ext.apply(this,{
            selModel: Ext.create('Ext.selection.CheckboxModel',{
                mode: 'SINGLE',
                allowDeselect: true,
                showHeaderCheckbox: false
            }),
            store:store,
            columns: [
                {xtype: 'rownumberer'},
                {header: this.usernameText, width: 80, sortable: true, dataIndex: 'username'},
                {header: this.fullnameText, width: 120, sortable: true,  dataIndex: 'fullname'},
                {header: this.emailaddressText, width: 160, sortable: true,  dataIndex: 'emailaddress'},
                {header: this.userTypeText,  width: 70, sortable: true, dataIndex: 'type',renderer:function(v){return jibu.status.usertype.get(v);}},
                {header: this.enabledText,  width: 70, sortable: true, dataIndex: 'enabled'}
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
                            ['User.username', this.usernameText],
                            ['User.fullname', this.fullnameText],
                            ['User.emailaddress', this.emailaddressText]
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
                    handler : this.searchUserFn
                },'->',{
                    itemId: 'user-star-button',
                    iconCls: 'star-off-icon',
                    disabled:true,
                    tooltip: this.starTooltip
                }
                      ]}],
            bbar: Ext.create('Ext.PagingToolbar',{
                itemId:'user-page-tbar',
                store: store,
                displayInfo: false,
                items:['-',
                       {
                           iconCls: 'add-icon',
                           tooltip: this.addTooltip,
                           handler: this.userAddFn,
                           scope: this
                       },{
                           itemId:'user-edit-button',
                           tooltip: this.editTooltip,
                           iconCls: 'edit-icon',
                           scope: this,
                           disabled:true,
                           handler: this.userEditFn
                       },{
                           itemId:'user-delete-button',
                           tooltip: this.delTooltip,
                           iconCls: 'delete-icon',
                           scope: this,
                           disabled:true,
                           handler: this.userDeleteFn
                       }]

            })

        });
        this.callParent(arguments);
    },

    searchUserFn : function(btn,event){
        this.getStore().currentPage = 1;
        this.getStore().removeAll();
        var key = this.getDockedItems()[0].getComponent('searchType').getValue();
        var value = this.getDockedItems()[0].getComponent('searchValue').getValue();
        var params = {};
        params[key]=value;
        var ep = this.getStore().proxy.extraParams;
        delete ep['User.username'];
        delete ep['User.fullname'];
        delete ep['User.emailaddress'];
        Ext.apply(ep,params);
        this.getStore().load({
            scope:this,
            params:params,
            callback:function(records,o,success){
            }
        });
    },

    userAddFn: function(btn,event){
        Ext.createWidget('window',{
            title: this.userAddTitle,
            width:500,
            height:300,
            border:false,
            plain: true,
            layout:'fit',
            items:[{
                xtype:'system.administration.userform',
                waitMsgTarget:true
            }],
            listeners:{
                show:function(){
                    this.addPanel = this.getComponent(0);
                }
            }
        }).show();
    },
    userEditFn: function(btn,event){
        var record = this.getSelectionModel().getLastSelected();
        Ext.createWidget('window',{
            title: this.userEditTitle,
            width:500,
            height:300,
            border:false,
            plain: true,
            layout:'fit',
            items:[{
                xtype:'system.administration.userform',
                waitMsgTarget:true
            }],
            listeners:{
                show:function(){
                    var data = [{id:'User.id',value:record.get('id')},
                                {id:'User.username',value:record.get('username')},
                                {id:'User.fullname',value:record.get('fullname')},
                                {id:'User.password',value:record.get('password')},
                                {id:'User.emailaddress',value:record.get('emailaddress')},
                                {id:'User.type',value:record.get('type')},
                                {id:'User.enabled',value:record.get('enabled')}
                               ];
                    this.getComponent(0).getForm().setValues(data);
                    //this.getComponent(0).getForm().findField('User.username').disable();
                }
            }
        }).show();
    },


    userDeleteFn: function(btn,event){
        var record = this.getSelectionModel().getLastSelected();
        var store = this.getStore();
        var userDelAjaxFn = function(btn) {
            if (btn == 'yes') {
                Ext.Ajax.request(
                    {
                        url:'User.z?ci=userDelete',
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
            fn: userDelAjaxFn,
            icon: Ext.MessageBox.WARNING
        });

    }
});

if(jibu.locale=='zh_CN') {
    Ext.override(jibu.security.user.Form, {
        userDetailText: '用户信息',
        usernameText: '用户名',
        fullnameText:'全名',
        emailText: '电子邮件',
        passwordText:'密码',
        userTypeText:'用户类型',
        enabledText:'是否有效',
        submitText:'提交',
        cancelText:'取消',
        waitMsg:'提交中...'
    });
}

if(jibu.locale=='zh_CN') {
    Ext.override(jibu.security.user.Grid, {
        usernameText: '用户名',
        fullnameText:'全名',
        emailaddressText:'电子邮件',
        enabledText:'是否有效',
        selectColumnText:'选择要查询的列',
        searchTooltip: '查询',
        starTooltip:'加载已绑定数据',
        addTooltip:'增加用户',
        editTooltip:'修改用户',
        delTooltip:'删除用户',
        userAddTitle:'增加用户',
        userEditTitle:'修改用户',
        userTypeText:'类型',
        delMsgTitle:'删除',
        delMsgText:'确定要永久的删除此数据？'
    });
}
