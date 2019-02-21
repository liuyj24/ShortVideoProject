package com.shenghao.service.impl;

import com.shenghao.mapper.UsersFansMapper;
import com.shenghao.mapper.UsersLikeVideosMapper;
import com.shenghao.mapper.UsersMapper;
import com.shenghao.mapper.UsersReportMapper;
import com.shenghao.pojo.Users;
import com.shenghao.pojo.UsersFans;
import com.shenghao.pojo.UsersLikeVideos;
import com.shenghao.pojo.UsersReport;
import com.shenghao.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    /**
     * 按用户名查询用户是否存在
     * @param username
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);
        return result == null ? false : true;
    }

    /**
     * 注册保存用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        //使用工具类生成userId
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    /**
     * 用户登陆, 返回用户信息
     * @param username
     * @param password
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        Users result = usersMapper.selectOneByExample(userExample);//根据模板到数据库中查找, 查找到则返回该用户所有信息
        return result;
    }

    /**
     * 用户修改信息
     * @param user
     */
    @Override
    public void updateUserInfo(Users user) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", user.getId());
        usersMapper.updateByExampleSelective(user, userExample);//Selective-用户有更新的地方才更新
    }

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", userId);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)){
            return false;
        }
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);

        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);
        if (list != null && list.size() > 0){
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        //往用户粉丝关系表中的添加数据
        UsersFans usersFans = new UsersFans();
        String ufId = sid.nextShort();
        usersFans.setId(ufId);
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);

        //对于视频发布者userId
        usersMapper.addFansCount(userId);//增加粉丝数量
        //对于粉丝, 也就是当前登陆用户
        usersMapper.addFollowersCount(fanId);//增加关注数
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        //删除用户粉丝关系表中的数据
        Example example = new Example(UsersFans.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);
        usersFansMapper.deleteByExample(example);

        //对于视频发布者
        usersMapper.reduceFansCount(userId);
        //对于粉丝
        usersMapper.reduceFollowerCount(fanId);
    }

    @Override
    public boolean queryIsFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);
        if (list != null && !list.isEmpty() && list.size() > 0){
            return true;
        }
        return false;
    }

    @Override
    public void reportUser(UsersReport usersReport) {
        String urId = sid.nextShort();
        usersReport.setId(urId);
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }
}
