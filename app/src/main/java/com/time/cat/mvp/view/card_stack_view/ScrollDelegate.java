package com.time.cat.mvp.view.card_stack_view;

public interface ScrollDelegate {

    void scrollViewTo(int x, int y);
    void setViewScrollY(int y);
    void setViewScrollX(int x);
    int getViewScrollY();
    int getViewScrollX();

}
