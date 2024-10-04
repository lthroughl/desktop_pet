package com.group_finity.mascot.action;

import java.util.List;
import java.util.logging.Logger;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.environment.Border;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

/**
 * Original Author: Yuki Yamada of Group Finity (http://www.group-finity.com/Shimeji/)
 * Currently developed by Shimeji-ee Group.
 */
public abstract class BorderedAction extends ActionBase {

	private static final Logger log = Logger.getLogger(BorderedAction.class.getName());

	private static final String PARAMETER_BORDERTYPE = "BorderType";

	public static final String DEFAULT_BORDERTYPE = null;

	public static final String BORDERTYPE_CEILING = "Ceiling";

	public static final String BORDERTYPE_WALL = "Wall";

	public static final String BORDERTYPE_FLOOR = "Floor";

	private Border border;

	public BorderedAction(final List<Animation> animations, final VariableMap params) {
		super(animations, params);
	}

	/**
	 * 重写了父类的 init 方法，用于初始化动作。在该方法内部，根据参数中指定的边界类型，选择相应的边界对象并进行设置
	 * @param mascot
	 * @throws VariableException
	 */
	@Override
	public void init(final Mascot mascot) throws VariableException {
		super.init(mascot);

		final String borderType = getBorderType();

		if (BORDERTYPE_CEILING.equals(borderType)) {
			this.setBorder(getEnvironment().getCeiling());
		} else if (BORDERTYPE_WALL.equals(borderType)) {
			this.setBorder(getEnvironment().getWall());
		} else if (BORDERTYPE_FLOOR.equals(borderType)) {
			this.setBorder(getEnvironment().getFloor());
		}
	}

	/**
	 * 实现具体的动作逻辑。根据当前的边界对象调整萌宠的位置
	 * @throws LostGroundException
	 * @throws VariableException
	 */
	@Override
	protected void tick() throws LostGroundException, VariableException {
		if (getBorder() != null) {
			getMascot().setAnchor(getBorder().move(getMascot().getAnchor()));
		}
	}

	private String getBorderType() throws VariableException {
		return eval(PARAMETER_BORDERTYPE, String.class, DEFAULT_BORDERTYPE);
	}

	private void setBorder(final Border border) {
		this.border = border;
	}
	
	protected Border getBorder() {
		return this.border;
	}

}
