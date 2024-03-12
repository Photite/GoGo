package cn.edu.hbwe.gogo.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class GPAScore {
    @JSONField(name = "xmnr")
    private String name;
    @JSONField(name = "yxfz")
    private String score;
}
