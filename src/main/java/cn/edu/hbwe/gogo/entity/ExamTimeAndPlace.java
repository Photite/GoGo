package cn.edu.hbwe.gogo.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class ExamTimeAndPlace {
    @JSONField(name = "xnm")
    private String year;
    @JSONField(name = "xqm")
    private String semester;
    //================下面是有用的信息================//
    @JSONField(name = "ksmc")
    private String stage; //考试阶段
    @JSONField(name = "kcmc")
    private String name; //课程名称
    @JSONField(name = "jsxx")
    private String teacher; //老师名字
    @JSONField(name = "xf")
    private String credit; //学分
    @JSONField(name = "cdmc")
    private String place; //考试地点
    @JSONField(name = "kssj")
    private String time; //考试时间
    @JSONField(name = "cdxqmc")
    private String campus ; //考试校区
    @JSONField(name = "zwh")
    private String type; //考试类型
    @JSONField(name = "zxbj")
    private String rebuild; //考试是否重修

}
