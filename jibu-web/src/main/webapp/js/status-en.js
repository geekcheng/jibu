Ext.ns('jibu.status.usertype');

jibu.status.usertype.get=function(status){
    var s = {
        0:'other',
        1:'member'
    }; 
    return s[status];
};
