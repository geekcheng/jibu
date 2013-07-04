Ext.define('jibu.tools.smssender.Panel', {
    extend : 'Ext.form.Panel',
    alias : 'widget.tools.smssender',
    layout: 'absolute',
    url: 'SMSSender.z?ci=smsSingleSend',
    waitMsgTarget : true,
    frame:true,
    initComponent : function() {
        Ext.apply(this, {
            defaultType: 'textfield',
            border: false,
            items: [{
                xtype:'displayfield',
                value:'<h3><span style="color:red;">仅限发送与工作相关的短信，乱发会导致整个短信通道被封，公司会不定期严查发送日志！！</span><span id="msgStatus" style="float:right" class="actionDetail">短信状态:</span></h3>',
                x: 5,
                y: 5,
            	anchor: '-5'  // anchor width by percentage
            },{
                fieldLabel: '手机号',
                fieldWidth: 60,
                msgTarget: 'side',
                allowBlank: false,
                x: 5,
                y: 35,
                name: 'tel',
                anchor: '-5'  // anchor width by percentage
            },
            {
                x:5,
                y: 65,
                xtype: 'textarea',
                style: 'margin:0',
                hideLabel: true,
                name: 'content',
                anchor: '-5 -5'  // anchor width and height
            }],
            buttons:[{
                xtype : 'button',
                scope : this,
                scale : 'large',
                text : '发送短信',
                itemId : 'sendSMS',
                handler : function(){
                    this.getForm().submit({
                        waitMsg : '短信发送中...'
                    });
                }
            }]
        });
        this.callParent(arguments);
   
    },
    render:function(){
        this.callParent(arguments);
        Ext.get('msgStatus').on('click', function(e, t) {
                Ext.Ajax.request({
                url: 'SMSSender.z?ci=getSMSStatus',
                success: function(resp){
                      Ext.get('msgStatus').update('短信状态：'+Ext.JSON.decode(resp.responseText).message);
                }
            });
         }, this);     
    }
});
