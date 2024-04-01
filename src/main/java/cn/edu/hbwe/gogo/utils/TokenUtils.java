package cn.edu.hbwe.gogo.utils;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtils {
    //生成token的方法
    public String createToken(String uid, String name) {
        // uid+pwd+uuid+当前时间==>md5
        return uid + name + new Date().getTime();
    }
}
