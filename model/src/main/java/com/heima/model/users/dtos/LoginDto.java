package com.heima.model.users.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginDto {
    /**
     * phone number
     **/
    @ApiModelProperty(value = "phone", required = true)
    private String phone;
    /**
     * password
     */
    @ApiModelProperty(value = "password", required = true)
    private String password;

}
