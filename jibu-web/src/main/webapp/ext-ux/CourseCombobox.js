Ext.define('jibu.CourseCombo', {
    extend : 'Ext.form.field.ComboBox',
    alias : 'widget.CourseCombo',
    queryMode : 'local',
    filterJianPinKey:'name_jianpin',
    filterNameKey:'name',
    filterId:'course_id',
    initComponent : function() {
        var me = this;
        this.callParent(arguments);
    },
    doQuery : function(queryString, forceAll, rawQuery) {

        queryString = queryString || '';
        var me = this, qe = {
            query : queryString,
            forceAll : forceAll,
            combo : me,
            cancel : false
        }, store = me.store, isLocalMode = me.queryMode === 'local', needsRefresh;

        if (me.fireEvent('beforequery', qe) === false || qe.cancel) {
            return false;
        }

        queryString = qe.query;
        forceAll = qe.forceAll;
        if (forceAll || (queryString.length >= me.minChars)) {
            me.expand();
            if (!me.queryCaching || me.lastQuery !== queryString) {
                me.lastQuery = queryString;
                if (isLocalMode) {
                    store.suspendEvents();
                    needsRefresh = me.clearFilter();
                    if (queryString || !forceAll) {
                        if (/^[0-9]*$/.test(queryString)){
                            me.activeFilter = new Ext.util.Filter({
                                        root : 'data',
                                        property : this.filterId,
                                        value : queryString
                                    });
                            store.filter(me.activeFilter);
                        }else if (/[a-zA-Z0-9]/.test(queryString)) {
                            me.activeFilter = new Ext.util.Filter({
                                        root : 'data',
                                        property : this.filterJianPinKey,
                                        value : new RegExp(queryString)
                                    });
                            store.filter(me.activeFilter);
                        } else {
                            me.activeFilter = new Ext.util.Filter({
                                        root : 'data',
                                        property : this.filterNameKey,
                                        value : new RegExp(queryString)
                                    });
                            store.filter(me.activeFilter);
                        }
                        needsRefresh = true;
                    } else {
                        delete me.activeFilter;
                    }
                    store.resumeEvents();
                    if (me.rendered && needsRefresh) {
                        me.getPicker().refresh();
                    }
                } else {
                    me.rawQuery = rawQuery;
                    if (me.pageSize) {
                        me.loadPage(1);
                    } else {
                        store.load({
                                    params : me.getParams(queryString)
                                });
                    }
                }
            }
            if (me.getRawValue() !== me.getDisplayValue()) {
                me.ignoreSelection++;
                me.picker.getSelectionModel().deselectAll();
                me.ignoreSelection--;
            }
            if (isLocalMode) {
                me.doAutoSelect();
            }
            if (me.typeAhead) {
                me.doTypeAhead();
            }
        }
        return true;
    }
});
