package com.wuqingsen.openglmeiyan.filter;

import android.content.Context;

import com.wuqingsen.openglmeiyan.R;

/**
 * wuqingsen on 2021/4/12
 * Mailbox:807926618@qq.com
 * annotation:显示特效滤镜,责任链模式
 */
public class ScreenFilter extends AbstractFilter {

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }

    @Override
    protected void initCoordinate() {

    }
}
