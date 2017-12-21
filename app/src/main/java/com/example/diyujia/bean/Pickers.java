package com.example.diyujia.bean;

/**
 * Created by diyujia on 2017/12/20.
 */

import java.io.Serializable;

/**
 * 摘自CSDN博客，博主信息为下
 * @author zengtao 2015年5月20日下午7:18:14
 *
 */
public class Pickers implements Serializable {

    private static final long serialVersionUID = 1L;

    private String showConetnt;
    private String showId;

    public String getShowConetnt() {
        return showConetnt;
    }

    public String getShowId() {
        return showId;
    }

    public Pickers(String showConetnt, String showId) {
        super();
        this.showConetnt = showConetnt;
        this.showId = showId;
    }

    public Pickers() {
        super();
    }

}
