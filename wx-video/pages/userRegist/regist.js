const app = getApp()  /**全局对象 */

Page({
    data: {

    },
    
    doRegist : function(e){
      var formObject = e.detail.value;
      var username = formObject.username;
      var password = formObject.password;

      // 简单验证
      if(username.length == 0 || password.length == 0){
        wx.showToast({  //这个方法可以进行弹窗
          title: '用户名或密码不能为空',
          icon:'none',  //图片默认没有
          duration:3000 //设置延时为3s
        })
      }else{
        //提高用户体验, 调用后端的过程中让用户等待一下, 在success方法中收回
        wx.showLoading({
          title: '等一下哦...',
        });
        var serverUrl = app.serverUrl;
        wx.request({
          url: serverUrl + '/regist',
          method: "POST",
          data: {
            username: username,
            password: password
          },
          header: {
            'content-type': 'application/json' // 默认值
          },
          success: function(res){
            console.log(res.data);
            wx.hideLoading();//收起等待框
            var status = res.data.status;
            if (status == 200){
              wx.showToast({  //这个方法可以进行弹窗
                title: '注册成功(*^_^*)',
                icon: 'none',  //图片默认没有
                duration: 3000 //设置延时为3s
              }),
                // app.userInfo = res.data.data; //配置全局变量UserInfo, 有点session的感觉
                //fixme 修改原有的全局对象为本地缓存
                app.setGlobalUserInfo(res.data.data);

            }else if(status == 500){
              wx.showToast({  //这个方法可以进行弹窗
                title: res.data.msg,
                icon: 'none',  //图片默认没有
                duration: 3000 //设置延时为3s
              })
            }
          }
        })
      }
  },
  goLoginPage: function () {
    wx.redirectTo({
      url: '../userLogin/login',
    })
  }
})