package com.heima.model.common.users.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * APP user info chart
 * </p>
 *
 * @author alexisfine
 */
@Data
@TableName("ap_user")
public class ApUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PK
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * Salt
     */
    @TableField("salt")
    private String salt;

    /**
     * Username
     */
    @TableField("name")
    private String name;

    /**
     * encrypted password
     */
    @TableField("password")
    private String password;

    /**
     * phone number
     */
    @TableField("phone")
    private String phone;

    /**
     * profile image
     */
    @TableField("image")
    private String image;

    /**
     * sex
     * 0 male
     1 female
     2 unknown
     */
    @TableField("sex")
    private Boolean sex;

    /**
     * 0 no
     1 yes
     */
    @TableField("is_certification")
    private Boolean certification;

    /**
     * is_identity_authentication
     */
    @TableField("is_identity_authentication")
    private Boolean identityAuthentication;

    /**
     * 0 normal
     1 locked
     */
    @TableField("status")
    private Boolean status;

    /**
     * 0 normal user
     1 content creator
     2 celebrity
     */
    @TableField("flag")
    private Short flag;

    /**
     * created time
     */
    @TableField("created_time")
    private Date createdTime;

}