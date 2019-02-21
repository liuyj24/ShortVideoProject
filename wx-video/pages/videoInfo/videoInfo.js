var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    cover: 'cover',
    videoId: "",
    src: "",
    userLikeVideo: false, //用户是否点赞的标志
    videoInfo: {},

    commentsPage: 1,
    commentsTotalPage: 1,
    commentsList: [],

    placeholder: "说点什么...",
  },
  //视频内容对象
  videoCtx: {},

  onLoad: function(params) {
    var me = this;
    var serverUrl = app.serverUrl;
    me.videoCtx = wx.createVideoContext("myVideo", me); //加载的时候拿到视频容器
    // console.log(params);
    //获取上一个页面传递的参数
    var videoInfo = JSON.parse(params.videoInfo);

    //根据视频的宽度和高度
    var height = videoInfo.videoHeight;
    var width = videoInfo.videoWidth;
    var cover = "cover";
    //如果视频的宽度大于高度, 那么就不进行拉伸
    if (width > height) {
      cover = "";
    }
    //把拿到的数据设置进page的data中
    me.setData({
      videoId: videoInfo.id,
      src: app.serverUrl + videoInfo.videoPath,
      videoInfo: videoInfo,
      cover: cover,
      serverUrl: serverUrl
    });
    //查询后端获得登陆用户是否喜欢该视频, 并获取视频发布者信息
    var user = app.getGlobalUserInfo();
    var loginUserId = "";
    if (user != null && user != undefined && user != "") {
      loginUserId = user.id;
    }
    wx.request({
      url: serverUrl + '/user/queryPublisher?loginUserId=' + loginUserId + "&videoId=" + videoInfo.id + "&publishUserId=" + videoInfo.userId,
      method: "POST",
      success: function(res) {
        // console.log(res.data);
        var publisher = res.data.data.publisher;
        var userLikeVideo = res.data.data.userLikeVideo;

        me.setData({
          publisher: publisher,
          userLikeVideo: userLikeVideo,
        });
      }
    })
    me.getCommentsList(1);//加载评论列表
  },

  onShow: function() {
    var me = this;
    me.videoCtx.play(); //页面展示的时候播放视频
  },

  onHide: function() {
    var me = this;
    me.videoCtx.pause(); //离开页面时页面隐藏, 暂停视频
  },

  showSearch: function() {
    wx.navigateTo({
      url: '../searchVideo/searchVideo',
    });
  },

  upload: function() {
    var me = this;
    var user = app.getGlobalUserInfo();

    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo; //由于 ?和= 会被过滤掉, 因此换为其他符号, 到时候再换回来. 

    //判断当前缓存中是否有用户信息, 没有要跳转到登陆页面, 有才进行上传
    if (user == null || user == undefined || user == "") {
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      });
      wx.showToast({
        title: '您还没登陆哦~',
        icon: 'none',
        duration: 1500
      })
    } else {
      videoUtil.uploadVideo();
    }
  },

  showIndex: function() {
    wx.redirectTo({
      url: '../index/index',
    })
  },

  showMine: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    //判断当前缓存中是否有用户信息, 没有要跳转到登陆页面, 有才进行跳转
    if (user == null || user == undefined || user == "") {
      wx.navigateTo({
        url: '../userLogin/login',
      });
      wx.showToast({
        title: '您还没登陆哦~',
        icon: 'none',
        duration: 1500
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine',
      })
    }
  },
  likeVideoOrNot: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo;
    //判断当前缓存中是否有用户信息, 没有要跳转到登陆页面, 有才进行跳转
    if (user == null || user == undefined || user == "") {
      wx.navigateTo({
        url: '../userLogin/login',
      });
    } else {
      var userLikeVideo = me.data.userLikeVideo;
      var url = '/video/userLike?userId=' + user.id + "&videoId=" + videoInfo.id + "&videoCreaterId=" + videoInfo.userId;
      if (userLikeVideo) {
        url = '/video/userUnLike?userId=' + user.id + "&videoId=" + videoInfo.id + "&videoCreaterId=" + videoInfo.userId;
      }
      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '稍等哈...',
      })
      wx.request({
        url: serverUrl + url,
        method: "POST",
        header: {
          'content-type': 'application/json', // 默认值
          'userId': user.id,
          'userToken': user.userToken,
        },
        success: function() {
          wx.hideLoading();
          me.setData({
            userLikeVideo: !userLikeVideo
          });
        }
      })
    }
  },
  showPublisher: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    var publisherId = me.data.videoInfo.userId;

    var videoInfo = me.data.videoInfo;
    var realUrl = '../mine/mine#publisherId@' + publisherId; //回跳的地址
    //判断当前缓存中是否有用户信息, 没有要跳转到登陆页面, 有才进行上传
    if (user == null || user == undefined || user == "") {
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      });
      wx.showToast({
        title: '您还没登陆哦~',
        icon: 'none',
        duration: 1500
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine?publisherId=' + videoInfo.userId,
      })
    }
  },
  shareMe: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    wx.showActionSheet({
      itemList: ['下载到本地', '举报用户', '分享到朋友圈', '分享到qq空间', '分享到微博'],
      success: function(res) {
        // console.log(res.tapIndex);
        if (res.tapIndex == 0) {
          //下载
          wx.showLoading({
            title: '下载中...',
          })
          wx.downloadFile({
            url: app.serverUrl + me.data.videoInfo.videoPath,
            success: function(res) {
              // 只要服务器有响应数据，就会把响应内容写入文件并进入 success 回调，业务需要自行判断是否下载到了想要的内容
              if (res.statusCode === 200) {
                console.log(res.tempFilePath);

                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success: function(res) {
                    wx.hideLoading();
                    wx.showToast({
                      title: '下载成功',
                      icon: 'success',
                      duration: 1500
                    })
                    console.log(res.errMsg)
                  }
                })
              }
            }
          })
        } else if (res.tapIndex == 1) {
          // 举报
          var videoInfo = JSON.stringify(me.data.videoInfo);
          var realUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;

          if (user == null || user == undefined || user == '') {
            wx.navigateTo({
              url: '../userLogin/login?redirectUrl=' + realUrl,
            })
          } else {
            var publishUserId = me.data.videoInfo.userId;
            var videoId = me.data.videoInfo.id;
            var currentUserId = user.id;
            wx.navigateTo({
              url: '../report/report?videoId=' + videoId + "&publishUserId=" + publishUserId
            })
          }
        }else{
          wx.showToast({
            title: '官方暂未开放...',
          })
        }
      }
    })
  }, 
  onShareAppMessage(res) {
    var me = this;
    var videoInfo = me.data.videoInfo;
    return {
      title: '短视频内容分享',
      path: '/pages/videoInfo/videoInfo?videoInfo=' + JSON.stringify(videoInfo)
    }
  }, 
  //展示留言
  leaveComment: function(){
    var me = this;
    me.setData({
      commentFocus: true//拿到文本框的焦点, 准备输入
    })
  },
  //回复评论的时候, 改变默认文字
  replyFocus: function(e){
    var me = this;
    var fatherCommentId = e.currentTarget.dataset.fathercommentid;//记得最后的属性名是小写
    var toUserId = e.currentTarget.dataset.touserid;
    var toNickname = e.currentTarget.dataset.tonickname;

    me.setData({
      placeholder: "回复" + toNickname,
      replyFatherCommentId: fatherCommentId,
      replyToUserId: toUserId,
      commentFocus: true//拿到文本框的焦点, 准备输入
    });
  },

  //到后端保存留言
  saveComment: function(e){
    var me = this;
    var content = e.detail.value;

    //获取回复评论的replyFatherCommentId和replyToUserId
    var fatherCommentId = e.currentTarget.dataset.replyfathercommentid;
    var toUserId = e.currentTarget.dataset.replytouserid;


    var user = app.getGlobalUserInfo();
    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo; //回跳

    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      })
    } else {
      wx.showLoading({
        title: '请稍后...',
      })
      wx.request({
        url: app.serverUrl + '/video/saveComment?fatherCommentId=' + fatherCommentId + "&toUserId=" + toUserId,
        method: "POST",
        header: {
          'content-type': 'application/json', 
          'userId': user.id,
          'userToken': user.userToken,
        },
        data: {
          fromUserId: user.id,
          comment: content,
          videoId: me.data.videoInfo.id//埋雷
        },
        success: function(res){
          me.setData({
            contentValue: "",
            commentsList: []//留言列表也要清空, 不然就变叠加了
          });
          me.getCommentsList(1);//对留言进行分页

          wx.hideLoading();
          wx.showToast({
            title: '评论成功...',
            icon: "success",
            duration: 2000
          })
          // console.log(res.data)
        }
      })
    }
  },
  // commentsPage: 1,
  // commentsTotalPage: 1,
  // commentsList: []
  getCommentsList: function(page){
    var me = this;
    var videoId = me.data.videoInfo.id;
    wx.request({
      url: app.serverUrl + '/video/getVideoComments?videoId=' + videoId + "&page=" + page + "&pageSize=5",
      method: "POST",
      success: function(res){
        // console.log(res);
        var commentsList = res.data.data.rows;
        var newCommentsList = me.data.commentsList;
        me.setData({
          commentsList: newCommentsList.concat(commentsList),
          commentsPage: page,
          commentsTotalPage: res.data.data.total
        });
      }
    })
  },

  onReachBottom: function(){
    var me = this;
    var currentPage = me.data.commentsPage;
    var totalPage = me.data.commentsTotalPage;
    if(currentPage === totalPage){
      return;
    }
    var page = currentPage + 1;
    me.getCommentsList(page);
  }
});