package com.shenghao.mapper;

import com.shenghao.pojo.Users;
import com.shenghao.utils.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersMapper extends MyMapper<Users> {
    /**
     * 用户受喜欢数累加
     * @param userId
     */
    public void addReceiveLikeCount(String userId);

    /**
     * 用户受喜欢数累减
     * @param userId
     */
    public void reduceReceiveLikeCount(String userId);

    /**
     * 增加粉丝数量
     * @param userId
     */
    public void addFansCount(String userId);

    /**
     * 减少粉丝数量
     * @param userId
     */
    public void reduceFansCount(String userId);

    /**
     * 增加关注数
     * @param userId
     */
    public void addFollowersCount(String userId);

    /**
     * 减少关注数
     * @param userId
     */
    public void reduceFollowerCount(String userId);

}