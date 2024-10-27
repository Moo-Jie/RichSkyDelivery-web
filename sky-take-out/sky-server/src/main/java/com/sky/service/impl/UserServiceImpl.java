package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties properties;
    @Autowired
    private UserMapper userMapper;

    /**
     * @param categoryId
     * @return
     */
    @Override
    public SetmealVO listBycategoryId(Integer categoryId) {
        Setmeal setmeal = userMapper.listBycategoryId(categoryId);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        return setmealVO;
    }

    /**
     *  微信登录
     * @param userLoginDTO
     * @return com.sky.entity.User
     * @author DuRuiChi
     * @create 2024/10/26
     **/
    public User login(UserLoginDTO userLoginDTO) {
        //拿着小程序发来的用户code去登录，返回的openid是登录的这个用户在当前微信小程序服务中的唯一标识ID
        String openid = getOpenid(userLoginDTO.getCode());
        log.info("当前用户的openid为：{}",openid);

        //判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);

        //如果是新用户，自动完成注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);//自动为user的id属性赋值
        }

        //返回这个用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        //调用微信接口服务，获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid",properties.getAppid());//小程序信息
        map.put("secret",properties.getSecret());//小程序信息
        map.put("js_code",code);//用户id
        map.put("grant_type","authorization_code");//固定格式
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
