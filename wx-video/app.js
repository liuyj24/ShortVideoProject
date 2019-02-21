//app.js
App({
  // 定义后端服务器的路径
  serverUrl: "http://106.14.150.87/v/",
  // serverUrl: "http://c8bca06c.ngrok.io",
  userInfo : null, 

  setGlobalUserInfo: function(user){
    wx.setStorageSync("userInfo", user);
  },

  getGlobalUserInfo: function(){
    return wx.getStorageSync("userInfo");
  },
  reportReasonArray: [
    "色情低俗",
    "政治敏感",
    "涉嫌诈骗",
    "辱骂谩骂",
    "广告垃圾",
    "诱导分享",
    "引人不适",
    "过于暴力",
    "违法违纪",
    "其它原因"
  ]
})