const app = getApp()

Page({
    data: {
      bgmList : [],
      serverUrl : '',
      videoParams : {}
    },

    onLoad:function(params){
      var me = this;
      var user = app.getGlobalUserInfo();
      console.log(params);
      me.setData({
        videoParams : params 
      });

      var serverUrl = app.serverUrl;//服务器地址
      wx.showLoading({
        title: '正在查询bgm...',
      });
      //调用后端
      wx.request({
        url: serverUrl + '/bgm/list',
        method: "POST",
        header: {
          'content-type': 'application/json', // 默认值
          'userId': user.id,
          'userToken': user.userToken,
        },
        success: function (res) {
          console.log(res.data);
          wx.hideLoading();//收起等待框
          if (res.data.status == 200) {
            var bgmList = res.data.data;
            me.setData({
              bgmList : bgmList,
              serverUrl : serverUrl
            });
          } else if (res.data.status == 502) {
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 2000,
            })
            wx.redirectTo({
              url: '../userLogin/login',
            })
          }
        }
      });
    },
    upload : function(e){
      var me = this;
      var user = app.getGlobalUserInfo();
      var bgmId = e.detail.value.bgmId;
      var desc = e.detail.value.desc;
      var duration = me.data.videoParams.duration;
      var tmpHeight = me.data.videoParams.tmpHeight;
      var tmpWidth = me.data.videoParams.tmpWidth;
      var tmpVideoUrl = me.data.videoParams.tmpVideoUrl;
      var tmpCoverUrl = me.data.videoParams.tmpCoverUrl;

      //上传短视频
      wx.showLoading({
        title: '正在上传^_^',
      })
      var serverUrl = app.serverUrl;//获得服务器的路径
      var userInfo = app.getGlobalUserInfo();
      wx.uploadFile({
        url: serverUrl + '/video/upload',
        header: {
          'content-type': 'application/json', // 默认值
          'userId': user.id,
          'userToken': user.userToken,
        },
        formData:{
          userId: userInfo.id,  //fixme 原来的 app.userInfo.id
          bgmId: bgmId,
          desc: desc,
          videoSeconds: duration,
          videoHeight: tmpHeight,
          videoWidth: tmpWidth,
        },
        filePath: tmpVideoUrl,//压缩后视频的临时路径
        name: 'file',//对应在服务器中参数的名字
        success(res) {
          var data = JSON.parse(res.data);//将接收到的数据格式化为json对象
          if (data.status == 200) {
            wx.navigateBack({
              delta: 1, //返回之间的页面
            });
            wx.showToast({
              title: '上传成功(*^_^*)',
              icon: 'none',
              duration: 1000
            });
            // var videoId = data.data;//接收到服务器返回的data数据, 就是videoId
            // wx.uploadFile({
            //   url: serverUrl + '/video/uploadCover',
            //   formData: {
            //     userId : app.userInfo.id,
            //     videoId : videoId
            //   },
            //   filePath: tmpCoverUrl,
            //   name: 'file',//对应在服务器中参数的名字
            //   success(res) {
            //     wx.hideLoading();
            //     if (data.status == 200) {
            //       wx.navigateBack({
            //         delta: 1, //返回之间的页面
            //       });
            //       wx.showToast({
            //         title: '上传成功,跳转中...',
            //         icon: 'none',
            //         duration: 1500
            //       });
            //     } else if (data.status == 500) {
            //       wx.showToast({
            //         title: '上传失败...'
            //       });
            //     }
            //   }
            // });
          } else if (data.status == 500) {
            wx.navigateBack({
              delta: 1, //返回之间的页面
            });
            wx.showToast({
              title: "上传失败, 未知错误...", 
              icon: "none"
            });
          } else if (res.data.status == 502) {
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 2000,
            })
            wx.redirectTo({
              url: '../userLogin/login',
            })
          }
        }
      });
    }
})

