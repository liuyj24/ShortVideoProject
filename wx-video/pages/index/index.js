const app = getApp();

Page({
  data: {
    //用于分页的属性
    totalPage: 1, //总页数
    page: 1, //当前的分页数
    videoList: [], //视频数组

    screenWidth: 350,
    serverUrl: '', 

    searchContent: ""
  },
  onLoad: function(params){
    var me = this;
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    me.setData({
      screenWidth: screenWidth
    });

    //获取搜索页面跳转时携带的参数
    var searchContent = params.search;
    var isSaveRecord = params.isSaveRecord;
    if(isSaveRecord == null || isSaveRecord == '' || isSaveRecord == undefined){
      isSaveRecord = 0;
    }
    //保存搜索的内容
    me.setData({
      searchContent: searchContent
    });

    var page = me.data.page;//获取当前的分页数
    me.getAllVideoList(page, isSaveRecord);
  },

  getAllVideoList: function (page, isSaveRecord){
    var me = this;
    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '加载视频中...',
      icon: "none"
    });
    //拿到搜索的内容
    var searchContent = me.data.searchContent;

    wx.request({
      url: serverUrl + '/video/showAll?page=' + page + "&isSaveRecord=" + isSaveRecord,
      method: "POST",
      data: {
        videoDesc: searchContent  //把搜索的内容作为数据带到后端
      },
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();//结束导航栏的加载动画.
        wx.stopPullDownRefresh();//停止下拉刷新的动画.
        console.log(res.data);

        //判断当前页是否为第一页, 如果是第一页, 那么设置videoList为空
        if (page == 1) {
          me.setData({
            videoList: []
          });
        }
        //视频展示的时候是对videoList进行拼接的过程
        var videoList = res.data.data.rows;//数据库中查询得出的视频列表
        var newVideoList = me.data.videoList;//现在页面中已有的视频列表

        me.setData({
          videoList: newVideoList.concat(videoList), //使用concat在原有数组上拼接新数组
          page: page,
          totalPage: res.data.data.total,
          serverUrl: serverUrl
        });
      }
    });
  }, 

  onPullDownRefresh: function(){
    wx.showNavigationBarLoading();//在当前页面显示导航栏加载动画, 在success方法中隐藏
    this.getAllVideoList(1,0);//刷新获取第一页的视频, 不需要保存热搜词
  },

  onReachBottom: function(){
    var me = this;
    var currentPage = me.data.page;
    var totalPage = me.data.totalPage;
    //判断当前页数和总页数是否相等, 如果相等就无需刷新了.
    if (currentPage === totalPage){
      wx.showToast({
        title: '已经没有视频啦...',
        icon: 'icon'
      })
      return;
    }
    //如果当前页面还没有到达总页数, 进行累加
    var page = currentPage + 1;
    me.getAllVideoList(page, 0);//0-上拉分页不需要保存搜索词
  }, 

  showVideoInfo: function(e){
    var me = this;
    var videoList = me.data.videoList;//获取查询得出的视频列表
    var arrIndex = e.target.dataset.arrindex;//在前端获得用户点击的视频的index
    var videoInfo = JSON.stringify(videoList[arrIndex]);//根据index在videoList中获取相应视频的信息, 需要转化为字符串才能传递给下一个页面
    wx.redirectTo({//跳转
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  }

});
