package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.users.dtos.LoginDto;
import com.heima.model.users.pojos.ApUser;

public interface ApUserService extends IService<ApUser> {
    /**
     * app login feature
     * @param dto
     * @return
     */
    ResponseResult login(LoginDto dto);
}
