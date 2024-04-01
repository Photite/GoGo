package cn.edu.hbwe.gogo.dto;

import lombok.*;

/**
 * @author Photite
 */
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String msg;
    private String code;
    private Object data;



}
