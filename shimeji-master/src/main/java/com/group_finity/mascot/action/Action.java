package com.group_finity.mascot.action;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public interface Action {

    /**
     * 用于初始化动作，传入一个 Mascot 对象作为参数
     * @param mascot
     */
    public void init(Mascot mascot) throws VariableException;

    /**
     * 这个方法用于检查是否还有下一个动作
     * @return
     */
    public boolean hasNext() throws VariableException;

    /**
     * 用于执行下一个动作
     * @throws LostGroundException
     */
    public void next() throws LostGroundException, VariableException;

    /**
     * 结束播放当前动画
     */
     void endCurrentAnimation();

}
