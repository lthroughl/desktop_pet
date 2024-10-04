package com.group_finity.mascot.action;

import java.util.List;
import java.util.logging.Logger;

import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public class Animate extends BorderedAction {

	private static final Logger log = Logger.getLogger(Animate.class.getName());

	public Animate(final List<Animation> animations, final VariableMap params) {
		super(animations, params);

	}

	/**
	 * 重写了父类的 tick 方法，并添加了额外的逻辑。
	 * 首先调用父类的 tick 方法，
	 * 然后检查萌宠是否已经超出了边界范围，如果超出则抛出 LostGroundException 异常；
	 * 最后调用动画对象的 next 方法，播放下一帧动画。
	 * @throws LostGroundException
	 * @throws VariableException
	 */
	@Override
	protected void tick() throws LostGroundException, VariableException {

		super.tick();

		if ((getBorder() != null) && !getBorder().isOn(getMascot().getAnchor())) {
			throw new LostGroundException();
		}

		getAnimation().next(getMascot(), getTime());

	}

	/**
	 * 判断动画是否还有下一帧。根据动画的持续时间和当前时间判断动画是否已经结束。
	 * @return
	 * @throws VariableException
	 */
	@Override
	public boolean hasNext() throws VariableException {

		final boolean intime = getTime() < getAnimation().getDuration();

		return super.hasNext() && intime;
	}

}
