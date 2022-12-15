package com.demo.drawland.mvp.proxy;


import com.demo.drawland.mvp.MvpInject;
import com.demo.drawland.mvp.MvpModel;
import com.demo.drawland.mvp.MvpPresenter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc   : 模型层代理实现
 */
public class MvpModelProxyImpl implements IMvpModelProxy {

    private MvpPresenter mPresenter;
    private List<MvpModel> mModels;

    public MvpModelProxyImpl(MvpPresenter presenter) {
        mPresenter = presenter;
    }

    @SuppressWarnings("all")
    @Override
    public void bindModel() {
        mModels = new ArrayList<>();

        Field[] fields = mPresenter.getClass().getDeclaredFields();
        for (Field field : fields) {
            MvpInject inject = field.getAnnotation(MvpInject.class);
            if (inject != null) {
                try {
                    Class<? extends MvpModel> clazz = (Class<? extends MvpModel>) field.getType();
                    MvpModel model = clazz.newInstance();
                    field.setAccessible(true);
                    field.set(mPresenter, model);
                    mModels.add(model);
                } catch (IllegalAccessException | InstantiationException | ClassCastException e) {
                    e.printStackTrace();
                    /**
                     * IllegalAccessException
                     * field.set：没有权限访问，请检查注解对象的修饰符
                     */
                    /**
                     * InstantiationException
                     * clazz.newInstance：检查一下注解的对象有没有空的构造函数
                     */
                    /**
                     * ClassCastException
                     * clazz.newInstance：检查一下自己注解的对象类型是否正确
                     * field.set：检查一下自己的 M 层类型是否正确
                     */
                    throw new IllegalStateException("are you ok?");
                }
            }
        }
    }

    @Override
    public void unbindModel() {
        mModels.clear();
        mModels = null;
        mPresenter = null;
    }
}