package com.shenghao.service;

import com.shenghao.pojo.Users;
import com.shenghao.pojo.UsersReport;

public interface UserService {
    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 保存用户
     * @param user
     */
    public void saveUser(Users user);

    /**
     * 用户登陆, 根据用户名和密码查询用户
     * @param username
     * @param md5Str
     * @return
     */
    Users queryUserForLogin(String username, String md5Str);

    /**
     * 用户修改信息
     * @param user
     */
    public void updateUserInfo(Users user);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 查询用户是否点赞喜欢视频
     * @param userId
     * @param videoId
     * @return
     */
    public boolean isUserLikeVideo(String userId, String videoId);

    /**
     * 点击关注后发布者和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void saveUserFanRelation(String userId, String fanId);

    /**
     * 点击取消关注后发布者和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void deleteUserFanRelation(String userId, String fanId);

    /**
     * 查询用户是否关注
     * @param userId
     * @param fanId
     * @return
     */
    public boolean queryIsFollow(String userId, String fanId);

    /**
     * 举报用户
     * @param usersReport
     */
    void reportUser(UsersReport usersReport);
}



