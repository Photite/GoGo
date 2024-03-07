package cn.edu.hbwe.gogo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class User {

    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private String wechatId;
    private String jwxtUsername;
    private String jwxtPassword;


}
