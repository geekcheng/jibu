Ext.define('jibu.common.setting.Model',{
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'int'},
        {name:'value', type:'string'}
    ]
});


Ext.define('jibu.common.setting.Form',{
    extend:'Ext.form.Panel',
    alias:'widget.common.setting',
    fullName : 'Full Name',
    email : 'E-mail',
    userName: 'User Name',
    password: 'New Password',
    passwordRepeat: 'Re-enter New Password',
    userInformation:'Account Information',
    settings: 'Preference Settings',
    theme : 'Theme',
    language : 'Language',
    fl_layout : 'Layout',
    confirmPassword: 'Passwords do not match',
    passwordInfo: 'Password',
    oldPassword: 'Current Password',
    submitText: 'Submit',
    waitMsgText: 'Submitting...',
    constructor: function(config) {
        this.initConfig(Ext.apply(config,{waitMsgTarget:true}));
        this.callParent(arguments);
    },
    initComponent:function() {
        Ext.apply(this,{
            bodyStyle:'padding:5px 5px 0',
            fieldDefaults:{
                labelWidth: 200
            },
            defaults:{
                anchor:'100%'
            },
            frame:true,
            cls:'form-frame',
            items: [
                {
                    xtype:'fieldset',
                    title: this.userInformation,
                    collapsible: true,
                    autoHeight:true,
                    defaultType: 'textfield',
                    items :[

                        {
                            name: 'User.id',
                            xtype:'hidden'
                        },{
                            fieldLabel: this.fullName,
                            name: 'User.fullname',
                            allowBlank:false
                        },{
                            fieldLabel: this.email,
                            name: 'User.emailaddress',
                            vtype:'email'
                        },{
                            fieldLabel: this.userName,
                            name: 'User.username',
                            allowBlank:false,
                            disabled:true
                        }
                    ]
                },{
                    xtype:'fieldset',
                    title: this.passwordInfo,
                    collapsible: true,
                    autoHeight:true,
                    defaultType: 'textfield',
                    items :[
                        {
                            fieldLabel: this.oldPassword,
                            name: 'oldpassword',
                            allowBlank:false,
                            inputType: 'password'
                        }, {
                            fieldLabel: this.password,
                            id :'setting_newpassword',
                            name: 'User.password',
                            inputType: 'password'
                        }, {
                            fieldLabel: this.passwordRepeat,
                            name: 'passwordAgain',
                            inputType: 'password',
                            vtype: 'password',
                            initialPassField:'setting_newpassword'
                        }
                    ]
                },{
                    xtype:'fieldset',
                    title: this.settings,
                    collapsible: true,
                    autoHeight:true,
                    defaultType: 'textfield',
                    items :[
                        Ext.create('Ext.form.field.ComboBox',{
                            store:Ext.create('Ext.data.Store',{
                                model:'jibu.common.setting.Model',
                                proxy:{
                                    type:'ajax',
                                    url:'Setting.y?ci=settingLoad',
                                    root:'settings',
                                    extraParams:{
                                        'settings.name' :'language'
                                    },
                                    reader: {
                                        type: 'json',
                                        root: 'settings'
                                    }
                                }
                            }),
                            displayField:'value',
                            editable:false,
                            valueField:'id',
                            id:'setting_language',
                            name:'settings.id',
                            triggerAction:'all',
                            fieldLabel: this.language,
                            selectOnFocus:true
                        }),
                        Ext.create('Ext.form.field.ComboBox',{
                            store:Ext.create('Ext.data.Store',{
                                model:'jibu.common.setting.Model',
                                proxy:{
                                    type:'ajax',
                                    url:'Setting.y?ci=settingLoad',
                                    root:'settings',
                                    extraParams:{
                                        'settings.name' :'layout'
                                    },
                                    reader: {
                                        type: 'json',
                                        root: 'settings'
                                    }
                                }
                            }),
                            displayField:'value',
                            editable:false,
                            valueField:'id',
                            id:'setting_layout',
                            name:'settings.id',
                            triggerAction:'all',
                            fieldLabel: this.fl_layout,
                            selectOnFocus:true
                        }),
                        Ext.create('Ext.form.field.ComboBox',{
                            store:Ext.create('Ext.data.Store',{
                                model:'jibu.common.setting.Model',
                                proxy:{
                                    type:'ajax',
                                    url:'Setting.y?ci=settingLoad',
                                    root:'settings',
                                    extraParams:{
                                        'settings.name' :'theme'
                                    },
                                    reader: {
                                        type: 'json',
                                        root: 'settings'
                                    }
                                }
                            }),
                            displayField:'value',
                            editable:false,
                            valueField:'id',
                            id:'setting_theme',
                            name:'settings.id',
                            triggerAction:'all',
                            fieldLabel:this.theme,
                            selectOnFocus:true
                        })
                    ]

                }],
            buttonAlign:'center',
            buttons: [{
                id:'setting_submitBtn',
                text: this.submitText,
                scope:this,
                formBind:true,
                handler:function() {
                        this.getForm().submit({
                            url: 'Setting.y?ci=settingUpdate',
                            method: 'POST',
                            waitMsg: this.waitMsgText,
                            success: function(form, action) {
                            },
                            failure: function(form, action) {
                            }
                        });
                }
            }],
            listeners:{
                render:function(){
                    this.getForm().load(
                        {
                            url: 'Setting.y?ci=formLoad',
                            success:function(f,a){
                                var settings = a.result.settings;
                                // f.setValues() 会把显示值和实际值都置为 settings[i].value
                                // 需要将下拉框的实际值置为 settings[i].id，而且必须在 f.setValues() 之后
                                for (var i=0;i<settings.length;i++) {
                                    f.setValues([{id:'setting_'+settings[i].name,value:settings[i].value}]);
                                    Ext.getCmp('setting_'+settings[i].name).value=settings[i].id;
                                }

                            }
                            
                        });
                }
            }

        });
        this.callParent(arguments);
        Ext.apply(Ext.form.field.VTypes, {
            password: function(val, field) {
                if (field.initialPassField) {
                    var pwd = field.up('form').down('#' + field.initialPassField);
                    return (val == pwd.getValue());
                }
                return true;
            },
            passwordText: this.confirmPassword
        });
        
    }
});

if(jibu.locale=='zh_CN') {
    Ext.override(jibu.common.setting.Form, {
        fullName : '全名',
        email : '电子邮件',
        userName: '用户名',
        password: '新密码',
        passwordRepeat: '再次输入新密码',
        userInformation:'账户信息',
        settings: '喜好设置',
        theme : '主题',
        language : '语言',
        fl_layout : '布局',
        confirmPassword: '两次输入的密码不匹配',
        passwordInfo: '密码',
        oldPassword: '当前密码',
        submitText: '提交',
        waitMsgText: '提交中...'
    });
}
