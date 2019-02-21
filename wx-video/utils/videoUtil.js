function uploadVideo() {
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
}
//videoUtil.js
module.exports = {
  uploadVideo: uploadVideo
}