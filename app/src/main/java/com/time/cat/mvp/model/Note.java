package com.time.cat.mvp.model;

import java.io.Serializable;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/22
 * @discription null
 * @usage null
 */
public class Note implements Serializable {
    private static final long serialVersionUID = 2L;

    private String url;// note的url 访问该url可返回该note
    private String title;//笔记标题
    private String content;//笔记内容
    private String owner;//用户ID
}
