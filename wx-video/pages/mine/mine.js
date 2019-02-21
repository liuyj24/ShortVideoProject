var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    faceUrl: "../resource/images/noneface.png",
    isMe: true,
    isFollow: false,

    videoSelClass: "video-info",
    isSelectedWork: "video-info-selected",
    isSelectedLike: "",
    isSelectedFollow: "",

    myVideoList: [],
    myVideoPage: 1,
    myVideoTotal: 1,

    likeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotal: 1,

    followVideoList: [],
    followVideoPage: 1,
    followVideoTotal: 1,

    myWorkFalg: true,
    myLikesFalg: true,
    myFollowFalg: false
  },

  onLoad: function(params) {
    var me = this; //先拿到当前对象
    // var user = app.userInfo;//拿到当前用户对象
    // fixme 修改原有的全局对象为本地缓存
    var user = app.getGlobalUserInfo();
    var userId = user.id;

    var publisherId = params.publisherId; //如果用户查看其他用户信息会有这个参数
    if (publisherId != null && publisherId != "" && publisherId != undefined) {
      //publisherId不为空, 证明是当前用户在查看其他用户, 这时把查询的id换为视频发布者的id
      userId = publisherId;
      me.setData({
        isMe: false,
        publisherId: publisherId,
        userId: user.id //为后面在个人页面查询自己发布的视频设置参数
      })
    }

    var serverUrl = app.serverUrl; //服务器地址
    wx.showLoading({
      title: '正在查询用户信息...',
    });
    //调用后端
    wx.request({
      url: serverUrl + '/user/query?userId=' + userId + "&fanId=" + user.id,
      method: "POST",
      header: {
        'content-type': 'application/json', // 默认值
        'userId': user.id,
        'userToken': user.userToken,
      },
      success: function(res) {
        console.log(res.data);
        wx.hideLoading(); //收起等待框
        if (res.data.status == 200) {
          var userInfo = res.data.data; //拿到服务器返回的用户信息
          var faceUrl = "../resource/images/noneface.png";
          if (userInfo.faceImage != null && userInfo.faceImage != '' &&
            userInfo.faceImage != undefined) {
            faceUrl = serverUrl + userInfo.faceImage; //拿到用户的头像地址
          }
          me.setData({
            faceUrl: faceUrl,
            fansCounts: userInfo.fansCounts,
            followCounts: userInfo.followCounts,
            receiveLikeCounts: userInfo.receiveLikeCounts,
            nickname: userInfo.nickname,
            isFollow: userInfo.follow
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
    })
  },
  followMe: function(e) {
    var me = this;
    var publisherId = me.data.publisherId;
    var user = app.getGlobalUserInfo();
    var userId = user.id;
    var followType = e.currentTarget.dataset.followtype; //注意这里followtype要改成小写
    //1: 点击后进行关注; 0: 点击后进行取消关注
    var url = '';
    if (followType == '1') {
      url = "/user/beYourFans?userId=" + publisherId + "&fanId=" + userId;
    } else {
      url = "/user/dontBeYourFans?userId=" + publisherId + "&fanId=" + userId;
    }
    wx.showLoading({
      title: '正在操作...',
    })
    wx.request({
      url: app.serverUrl + url,
      method: "POST",
      header: {
        'content-type': 'application/json', // 默认值
        'userId': user.id,
        'userToken': user.userToken,
      },
      success: function() {
        wx.hideLoading();
        if (followType == '1') {
          me.setData({
            isFollow: true,
            fansCounts: ++me.data.fansCounts
          });
        } else {
          me.setData({
            isFollow: false,
            fansCounts: --me.data.fansCounts
          });
        }
      }
    })
  },

  logout: function() {
    // var user = app.userInfo;//从全局对象user中拿到用户的信息
    // fixme 修改全局对象为本地缓存
    var user = app.getGlobalUserInfo();
    var serverUrl = app.serverUrl; //服务器地址
    //提高用户体验, 调用后端的过程中让用户等待一下, 在success方法中收回
    wx.showLoading({
      title: '正在注销(*^_^*)',
    });
    //调用后端
    wx.request({
      url: serverUrl + '/logout?userId=' + user.id,
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function(res) {
        console.log(res.data);
        wx.hideLoading(); //收起等待框
        if (res.data.status == 200) {
          wx.showToast({
            title: '注销成功',
            icon: 'success',
            duration: 2000
          });
          // app.userInfo = null;
          //fixme 注销以后清空缓存
          wx.removeStorageSync("userInfo");
          //页面跳转
          wx.redirectTo({
            url: '../userLogin/login',
          })
        }
      }
    })
  },
  changeFace: function() {
    var me = this; //保存作用域
    var user = app.getGlobalUserInfo();
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      success(res) {
        const tempFilePaths = res.tempFilePaths; //图片的本地文件路径列表(数组)
        console.log(tempFilePaths);

        wx.showLoading({
          title: '正在上传^_^',
        })

        var serverUrl = app.serverUrl; //获得服务器的路径
        // fixme 修改原有的全局对象为本地缓存
        var userInfo = app.getGlobalUserInfo();
        wx.uploadFile({
          url: serverUrl + '/user/uploadFace?userId=' + userInfo.id,
          header: {
            'content-type': 'application/json', // 默认值
            'userId': user.id,
            'userToken': user.userToken,
          },
          filePath: tempFilePaths[0], //只有一张照片,选中第0个
          name: 'file', //对应在服务器中参数的名字
          success(res) {
            var data = JSON.parse(res.data); //将接收到的数据格式化为json对象
            console.log(data);
            wx.hideLoading();
            if (data.status == 200) {
              wx.showToast({
                title: '上传成功',
                icon: 'success',
                duration: 2000
              });

              var imageUrl = data.data; //get url of the img
              me.setData({ //这里this的作用域有问题, 在本函数的开头就获得this对象, 保证作用域可行
                faceUrl: serverUrl + imageUrl
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
    });
  },
  uploadVideo: function() {
    // videoUtil.uploadVideo();
    var me = this;

    wx.chooseVideo({
      sourceType: ['album'],
      success(res) {
        console.log(res);
        //获得视频的信息
        var duration = res.duration;
        var tmpHeight = res.height;
        var tmpWidth = res.width;
        var tmpVideoUrl = res.tempFilePath;
        var tmpCoverUrl = res.thumbTempFilePath;

        if (duration > 15) {
          wx.showToast({
            title: '视频不能超过15秒哦~',
            icon: 'none',
            duration: 2500
          })
        } else if (duration < 1) {
          wx.showToast({
            title: '视频不能短于1秒哦~',
            icon: 'none',
            duration: 2500
          })
        } else { //视频经检验合格
          //打开选择bgm的页面
          wx.navigateTo({
            url: '../chooseBgm/chooseBgm?duration=' + duration +
              "&tmpHeight=" + tmpHeight +
              "&tmpWidth=" + tmpWidth +
              "&tmpVideoUrl=" + tmpVideoUrl +
              "&tmpCoverUrl=" + tmpCoverUrl
          })
        }
      }
    })
  },
  doSelectWork: function () {
    this.setData({
      isSelectedWork: "video-info-selected",
      isSelectedLike: "",
      isSelectedFollow: "",

      myWorkFalg: false,
      myLikesFalg: true,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyVideoList(1);
  },

  doSelectLike: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "video-info-selected",
      isSelectedFollow: "",

      myWorkFalg: true,
      myLikesFalg: false,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyLikesList(1);
  },

  doSelectFollow: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "",
      isSelectedFollow: "video-info-selected",

      myWorkFalg: true,
      myLikesFalg: true,
      myFollowFalg: false,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyFollowList(1)
  },
  //展示我的视频信息列表
  getMyVideoList: function (page) {
    var user = app.getGlobalUserInfo();
    var me = this;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showAll/?page=' + page + '&pageSize=6',
      method: "POST",
      data: {
        userId: user.id
      },
      header: {
        'content-type': 'application/json', // 默认值
        'userId': user.id,
        'userToken': user.userToken,
      },
      success: function (res) {
        console.log(res.data);
        var myVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.myVideoList;
        me.setData({
          myVideoPage: page,
          myVideoList: newVideoList.concat(myVideoList),
          myVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  getMyLikesList: function (page) {
    // debugger;
    var me = this;
    var userId = me.data.userId;
    var user = app.getGlobalUserInfo();

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyLike/?userId=' + userId + '&page=' + page + '&pageSize=6',
      method: "POST",
      header: {
        'content-type': 'application/json', // 默认值
        'userId': user.id,
        'userToken': user.userToken,
      },
      success: function (res) {
        console.log(res.data);
        var likeVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.likeVideoList;
        me.setData({
          likeVideoPage: page,
          likeVideoList: newVideoList.concat(likeVideoList),
          likeVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  getMyFollowList: function (page) {
    var me = this;
    var userId = me.data.userId;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyFollow/?userId=' + userId + '&page=' + page + '&pageSize=6',
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        var followVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.followVideoList;
        me.setData({
          followVideoPage: page,
          followVideoList: newVideoList.concat(followVideoList),
          followVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },
  // 到底部后触发加载
  onReachBottom: function () {
    var myWorkFalg = this.data.myWorkFalg;
    var myLikesFalg = this.data.myLikesFalg;
    var myFollowFalg = this.data.myFollowFalg;

    if (!myWorkFalg) {
      var currentPage = this.data.myVideoPage;
      var totalPage = this.data.myVideoTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyVideoList(page);
    } else if (!myLikesFalg) {
      var currentPage = this.data.likeVideoPage;
      var totalPage = this.data.myLikesTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyLikesList(page);
    } else if (!myFollowFalg) {
      var currentPage = this.data.followVideoPage;
      var totalPage = this.data.followVideoTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyFollowList(page);
    }
  }
});