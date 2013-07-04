Ext.define('jibu.security.PM', {
    extend: 'Ext.panel.Panel',
    alias:'widget.system.administration.pm',
    initComponent:function() {
        Ext.apply(this,{
            layout: {
                type: 'hbox',
                align : 'stretch'
            },
            items:[{
                xtype:'system.administration.usergrid',
                flex:2
            },{
                xtype:'system.administration.roletree',
                flex:1
            },{
                xtype:'system.administration.authgrid',
                width:380
            }]

        });
        this.callParent(arguments);

        this.userPanel = this.getComponent(0);
        this.rolePanel = this.getComponent(1);
        this.authPanel = this.getComponent(2);

        /*
         * 一个重要的控制变量。
         * fromwhere =''      : 初始状态。
         * fromwhere ='user'  : 选定了 user，并加载了此user 拥有的 role 和 authority，可以修改 role和user的绑定关系。
         * fromwhere ='role'  : 选定了 role，并加载了此role 拥有的 user 和 aurhority，可以修改 role与user， role与authority的绑定关系。
         * fromwhere ='auth'  : 选定了 authority，并加载了此authority 绑定的 role 和 拥有它的user ，可以修改 role和authority的绑定关系。
         *
         */
        this.fromwhere = '';
        // 保存所有选中的 User Checkbox，即使再次加载也能保留 check状态
        this.checkedUserIds = new Array();
        this.userSM = this.userPanel.getSelectionModel();
        this.userStore = this.userPanel.getStore();
        this.userStarBtn = this.userPanel.getDockedItems()[0].getComponent('user-star-button');

        this.checkedAuthIds = new Array();
        this.authSM = this.authPanel.getSelectionModel();
        this.authStore = this.authPanel.getStore();
        this.authStarBtn = this.authPanel.getDockedItems()[0].getComponent('auth-star-button');

        this.roleStarBtn = this.rolePanel.getDockedItems()[0].getComponent('role-star-button');
        this.roleRefreshBtn = this.rolePanel.getDockedItems()[0].getComponent('role-refresh-button');

        var resetAuthFn = function(authSM,bbar,btn) {
            authSM.suspendEvents();
            // 在 clearSection() 之前必须保证 unlock，做完后为初始状态不用置回 lock
            authSM.setLocked(false);
            authSM.setSelectionMode('SINGLE');
            authSM.deselectAll();
            authSM.resumeEvents();
            authSM.setLocked(false);
            bbar.getComponent('auth-delete-button').disable();
            bbar.getComponent('auth-edit-button').disable();
            btn.disable();
        };
        var resetUserFn = function(userSM,bbar,btn) {
            userSM.suspendEvents();
            // 在 clearSection() 之前必须保证 unlock，做完后为初始状态不用置回 lock
            userSM.setLocked(false);
            userSM.setSelectionMode('SINGLE');
            userSM.deselectAll();
            userSM.resumeEvents();
            userSM.setLocked(false);
            bbar.getComponent('user-delete-button').disable();
            bbar.getComponent('user-edit-button').disable();
            btn.disable();
        };

        var resetRoleFn = function(rp,bbar,btn) {
            rp.suspendEvents();
            var root = rp.store.getRootNode();
            root.cascadeBy(function(n) {
                n.set('checked', false);
            });
            rp.resumeEvents();
            bbar.getComponent('role-delete-button').disable();
            bbar.getComponent('role-edit-button').disable();
            bbar.getComponent('role-add-button').disable();
            rp.getDockedItems()[0].getComponent('role-refresh-button').enable();
            btn.disable();
        };

        this.userSM.on('selectionchange',
                       function(t){
                           var ubbar = this.userPanel.getComponent('user-page-tbar');
                           if(this.fromwhere=='' || this.fromwhere =='user') {
                               if (t.getCount()==1) {  // 单选,下面按钮有效.
                                   ubbar.getComponent('user-delete-button').enable();
                                   ubbar.getComponent('user-edit-button').enable();
                                   this.userStarBtn.enable();

                               } else { // 没有选择或者多选, 下面按钮无效.
                                   ubbar.getComponent('user-delete-button').disable();
                                   ubbar.getComponent('user-edit-button').disable();
                                   this.userStarBtn.disable();
                               }

                               this.fromwhere ='';
                               this.userStarBtn.setIconCls('star-off-icon');
                               this.checkedAuthIds.length=0;
                               resetRoleFn(this.rolePanel,this.rolePanel.getDockedItems()[2], this.roleStarBtn);
                               resetAuthFn(this.authSM, this.authPanel.getDockedItems()[2], this.authStarBtn);

                           } else { // 通过绑定被 check , disable下面按钮.
                               ubbar.getComponent('user-delete-button').disable();
                               ubbar.getComponent('user-edit-button').disable();
                               this.userStarBtn.disable();
                           }

                       },this);

        this.userSM.on('select',
                       function(e, record,rowIndex) {
                           if (this.fromwhere=='role') {
                               var ck = this.rolePanel.getChecked();
                               Ext.Ajax.request(
                                   {
                                       url:'Role.z?ci=bindUser',
                                       params:{
                                           'user.id':record.data.id,
                                           'role.id':ck[0].get('id')
                                       },
                                       method:'POST',
                                       success: function(r,a){
                                       },
                                       failure: function(r,o){
                                       },
                                       scope:this
                                   }
                               );
                           }

                           // 判断本次选择的 row 是否已经在 bindUserIds，如果不在，放入。
                           for(var i=0;i<this.checkedUserIds.length;i++) {
                               if(this.checkedUserIds[i] == record.data.id) {
                                   return;
                               }
                           }
                           this.checkedUserIds.push(record.data.id);
                       },

                       this);

        this.userSM.on('deselect',
                       function(e, record, rowIndex) {
                           if (this.fromwhere=='role') {
                               var ck = this.rolePanel.getChecked();
                               Ext.Ajax.request(
                                   {
                                       url:'Role.z?ci=unbindUser',
                                       params:{
                                           'user.id':record.data.id,
                                           'role.id':ck[0].get('id')
                                       },
                                       method:'POST',
                                       success: function(r,a){
                                       },
                                       failure: function(r,o){
                                       },
                                       scope:this
                                   }
                               );
                           }
                           for(var i=0;i<this.checkedUserIds.length;i++) {
                               if(this.checkedUserIds[i] == record.data.id) {
                                   Ext.Array.remove(this.checkedUserIds,record.data.id);
                                   return;
                               }
                           }
                       },
                       this);


        this.userStore.on('load',
                          function() {
                              var checkedIds = this.checkedUserIds;
                              // 防止 userpanel 的 rowselect 事件发 ajax请求
                              this.rolePanel.suspendEvents();
                              var records = new Array();
                              this.userStore.each(
                                  function(record) {
                                      for(var i=0;i<checkedIds.length;i++) {
                                          if(checkedIds[i] == record.data.id) {
                                              records.push(record);
                                          }
                                      }

                                  });
                              if (this.fromwhere == 'auth') {
                                  this.userSM.setLocked(false);
                                  if(records.length>0)
                                      this.userSM.select(records, true,true);
                                  this.userSM.setLocked(true);
                              } else {
                                  if(records.length>0)
                                      this.userSM.select(records, true,true);
                              }
                              this.rolePanel.resumeEvents();
                          },
                          this);

        this.userStarBtn.on('click',
                            function(n,e){
                                if (this.fromwhere=='' || this.fromwhere=='user') {
                                    Ext.Ajax.request(
                                        {
                                            url:'Role.z?ci=userBindCheck',
                                            params:{
                                                'username':this.userSM.getLastSelected().get('username')},
                                            method:'POST',
                                            success: function(r,a){
                                                var data = Ext.JSON.decode(r.responseText);

                                                //Ext.Msg.alert('信息3',ck.length);
                                                // 在改变role和auth的选择状态之前，把它们的事件suspend，处理完后再恢复。
                                                this.rolePanel.suspendEvents();
                                                this.authSM.suspendEvents();

                                                var root = this.rolePanel.store.getRootNode();
                                                //先把所有role节点取消选中，然后再遍历数，把 id 相等的 node 选中。
                                                var roles = data.roles;
                                                root.cascadeBy(function(n) {
                                                    n.set('checked', false);
                                                    for(var j =0;j<roles.length;j++){
                                                        if(n.get('id') == roles[j])
                                                            n.set('checked', true);
                                                    }
                                                });

                                                // 将匹配的 auth 选中
                                                this.checkedAuthIds = data.auths;
                                                var checkedIds = this.checkedAuthIds;
                                                var records = new Array();
                                                this.authStore.each(
                                                    function(record) {
                                                        for(var i=0;i<checkedIds.length;i++) {
                                                            if(checkedIds[i] == record.data.id) {
                                                                records.push(record);
                                                            }
                                                        }

                                                    });
                                                this.authSM.setLocked(false);
                                                this.authSM.setSelectionMode('SIMPLE');
                                                this.authSM.deselectAll();
                                                if(records.length>0)
                                                    this.authSM.select(records, true);
                                                this.authSM.setLocked(true);

                                                this.rolePanel.resumeEvents();
                                                this.authSM.resumeEvents();

                                                this.userStarBtn.setIconCls('star-icon');
                                                this.roleRefreshBtn.disable();
                                                this.fromwhere = 'user';
                                            },
                                            failure: function(r,o){
                                            },
                                            scope:this
                                        }
                                    );
                                };
                            },
                            this);
        this.authSM.on('selectionchange',
                       function(t){
                           var abbar = this.authPanel.getDockedItems()[2];
                           if(this.fromwhere=='' || this.fromwhere =='auth') {
                               if (t.getCount()==1) {  // 单选,下面按钮有效.
                                   abbar.getComponent('auth-delete-button').enable();
                                   abbar.getComponent('auth-edit-button').enable();
                                   this.authStarBtn.enable();

                               } else { // 没有选择或者多选, 下面按钮无效.
                                   abbar.getComponent('auth-delete-button').disable();
                                   abbar.getComponent('auth-edit-button').disable();
                                   this.authStarBtn.disable();
                               }

                               this.fromwhere ='';
                               this.authStarBtn.setIconCls('star-off-icon');
                               this.checkedAuthIds.length=0;
                               resetRoleFn(this.rolePanel,this.rolePanel.getDockedItems()[2], this.roleStarBtn);
                               // 在 clearSection() 之前必须保证 unlock，做完后为初始状态不用置回 lock
                               this.checkedUserIds.length=0;
                               resetUserFn(this.userSM, this.userPanel.getComponent('user-page-tbar'), this.userStarBtn);
                           } else { // 通过绑定被 check , disable下面按钮.
                               abbar.getComponent('auth-delete-button').disable();
                               abbar.getComponent('auth-edit-button').disable();
                               this.authStarBtn.disable();
                           }
                       },this);

        this.authSM.on('select',
                       function(e,  record,rowIndex) {
                           if (this.fromwhere=='role') {
                               var ck = this.rolePanel.getChecked();
                               Ext.Ajax.request(
                                   {
                                       url:'Role.z?ci=bindAuth',
                                       params:{
                                           'authority.id':record.data.id,
                                           'role.id':ck[0].get('id')
                                       },
                                       method:'POST',
                                       success: function(r,a){
                                       },
                                       failure: function(r,o){
                                       },
                                       scope:this
                                   }
                               );
                           }
                           // 判断本次选择的 row 是否已经在 bindAuthIds，如果不在，放入。
                           for(var i=0;i<this.checkedAuthIds.length;i++) {
                               if(this.checkedAuthIds[i] == record.data.id) {
                                   return;
                               }
                           }
                           this.checkedAuthIds.push(record.data.id);

                       },
                       this);

        this.authSM.on('deselect',
                       function(e, record, rowIndex) {
                           if (this.fromwhere=='role') {
                               var ck = this.rolePanel.getChecked();
                               Ext.Ajax.request(
                                   {
                                       url:'Role.z?ci=unbindAuth',
                                       params:{
                                           'authority.id':record.data.id,
                                           'role.id':ck[0].get('id')
                                       },
                                       method:'POST',
                                       success: function(r,a){
                                                      },
                                       failure: function(r,o){
                                       },
                                       scope:this
                                   }
                               );
                           }
                           for(var i=0;i<this.checkedAuthIds.length;i++) {
                               if(this.checkedAuthIds[i] == record.data.id) {
                                   Ext.Array.remove(this.checkedAuthIds,record.data.id);
                                   return;
                               }
                           }
                       },
                       this);


        this.authStore.on('load',
                          function() {
                              // 防止 authpanel 的 rowselect 事件发 ajax请求
                              var checkedIds = this.checkedAuthIds;
                              this.rolePanel.suspendEvents();
                              var records = new Array();
                              this.authStore.each(
                                  function(record) {
                                      for(var i=0;i<checkedIds.length;i++) {
                                          if(checkedIds[i] == record.data.id) {
                                              records.push(record);
                                          }
                                      }

                                  });
                              if (this.fromwhere == 'user') {
                                  this.authSM.setLocked(false);
                                  if(records.length>0)
                                      this.authSM.select(records, true);
                                  this.authSM.setLocked(true);
                              } else {
                                  if(records.length>0)
                                      this.authSM.select(records, true);
                              }

                              this.rolePanel.resumeEvents();
                          },
                          this);



        this.authStarBtn.on('click',
                            function(n,e){
                                if (this.fromwhere=='' || this.fromwhere=='auth') {
                                    Ext.Ajax.request(
                                        {
                                            url:'Role.z?ci=authBindCheck',
                                            params:{
                                                'id':this.authSM.getLastSelected().get('id')},
                                            method:'POST',
                                            success: function(r,a){
                                                var data = Ext.JSON.decode(r.responseText);
                                                var ck = this.rolePanel.getChecked();
                                                this.rolePanel.suspendEvents();
                                                this.userSM.suspendEvents();

                                                var root = this.rolePanel.store.getRootNode();

                                                //先把所有role节点取消选中，然后再遍历数，把 id 相等的 node 选中。
                                                var roles = data.roles;
                                                root.cascadeBy(function(n) {
                                                    n.set('checked', false);
                                                    for(var j =0;j<roles.length;j++){
                                                        if(n.get('id') == roles[j])
                                                            n.set('checked', true);
                                                    }
                                                });

                                                // 将匹配的 auth 选中
                                                this.checkedUserIds = data.users;
                                                var records = new Array();
                                                this.userStore.each(
                                                    function(record) {
                                                        for(var i=0;i<this.checkedUserIds.length;i++) {
                                                            if(this.checkedUserIds[i] == record.data.id) {
                                                                records.push(record);
                                                            }
                                                        }

                                                    },this);
                                                this.userSM.setLocked(false);
                                                this.userSM.setSelectionMode('SIMPLE');
                                                this.userSM.deselectAll();
                                                if(records.length>0)
                                                    this.userSM.select(records, true);
                                                this.userSM.setLocked(true);

                                                this.rolePanel.resumeEvents();
                                                this.userSM.resumeEvents();

                                                this.authStarBtn.setIconCls('star-icon');
                                                this.roleRefreshBtn.disable();
                                                this.fromwhere = 'auth';
                                            },
                                            failure: function(r,o){
                                                           },
                                            scope:this
                                        }
                                    );
                                };
                            },
                            this);

        this.roleStarBtn.on('click',
                            function(b,e){
                                if (this.fromwhere=='' || this.fromwhere=='role') {
                                    var ck = this.rolePanel.getChecked();
                                    Ext.Ajax.request(
                                        {
                                            url:'Role.z?ci=roleBindCheck',
                                            params:{
                                                'id':ck[0].get('id')},
                                            method:'POST',
                                            success: function(r,a){
                                                var resp = Ext.JSON.decode(r.responseText);
                                                this.checkedUserIds = resp.users;
                                                var recuser = new Array();
                                                this.userStore.each(
                                                    function(record) {
                                                        for(var i=0;i<this.checkedUserIds.length;i++) {
                                                            if(this.checkedUserIds[i] == record.data.id) {
                                                                recuser.push(record);
                                                            }
                                                        }

                                                    },this);
                                                this.checkedAuthIds = resp.auths;
                                                var recauth = new Array();
                                                this.authStore.each(
                                                    function(record) {
                                                        for(var i=0;i<this.checkedAuthIds.length;i++) {
                                                            if(this.checkedAuthIds[i] == record.data.id) {
                                                                recauth.push(record);
                                                            }
                                                        }

                                                    },this);
                                                this.userSM.suspendEvents();
                                                this.authSM.suspendEvents();

                                                this.userSM.setSelectionMode('SIMPLE');
                                                this.userSM.deselectAll();
                                                if(recuser.length>0)
                                                    this.userSM.select(recuser, true);

                                                this.authSM.setSelectionMode('SIMPLE');
                                                this.authSM.deselectAll();
                                                if(recauth.length>0)
                                                    this.authSM.select(recauth, true);

                                                this.userSM.resumeEvents();
                                                this.authSM.resumeEvents();

                                                this.roleStarBtn.setIconCls('star-icon');
                                                this.roleRefreshBtn.disable();
                                                this.fromwhere = 'role';
                                            },
                                            failure: function(r,o){
                                            },
                                            scope:this
                                        }
                                    );
                                }
                            },
                            this);
        this.rolePanel.on('checkchange',
                          function(n,b){
                              var rbbar = this.rolePanel.getDockedItems()[2];
                              // 如果不是在做 bind 操作，只能单选。
                              if (this.fromwhere=='' || this.fromwhere=='role') {
                                  if (b) {
                                      var ck = this.rolePanel.getChecked();
                                      for(var j =0;j<ck.length;j++){
                                          ck[j].set('checked', false);
                                      }
                                      n.set('checked', true);
                                      rbbar.getComponent('role-add-button').enable();
                                      rbbar.getComponent('role-delete-button').enable();
                                      rbbar.getComponent('role-edit-button').enable();
                                      this.roleStarBtn.enable();
                                  } else { // 没有选择或者多选, 下面按钮无效.
                                      rbbar.getComponent('role-add-button').disable();
                                      rbbar.getComponent('role-delete-button').disable();
                                      rbbar.getComponent('role-edit-button').disable();
                                      this.roleStarBtn.disable();
                                  }


                                  this.fromwhere ='';
                                  this.checkedUserIds.length=0;
                                  this.checkedAuthIds.length=0;
                                  resetAuthFn(this.authSM, this.authPanel.getDockedItems()[2], this.authStarBtn);
                                  resetUserFn(this.userSM, this.userPanel.getComponent('user-page-tbar'), this.userStarBtn);
                                  this.roleStarBtn.setIconCls('star-off-icon');
                              } else {
                                  rbbar.getComponent('role-delete-button').disable();
                                  rbbar.getComponent('role-edit-button').disable();
                                  this.roleStarBtn.disable();
                              }

                              if (this.fromwhere =='') {
                                  this.roleRefreshBtn.enable();
                              } else {
                                  this.roleRefreshBtn.disable();
                              }

                              if (this.fromwhere=='user' ) {
                                  var url;
                                  if(b) {
                                      url = 'Role.z?ci=bindUser';
                                  } else {
                                      url = 'Role.z?ci=unbindUser';
                                  }

                                  Ext.Ajax.request({
                                          url:url,
                                          params:{
                                              'user.id':this.userSM.getLastSelected().get('id'),
                                              'role.id':n.get('id')
                                          },
                                          method:'POST',
                                          success: function(r,a){
                                              var data = Ext.JSON.decode(r.responseText);
                                              this.checkedAuthIds = data.auths;
                                              var checkedIds = this.checkedAuthIds;
                                              var records = new Array();

                                              if(checkedIds){
                                                  this.authStore.each(
                                                      function(record) {
                                                          for(var i=0;i<checkedIds.length;i++) {
                                                              if(checkedIds[i] == record.data.id) {
                                                                  records.push(record);
                                                              }
                                                          }

                                                      });
                                              }
                                              this.authSM.setLocked(false);
                                              this.authSM.setSelectionMode('SIMPLE');
                                              this.authSM.deselectAll();
                                              if(records.length>0)
                                                  this.authSM.select(records, true);
                                              this.authSM.setLocked(true);
                                          },
                                          failure: function(r,o){
                                          },
                                          scope:this
                                      });

                              }

                              if (this.fromwhere=='auth') {
                                  var url;
                                  if(b) {
                                      url = 'Role.z?ci=bindAuth';
                                  } else {
                                      url = 'Role.z?ci=unbindAuth';
                                  }
                                  Ext.Ajax.request({
                                          url:url,
                                          params:{
                                              'authority.id':this.authSM.getLastSelected().get('id'),
                                              'role.id':n.get('id')
                                          },
                                          method:'POST',
                                          success: function(r,a){
                                              var data = Ext.JSON.decode(r.responseText);
                                              this.checkedUserIds = data.users;
                                              var checkedIds = this.checkedUserIds;
                                              var records = new Array();
                                              this.userStore.each(
                                                  function(record) {
                                                      for(var i=0;i<checkedIds.length;i++) {
                                                          if(checkedIds[i] == record.data.id) {
                                                              records.push(record);
                                                          }
                                                      }

                                                  });
                                              this.userSM.setLocked(false);
                                              this.userSM.setSelectionMode('SIMPLE');
                                                this.userSM.deselectAll();
                                                if(records.length>0)
                                                    this.userSM.select(records, true);
                                                this.userSM.setLocked(true);
                                          },
                                          failure: function(r,o){
                                          },
                                          scope:this
                                      });
                              }
                          },
                          this);

    }
});
