package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.SetmealVO;

public interface UserService {
    User login(UserLoginDTO userLoginDTO);

    SetmealVO listBycategoryId(Integer categoryId);
}
