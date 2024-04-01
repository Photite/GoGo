package cn.edu.hbwe.gogo.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author Photite
 */
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
    //课程名称
    private String name;
    @JSONField(name = "tjrxm")
    //老师名字
    private String teacher;
    @JSONField(name = "xf")
    //学分
    private String credit;
    @JSONField(name = "jd")
    //绩点
    private String gradePoint;
    @JSONField(name = "xfjd")
    //学分*绩点
    private String crTimesGp;
    @JSONField(name = "bfzcj")
    //考试绝对分数
    private String absoluteScore;
    @JSONField(name = "cj")
    //评级
    private String relateScore;
    @JSONField(name = "ksxzdm")
    //挂科标识
    private String completionCode;
    //是否是学位课
    @JSONField(name = "sfxwkc")
    private boolean degreeProgram;

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
        //考试一遍过
        SUCCESS,
        //老师不捞我，呜呜呜
        FAIL,
        //重修或补考成功
        SUCCESS_RE
    }


}
