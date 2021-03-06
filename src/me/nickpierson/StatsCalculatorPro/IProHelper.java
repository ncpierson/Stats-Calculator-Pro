package me.nickpierson.StatsCalculatorPro;

import android.app.Activity;

import com.thecellutioncenter.mvplib.DataActionHandler;

public interface IProHelper {

	public <T extends IHelperView> void handleWakeLock(Activity activity, T view);

	public <T extends DataActionHandler & IHelperView> void listenForItemClick(T view, final Enum<?> type);

	public <T extends DataActionHandler & IHelperView, U extends IHelperModel> void listenForMoveUpClick(final T view, final U model, Enum<?> type);

	public <T extends DataActionHandler & IHelperView, U extends IHelperModel> void listenForMoveDownClick(final T view, final U model, Enum<?> type);

	public <T extends DataActionHandler & IHelperView> void listenForRemoveClick(final T view, Enum<?> type);

	public <T extends DataActionHandler & IHelperView> void listenForResetList(final T view, Enum<?> type);

	public <T extends DataActionHandler & IHelperView, U extends IHelperModel> void listenForInfoClick(final T view, final U model, Enum<?> type);

	public <T extends DataActionHandler & IHelperView> void listenForLongItemClick(final T view, final Enum<?> type);
}
