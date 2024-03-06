package cn.edu.hbwe.gogo.entity;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String wechatId;
    private String phone;
    private String password;
//    private String stuId;
//    private String stuPassword;
    private String avatar;


}
