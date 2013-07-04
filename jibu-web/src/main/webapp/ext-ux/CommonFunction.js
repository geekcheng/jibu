Ext.define('jibu.CommonFunction', {
    /**
     * 调用该方法，需要加入下面两个配置项
     * actionTipWidth : 400,
     * actionTipUrl : 'TeeTimeBooking.z?ci=getActionHistory&bookNo=',
     * status : {
     *   M10 : '预订',
     *   M20 : '预订成功',
     *   M30 : '退订',
     *   M40 : '已签到',
     *   M50 : '缺席',
     *   M0 : '删除'
     *  }
     * @param {} view
     */
    showActionHistory : function(view) {
        var me = this;
        
        var tId;
        view.tip = Ext.create('Ext.tip.ToolTip', {
            target : view.el,
            delegate : 'span.actionDetail',
            // trackMouse : true,
            anchor : 'right',
            width : me.actionTipWidth,
            disabled : true,
            dismissDelay : 0,
            showDelay : 0,
            renderTo : Ext.getBody(),
            listeners : {
                beforeshow : function updateTipBody(tip) {
                    Ext.Ajax.request({
                        url : me.actionTipUrl + tId,
                        success : function(response) {
                            var text = response.responseText;
                            var s = '<h1>操作记录</h1>';
                            Ext.each(Ext.decode(text).data, function(o, idx) {
                                s += Ext.String.format('<div><B>' + (idx + 1)
                                                + '.{0} {1} {2}</B></div>',
                                        o.fullname, o.action_ts,
                                        me.status['M' + o.status]);
                                if (o.description) {
                                    Ext.each(o.description.split(','),
                                            function(v) {
                                                s += '<div style="text-indent:1em;">'
                                                        + v + '</div>';
                                            });
                                }
                            });
                            tip.update(s);
                        }
                    });
                },
                hide : function() {
                    view.tip.setDisabled(true);
                    view.tip.update('');
                }
            }
        });
        view.getEl().on('click', function(e, t) {
                    view.tip.setDisabled(false);
                    tId = t.id;
                    view.tip.triggerElement = t;
                    view.tip.clearTimer('hide');
                    view.tip.targetXY = e.getXY();
                    view.tip.delayShow();
                }, view, {
                    delegate : 'span.actionDetail'
                });
    }
})

