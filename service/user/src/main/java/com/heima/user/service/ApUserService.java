package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.users.dtos.LoginDto;
import com.heima.model.common.users.pojos.ApUser;
import org.springframework.stereotype.Service;

public interface ApUserService extends IService<ApUser> {
    /**
     * app login feature
     * @param dto
     * @return
     */
    ResponseResult login(LoginDto dto);
}
