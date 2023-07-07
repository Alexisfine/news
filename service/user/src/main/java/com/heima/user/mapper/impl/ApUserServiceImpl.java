package com.heima.user.mapper.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.common.users.dtos.LoginDto;
import com.heima.model.common.users.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

import static com.heima.model.common.enums.AppHttpCodeEnum.*;

@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    /**
     * app login feature
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        // Attempt to log in
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())) {
            ApUser user = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if (user == null) {
                return ResponseResult.errorResult(DATA_NOT_EXIST, "Username does not exist");
            }
            String salt = user.getSalt();
            String password = dto.getPassword();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pswd.equals(user.getPassword())) {
                return ResponseResult.errorResult(LOGIN_PASSWORD_ERROR, "Incorrect password");
            }

            String token = AppJwtUtil.getToken(user.getId().longValue());
            Map<String, Object> map = new HashMap<>();
            user.setSalt("");
            user.setPassword("");
            map.put("token", token);
            map.put("user", user);

            return ResponseResult.okResult(map);

        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }
}
