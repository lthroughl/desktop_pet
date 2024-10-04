package com.group_finity.mascot.action;

import java.util.List;
import java.util.logging.Logger;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.environment.MascotEnvironment;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.Variable;
import com.group_finity.mascot.script.VariableMap;

/**
 * 定义了萌宠动作的基本操作逻辑，并提供了一些共用的方法和属性
 */
public abstract class ActionBase implements Action {

    private static final Logger log = Logger.getLogger(ActionBase.class.getName());

    public static final String PARAMETER_DURATION = "Duration";

    private static final boolean DEFAULT_CONDITION = true;

    public static final String PARAMETER_CONDITION = "Condition";

    private static final int DEFAULT_DURATION = Integer.MAX_VALUE;

    private Mascot mascot;

    private int startTime;

    private List<Animation> animations;

    private VariableMap variables;

    private boolean isStopAnimation = false;

    /**
     * 接受一个动画列表和变量映射对象作为参数，用于初始化动作。
     * @param animations
     * @param context
     */
    public ActionBase(final List<Animation> animations, final VariableMap context) {
        this.animations = animations;
        this.variables = context;
    }


    @Override
    public String toString() {
        try {
            return "Action (" + getClass().getSimpleName() + "," + getName() + ")";
        } catch (final VariableException e) {
            return "Action (" + getClass().getSimpleName() + "," + null + ")";
        }
    }
    /**
     * 初始化动作，传入一个 Mascot 对象作为参数。在该方法内部，会对动画和变量进行初始化操作。
     * @return
     */
    @Override
    public void init(final Mascot mascot) throws VariableException {
        this.setMascot(mascot);
        this.setTime(0);
        this.isStopAnimation = false;
        this.getVariables().put("mascot", mascot);
        this.getVariables().put("action", this);

        getVariables().init();

        for (final Animation animation : this.animations) {
            animation.init();
        }
    }

    /**
     * 执行下一个动作，具体的逻辑由子类实现。在该方法内部，会调用 initFrame() 方法进行帧动画的初始化，并调用 tick() 方法执行具体的动作逻辑
     * @throws LostGroundException
     * @throws VariableException
     */
    @Override
    public void next() throws LostGroundException, VariableException {
        initFrame();
        tick();
    }

    /**
     * 初始化帧动画
     */
    private void initFrame() {

        getVariables().initFrame();

        for (final Animation animation : getAnimations()) {
            animation.initFrame();
        }
    }

    protected List<Animation> getAnimations() {
        return this.animations;
    }

    protected abstract void tick() throws LostGroundException, VariableException;

    /**
     * 检查是否还有下一个动作可以执行。如果动画被强制停止，则返回 false；否则，根据条件和时长判断动作是否有效并在有效时间范围内
     * @return
     * @throws VariableException
     */
    @Override
    public boolean hasNext() throws VariableException {
        if (isStopAnimation) {
            //isStopAnimation = false;
            return false;
        } else {
            final boolean effective = isEffective();
            final boolean intime = getTime() < getDuration();
            return effective && intime;
        }
    }

    /**
     * 强制将当前播放结束
     */
    @Override
    public synchronized void endCurrentAnimation() {
        this.isStopAnimation = true;
    }

    private Boolean isEffective() throws VariableException {
        return eval(PARAMETER_CONDITION, Boolean.class, DEFAULT_CONDITION);
    }

    private int getDuration() throws VariableException {
        return eval(PARAMETER_DURATION, Number.class, DEFAULT_DURATION).intValue();
    }

    private void setMascot(final Mascot mascot) {
        this.mascot = mascot;
    }

    protected Mascot getMascot() {
        return this.mascot;
    }

    protected int getTime() {
        return getMascot().getTime() - this.startTime;
    }

    protected void setTime(final int time) {
        this.startTime = getMascot().getTime() - time;
    }

    private String getName() throws VariableException {
        return this.eval("Name", String.class, null);
    }

    protected Animation getAnimation() throws VariableException {
        for (final Animation animation : getAnimations()) {
            if (animation.isEffective(getVariables())) {
                return animation;
            }
        }

        return null;
    }

    protected VariableMap getVariables() {
        return this.variables;
    }

    /**
     * 向变量映射对象中添加或更新变量。对变量映射对象进行同步操作，以避免多线程访问时的并发问题
     * @param key
     * @param value
     */
    protected void putVariable(final String key, final Object value) {
        synchronized (getVariables()) {
            getVariables().put(key, value);
        }
    }

    /**
     * 获取指定名称的变量，并将其转换为指定类型(type)的值。如果变量不存在，则返回默认值。会对变量映射对象进行同步操作。
     * @param name
     * @param type
     * @param defaultValue
     * @return
     * @param <T>
     * @throws VariableException
     */
    protected <T> T eval(final String name, final Class<T> type, final T defaultValue) throws VariableException {

        synchronized (getVariables()) {
            final Variable variable = getVariables().getRawMap().get(name);
            if (variable != null) {
                return type.cast(variable.get(getVariables()));
            }
        }

        return defaultValue;
    }

    /**
     * 获取萌宠所处的环境对象 MascotEnvironment。调用 getMascot() 方法获取 Mascot 对象，并从该对象中获取环境对象。
     * @return
     */
    protected MascotEnvironment getEnvironment() {
        return getMascot().getEnvironment();
    }
}
