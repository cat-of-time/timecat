package com.time.cat.mvp.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription null
 * @usage null
 */
public class EmojiBean {

  private List<String> emoji_list;

  public List<String> getEmoji_list() {
    List<String> strList = new ArrayList<>();
    for (String s : emoji_list) {
      int id = Integer.parseInt(s,16);
      strList.add(new String(Character.toChars(id)));
    }
    return strList;
  }

  public void setEmoji_list(List<String> emoji_list) {
    this.emoji_list = emoji_list;
  }
}
