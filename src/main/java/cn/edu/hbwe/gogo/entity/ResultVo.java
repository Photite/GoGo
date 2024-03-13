package cn.edu.hbwe.gogo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResultVo<T> {

    private HashMap param;
    private String message;//提示信息
    private boolean success;//状态信息
    private Integer pageIndex;// 当前页
    private Integer pageSize;//页容量
    private Object data; //返回的数据
    private Integer count; //返回的页数
    private String token;
    public ResultVo(String message, boolean success, Object data){
        this.message=message;
        this.success=success;
        this.data=data;
    }

    public ResultVo(String message, boolean success, Object data, String token) {
        this.message = message;
        this.success = success;
        this.data = data;
        this.token = token;
    }

    public ResultVo(String message, boolean success, Integer pageIndex, Integer pageSize, Object data) {
        this.message = message;
        this.success = success;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.data = data;
    }

    public ResultVo(String message, boolean success, Integer pageIndex, Integer pageSize, Object data, Integer count) {
        this.message = message;
        this.success = success;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.data = data;
        this.count = count;
    }

    public ResultVo(String message, boolean success, Object data, Integer count) {
        this.message = message;
        this.success = success;
        this.data = data;
        this.count = count;
    }

    public ResultVo(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}

