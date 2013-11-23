package evemanutool.constants;

import java.awt.Color;

import evemanutool.constants.DBConstants.Trend;

public interface GUIConstants {

	//Edit text color (green).
	public static final Color EDITABLE_TABLE_HEAD = new Color(0, 150, 65);
	
	//Window icon.
	public static final String WINDOW_ICON_PATH = "img/emt_icon.png";
	
	//Trend icons.
	public static final Trend[] TRENDS = {Trend.RISING, Trend.FALLING, Trend.STABLE, Trend.UNSTABLE, Trend.INCONCLUSIVE};
	public static final String[] TREND_ICON_PATHS = {"img/up.png", "img/down.png", "img/forward.png", "img/up_down.png", "img/dash.png"};
	public static final String[] TREND_ICON_TIPS = {"Rising", "Falling", "Stable", "Unstable", "Inconclusive"};
	
	//Tabs.
	public static final int INDY_TAB_INDEX = 0;
	public static final int CORP_TAB_INDEX = 1;
}
