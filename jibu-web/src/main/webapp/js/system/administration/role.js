Ext.define('jibu.security.role.Form',{
    extend:'Ext.form.Panel',
    alias:'widget.system.administration.roleform',
    roleDetailText:'Role Detail',
    parentNameText:'Parent Name',
    nameText:'Name',
    descriptionText:'Description',
    submitText:'Submit',
    cancelText:'Cancel',
    waitMsgText:'Submitting...',
    initComponent:function() {
        Ext.apply(this,{
            frame: true,
            items : [{
                xtype: 'fieldset',
                labelWidth: 150,
                title: this.roleDetailText,
                collapsible: true,
                defaults: {width: 310},
                defaultType: 'textfield',
                autoHeight: true,
                items: [{
                    xtype: 'hidden',
                    name: 'Role.id',
                    allowBlank:true
                },{
                    xtype: 'hidden',
                    name: 'pid',
                    allowBlank:true
                },{
                    fieldLabel: this.parentNameText,
                    name: 'pname',
                    allowBlank:true,
                    disabled:true
                },{
                    fieldLabel: this.nameText,
                    name: 'Role.name',
                    allowBlank:false
                },{
                    xtype: 'textarea',
                    fieldLabel: this.descriptionText,
                    name: 'Role.description',
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
        var rid = this.getForm().findField("Role.id").getValue();
        var url;
        if (rid.length > 0) {
            url = 'Role.z?ci=roleUpdate';
        } else {
            url = 'Role.z?ci=roleAdd';
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


Ext.define('jibu.security.role.CheckTree', {
    extend: 'Ext.tree.Panel',
    alias:'widget.system.administration.roletree',
    refreshTooltip:'Reload role tree',
    starTooltip:'Load binded data.',
    addTooltip:'Add Role',
    editTooltip:'Edit Role',
    delTooltip:'Delete Role',
    roleAddTitle:'Role Add',
    roleEditTitle:'Role Edit',
    delMsgTitle:'Delete',
    delMsgText:'Are you sure you want to permanently delete the data?',
    initComponent:function() {
        Ext.apply(this,{
            rootVisible:false,
            autoScroll:true,
            collapseMode:"mini",
            collapseFirst:false,
            height:300,
            store:Ext.create('Ext.data.TreeStore'),
            tbar:['->',{
                itemId: 'role-refresh-button',
                xtype: 'button',
                iconCls: 'refresh-icon',
                tooltip: this.refreshTooltip,
                scope: this,
                handler:this.loadRoleFn
            },{
                itemId: 'role-star-button',
                iconCls: 'star-off-icon',
                disabled:true,
                tooltip: this.starTooltip
            }],
            bbar : [{
                id:'role-add-button',
                tooltip:this.addTooltip,
                iconCls: 'add-icon',
                disabled:true,
                scope: this,
                handler: this.roleAddFn
            },{
                id:'role-edit-button',
                tooltip: this.editTooltip,
                iconCls: 'edit-icon',
                scope: this,
                disabled:true,
                handler: this.roleEditFn
            },{
                id:'role-delete-button',
                tooltip:this.delTooltip,
                iconCls: 'delete-icon',
                disabled:true,
                scope: this,
                handler: this.roleDeleteFn
            }],
            
            listeners : {
                render: function(n) {
                    this.loadRoleFn();
                }
            }

        });
        this.callParent(arguments);
    },
    loadRoleFn : function(){
        Ext.Ajax.request({
            url:'Role.z?ci=getAllRole',
            method:'POST',
            success: function(r,a){
                //Ext.Msg.alert('信息2',r.responseText);
                var data = Ext.JSON.decode(r.responseText);
                var root = this.store.getRootNode();
                root.removeAll();
                root.appendChild(data);
                root.expandChildren(true);
            },
            failure: function(r,o){
            },
            scope:this
        });
        
    },
    roleAddFn: function(btn,event){
        var ck = this.getChecked();
        Ext.createWidget('window',{
            title: this.roleAddTitle,
            width:500,
            height:300,
            border:false,
            plain: true,
            layout:'fit',
            items:[{
                xtype:'system.administration.roleform',
                waitMsgTarget:true
            }],
            listeners:{
                show:function(){
                    this.getComponent(0).getForm().findField("pid").setValue(ck[0].get('id'));
                    this.getComponent(0).getForm().findField("pname").setValue(ck[0].get('text'));
                }
            }
        }).show();
        
    },
    roleEditFn: function(btn,event){
        var ck = this.getChecked();
        var win = new Ext.Window({
            title: this.roleEditTitle,
            width:500,
            height:300,
            border:false,
            plain: true,
            layout:'fit',
            items:[{
                xtype:'system.administration.roleform',
                waitMsgTarget:true
            }],
            listeners:{
                show:function(){
                    var data = [{id:'Role.id',value:ck[0].get('id')},
                                {id:'Role.name',value:ck[0].get('text')},
                                {id:'Role.description',value:ck[0].get('qtip')}
                               ];
                    this.getComponent(0).getForm().setValues(data);
                    this.getComponent(0).getForm().findField("pname").destroy();
                }
            }
        });
        win.show();
    },
    roleDeleteFn: function(btn,event){
        var ck = this.getChecked();
        var roleDelAjaxFn = function(btn) {
            if (btn == 'yes') {
                Ext.Ajax.request(
                    {
                        url:'Role.z?ci=roleDelete',
                        params:{
                            'id':ck[0].get('id')
                        },
                        method:'POST',
                        success: function(r,a){
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
            fn: roleDelAjaxFn,
            icon: Ext.MessageBox.WARNING
        });
    }
});

if(jibu.locale=='zh_CN') {
    Ext.override(jibu.security.role.Form, {
        roleDetailText:'角色信息',
        parentNameText:'父角色名称',
        nameText:'角色名',
        descriptionText:'描述',
        submitText:'提交',
        cancelText:'取消',
        waitMsgText:'提交中...'
    });
}

if(jibu.locale=='zh_CN') {
    Ext.override(jibu.security.role.CheckTree, {
        refreshTooltip:'重新加载角色树',
        starTooltip:'加载已绑定数据',
        addTooltip:'增加角色',
        editTooltip:'修改角色',
        delTooltip:'删除角色',
        roleAddTitle:'增加角色',
        roleEditTitle:'修改角色',
        delMsgTitle:'删除',
        delMsgText:'确定要永久的删除此数据？'
    });
}
