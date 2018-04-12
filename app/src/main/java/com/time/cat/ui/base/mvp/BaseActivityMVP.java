package com.time.cat.ui.base.mvp;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription null
 * @usage null
 */
public interface BaseActivityMVP {

    interface View extends BaseMVP.View {
        boolean isAlive();

        boolean isRunning();
    }

    interface Presenter extends BaseMVP.Presenter {
    }

}
