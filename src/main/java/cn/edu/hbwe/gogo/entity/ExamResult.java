package cn.edu.hbwe.gogo.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class ExamResult {

    @JSONField(name = "xnm")
    private String year;
    @JSONField(name = "xqm")
    private String semester;

    @JSONField(name = "jxb_id")
    private String detailsID;

    //================下面是有用的信息================//
    @JSONField(name = "kcmc")
    private String name; //课程名称
    @JSONField(name = "tjrxm")
    private String teacher; //老师名字
    @JSONField(name = "xf")
    private String credit; //学分
    @JSONField(name = "jd")
    private String gradePoint; //绩点
    @JSONField(name = "xfjd")
    private String crTimesGp; //学分*绩点
    @JSONField(name = "bfzcj")
    private String absoluteScore; //考试绝对分数
    @JSONField(name = "cj")
    private String relateScore; //评级
    @JSONField(name = "ksxzdm")
    private String completionCode; //挂科标识

    @JSONField(name = "sfxwkc")
    private boolean degreeProgram; //是否是学位课

    public Status getStatus() {
        if (Double.compare(Double.parseDouble(absoluteScore), 60) == -1) {
            return Status.FAIL;
        } else {
            int ksxzdm = Integer.parseInt(completionCode);
            if (ksxzdm == 11 || ksxzdm == 16 || ksxzdm == 17) {
                return Status.SUCCESS_RE;
            }
        }
        return Status.SUCCESS;
    }

    public enum Status {
        SUCCESS, //考试一遍过
        FAIL, //老师不捞我，呜呜呜
        SUCCESS_RE //重修或补考成功
    }



}
