Ext.define('jibu.common.login.Form', {
            extend : 'Ext.window.Window',
            initComponent : function() {
                var config = {
                    title : this.reLogin,
                    draggable : false,
                    modal : true,
                    id : 'windowLogin',
                    width : 360,
                    minWidth : 350,
                    height : 190,
                    bodyStyle : 'padding: 5px;',
                    items : [{
                                xtype : 'form',
                                waitMsgTarget : true,
                                url : 'Login.x?ci=loginAjax',
                                layout : 'anchor',
                                frame : true,
                                padding : '20',
                                buttonAlign : 'center',
                                defaults : {
                                    anchor : '90%'
                                },
                                fieldDefaults : {
                                    labelAlign : 'right',
                                    labelWidth : 80
                                },
                                defaultType : 'textfield',
                                items : [{
                                            fieldLabel : this.userName,
                                            name : 'username',
                                            value : jibu.username,
                                            allowBlank : false
                                        }, {
                                            fieldLabel : this.password,
                                            inputType : 'password',
                                            name : 'password',
                                            allowBlank : false,
                                            listeners : {
                                                scope : this,
                                                specialkey : function(o, e) {
                                                    if (e.getCharCode() == e.ENTER) {
                                                        this.loginSubmit();
                                                    }
                                                }
                                            }
                                        }],
                                buttons : [{
                                            text : this.login,
                                            formBind : true,
                                            scope:this,
                                            disabled : true,
                                            handler : this.loginSubmit
                                        }]
                            }]
                };
                Ext.apply(this, config);
                this.callParent(arguments);
                this.on({
                            show : {
                                fn : function() {
                                    this.down('form').getForm()
                                            .findField('password').focus(false,
                                                    200);
                                },
                                scope : this,
                                single : true
                            }
                        });
            },
            loginSubmit : function() {
                var form = this.down('form').getForm();
                var window = this;
                if (form.isValid()) {
                    form.submit({
                                waitMsg : 'Login...',
                                success : function(form, action) {
                                    window.close();
                                },
                                failure : function(form, action) {
                                }
                            });
                }
            }

        });

if (jibu.locale == 'en') {
    Ext.override(jibu.common.login.Form, {
                login : 'Login',
                userName : 'User Name',
                password : 'Password',
                reLogin : 'Session expired, please relogin!'
            });
} else {
    Ext.override(jibu.common.login.Form, {
                login : '登录',
                userName : '用户名',
                password : '密码',
                reLogin : '由于长时间未操作，请重新登录'
            });
}

Ext.Ajax.on('requestcomplete', function(all, resp) {
    var sb = Ext.getCmp('msg-statusbar');
    var data = {};

    if (!resp.getResponseHeader || resp.getResponseHeader('Content-type') == 'application/json;charset=UTF-8') {
        data = Ext.JSON.decode(resp.responseText);
    }
    if (data.message == 'expired') {
        if (!Ext.getCmp('windowLogin')) {
            Ext.create('jibu.common.login.Form').show();
        }
    } else if (data.message) {
        var msg;
        if (Ext.isString(data.message)){
            msg = data.message;
        } else if (data.message.message){
            msg = data.message.message;
        }
        if (data.success && msg) {
            sb.setStatus({
                        text : '<span style="color:green;font-weight:bold;">'
                                + msg + '</span>',
                        iconCls : 'x-status-valid',
                        clear : false
                    });
        } else if (msg) {
            sb.setStatus({
                        text : '<span style="color:red;font-weight:bold;">'
                                + msg + '</span>',
                        iconCls : 'x-status-error',
                        clear : false
                    });
        }
    } else {
//    	 sb.setStatus({
//                        text : '',
//                        iconCls : '',
//                        clear : false
//                    });
    }

}, this);

Ext.Ajax.on('requestexception', function() {
            if (arguments[1].responseText) {
                win = Ext.create('widget.window', {
                            title : '异常信息',
                            closable : true,
                            closeAction : 'hide',
                            width : 600,
                            autoScroll : true,
                            minWidth : 350,
                            height : 350,
                            html : arguments[1].responseText,
                            bodyStyle : 'padding: 5px;'
                        });
                win.show();
            } else {
                Ext.Msg.alert('重要提示', '服务器维护中，请稍候登录');
            }
        }, this);