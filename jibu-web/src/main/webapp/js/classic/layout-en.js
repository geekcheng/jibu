if(jibu.layout.classic.HeaderPanel) {
    Ext.override(jibu.layout.classic.HeaderPanel, {
        accountText:'Account',
        accountTooltip:'Account Setting',
        exitText:'Exit'
    });
}


if(jibu.layout.classic.NavPanel) {
    Ext.override(jibu.layout.classic.NavPanel, {
        findModuleText:'Find a Module',
        expandText:'Expand All',
        collapseText:'Collapse All'
    });
}

if(jibu.layout.classic.MainPanel) {
    Ext.override(jibu.layout.classic.MainPanel, {
        centerPanelText:'Welcome'
    });
}
