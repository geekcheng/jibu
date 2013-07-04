if(jibu.layout.classic.HeaderPanel) {
    Ext.override(jibu.layout.classic.HeaderPanel, {
        accountText:'账户',
        accountTooltip:'账户设置',
        exitText:'退出'
    });
}


if(jibu.layout.classic.NavPanel) {
    Ext.override(jibu.layout.classic.NavPanel, {
        findModuleText:'查找一个模块',
        expandText:'全部展开',
        collapseText:'全部收缩'
    });
}

if(jibu.layout.classic.MainPanel) {
    Ext.override(jibu.layout.classic.MainPanel, {
        centerPanelText:'欢迎'
    });
}
