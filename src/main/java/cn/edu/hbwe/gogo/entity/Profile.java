package cn.edu.hbwe.gogo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Profile {
    //2203050528 0
    //赵洋 1
    //2203050528 2
    //赵洋 3
    // 4
    //男 5
    //居民身份证 6
    //210603200404246515 7
    //2004-04-24 8
    //汉族 9
    //中国共产主义青年团团员 10
    //2022-09-16 11
    //辽宁 12
    //2022 13
    //信息科学与工程学院 14
    //计算机科学与技术(0305) 15
    // 16
    //22030505 17
    //4 18
    //在读 19
    //是 20
    // 21
    //信息科学与工程学院 22
    //22210602150010 23
    //英语 24
    // 25
    //iveour@163.com 26
    //15141522791 27
    private String name; //姓名
    private String collegeName; //学院
    private String studyName; //专业
    private byte[] avatar; //头像

    private String email; //邮箱
    private String phone; //手机
    private String id; //身份证
    private String policy; //政治面貌
    private String language; //外语语种
}
