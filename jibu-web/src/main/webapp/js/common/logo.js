Ext.define('jibu.logo', {
    extend : 'Ext.draw.Component',
    alias : 'widget.logo',
    constructor : function(config) {
        this.items = [{
            type : 'image',
            src: "images/logo-login.png",
            width: 122,
            height: 30
        }];
        if (this.items) {
            Ext.apply(config, {
                width: 122,
                height: 30,
                items : this.items
            });
        }
        this.callParent([config]);
    }
});
