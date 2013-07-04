Ext.ns('jibu.status.usertype');
jibu.status.usertype={
    0:'其它',
    1:'会员'
};
jibu.status.usertype.get=function(status){
    return this[status];
};
