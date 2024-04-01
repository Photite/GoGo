package cn.edu.hbwe.gogo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

/**
 * @author Photite
 */
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private String wechatId;
    private String jwxtUsername;
    private String jwxtPassword;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
