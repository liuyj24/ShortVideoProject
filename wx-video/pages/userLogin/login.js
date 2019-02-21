const app = getApp()

Page({
  data: {
  },

  onLoad: function(params){
    var me = this;
    var redirectUrl = params.redirectUrl;
    //对拿到的字符串进行转换
    redirectUrl = redirectUrl.replace(/#/g, "?");
    redirectUrl = redirectUrl.replace(/@/g, "=");

    me.redirectUrl = redirectUrl;
  },

  doLogin : function(e){
    var me = this;
    var formObject = e.detail.value;
    var username = formObject.username;
    var password = formObject.password;
    //简单认证
    if(username.length == 0 || password == 0){
      wx.showToast({  //这个方法可以进行弹窗
        title: '用户名或密码不能为空',
        icon: 'none',  //图片默认没有
        duration: 3000 //设置延时为3s
      })
    }else{
      var serverUrl = app.serverUrl;
      //提高用户体验, 调用后端的过程中让用户等待一下, 在success方法中收回
      wx.showLoading({
        title: '正在登陆(*^_^*)',
      });
      //调用后端
      wx.request({
        url: serverUrl + '/login',
        method: "POST",
        data: {
          username : username,
          password : password
        },
        header: {
          'content-type': 'application/json' // 默认值
        },
        success : function(res){
          console.log(res.data);
          wx.hideLoading();//收起等待框
          if(res.data.status == 200){
            //登陆成功
            wx.showToast({  //这个方法可以进行弹窗
              title: '登陆成功(*^_^*)',
              icon: 'success',
              duration: 3000, //设置延时为3s
            })
            //页面跳转
            var redirectUrl = me.redirectUrl;
            if (redirectUrl != null && redirectUrl != "" && redirectUrl != undefined){
              wx.redirectTo({
                url: redirectUrl,
              })
            }else{
              wx.redirectTo({
                url: '../index/index',
              })
            }
            // app.userInfo = res.data.data; //保存好用户信息.
            //fixme 修改原有的全局对象为本地缓存
            app.setGlobalUserInfo(res.data.data);
          }else{
            wx.showToast({  //这个方法可以进行弹窗
              title: res.data.msg,
              icon: 'none',  //图片默认没有
              duration: 3000 //设置延时为3s
              //TODO 页面跳转            
            })
          }
        }
      })
    }
  },
  goRegistPage:function(){
    wx.redirectTo({
      url: '../userRegist/regist',
    })
  }
})