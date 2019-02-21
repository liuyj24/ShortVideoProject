package com.shenghao.service;

import com.shenghao.pojo.Users;
import com.shenghao.utils.PagedResult;

public interface UsersService {

	public PagedResult queryUsers(Users user, Integer page, Integer pageSize);
	
}
