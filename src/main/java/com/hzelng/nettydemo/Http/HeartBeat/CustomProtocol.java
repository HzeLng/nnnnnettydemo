package com.hzelng.nettydemo.Http.HeartBeat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author HzeLng
 * @version 1.0
 * @description CustomProtocol
 * @date 2021/5/5 15:13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomProtocol implements Serializable {

    private static final long serialVersionUID = 4671171056588401542L;
    private long id ;
    private String content ;

    @Override
    public String toString() {
        return "CustomProtocol{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
