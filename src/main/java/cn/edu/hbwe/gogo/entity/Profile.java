package cn.edu.hbwe.gogo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Photite
 */
@Data
@AllArgsConstructor
public class Profile {
    //学号
    private String no;
    //姓名
    private String name;
    //年级
    private String grade;
    //学院
    private String collegeName;
    //专业
    private String studyName;
    //身份证
    private String idCard;
    //邮箱
    private String email;
    //手机
    private String phone;

    //政治面貌
    private String policy;
    //外语语种
    private String language;

    public Profile(String no, String name, String grade, String collegeName, String studyName, String idCard) {
        this.no = no;
        this.name = name;
        this.grade = grade;
        this.collegeName = collegeName;
        this.studyName = studyName;
        this.idCard = idCard;
    }
}
