package me.nickpierson.StatsCalculatorPro.pc;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;

import me.nickpierson.StatsCalculator.pc.PCPresenterTest;
import me.nickpierson.StatsCalculator.utils.Constants;
import me.nickpierson.StatsCalculatorPro.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPreferenceManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ProPCPresenterTest extends PCPresenterTest {

	Activity activity;
	ProPCView proView;
	private SharedPreferences sharedPref;
	private ProPCModel proModel;

	private static final String WAKE_LOCK = "wake lock";

	@Override
	@Before
	public void setup() {
		super.setup();

		activity = mock(Activity.class);
		proView = mock(ProPCView.class);
		proModel = mock(ProPCModel.class);

		Context context = Robolectric.application.getApplicationContext();
		when(activity.getApplicationContext()).thenReturn(context);
		when(activity.getString(R.string.wake_lock)).thenReturn(WAKE_LOCK);

		sharedPref = ShadowPreferenceManager.getDefaultSharedPreferences(context);
	}

	public void createPresenter() {
		ProPCPresenter.create(activity, proModel, proView);
	}

	@Test
	public void whenPresenterIsCreated_ViewIsToldToKeepScreenOnIfPreferenceIsSet() {
		sharedPref.edit().putBoolean(WAKE_LOCK, true).commit();

		createPresenter();

		verify(proView).wakeLock();
	}

	@Test
	public void whenPresenterIsCreated_ViewIsNeverToldToKeepScreenOnIfPreferenceIsNotSet() {
		sharedPref.edit().putBoolean(WAKE_LOCK, false).commit();

		createPresenter();

		verify(proView, never()).wakeLock();
	}

	@Test
	public void whenAListItemIsSelectedAndControllerIsNotShown_ThenTheControllerShouldAppear() {
		when(proView.getSelectedPosition()).thenReturn(-1);
		HashMap<Enum<?>, Integer> testMap = new HashMap<Enum<?>, Integer>();
		testMap.put(ProPCView.ProTypes.ITEM_CLICK, 3);
		createPresenter();

		verify(proView).addListener(dataListener.capture(), eq(ProPCView.ProTypes.ITEM_CLICK));

		dataListener.getValue().fire(testMap);

		verify(proView).showController();
	}

	@Test
	public void whenAListItemIsSelectedAndControllerIsNotShown_ThenAdapterShouldSaveSelectedPosition() {
		when(proView.getSelectedPosition()).thenReturn(-1);
		HashMap<Enum<?>, Integer> testMap = new HashMap<Enum<?>, Integer>();
		testMap.put(ProPCView.ProTypes.ITEM_CLICK, 3);
		createPresenter();

		verify(proView).addListener(dataListener.capture(), eq(ProPCView.ProTypes.ITEM_CLICK));

		dataListener.getValue().fire(testMap);

		verify(proView).setSelectedPosition(3);
	}

	@Test
	public void whenANewListItemIsSelectedAndTheControllerIsAlreadyShown_ThenAdapterShouldSaveNewPosition() {
		when(proView.getSelectedPosition()).thenReturn(2);
		HashMap<Enum<?>, Integer> testMap = new HashMap<Enum<?>, Integer>();
		testMap.put(ProPCView.ProTypes.ITEM_CLICK, 3);
		createPresenter();

		verify(proView).addListener(dataListener.capture(), eq(ProPCView.ProTypes.ITEM_CLICK));

		dataListener.getValue().fire(testMap);

		verify(proView).setSelectedPosition(3);
		verify(proView, never()).showController();
	}

	@Test
	public void whenAListItemIsClickedAndAlreadySelected_ThenTheControllerShouldHideAndAdapterClearSavedPosition() {
		when(proView.getSelectedPosition()).thenReturn(3);
		HashMap<Enum<?>, Integer> testMap = new HashMap<Enum<?>, Integer>();
		testMap.put(ProPCView.ProTypes.ITEM_CLICK, 3);
		createPresenter();

		verify(proView).addListener(dataListener.capture(), eq(ProPCView.ProTypes.ITEM_CLICK));

		dataListener.getValue().fire(testMap);

		verify(proView).clearChoices();
		verify(proView).setSelectedPosition(-1);
		verify(proView).hideController();
	}

	@Test
	public void whenMoveUpButtonIsClicked_ThenCurrentSelectedItemShouldMoveUp() {
		int testPos = 1;
		ArrayList<String> testItems = makeStringList("First", "Second", "Third");
		when(proView.getSelectedPosition()).thenReturn(testPos);
		when(proView.getAllItems()).thenReturn(testItems);
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.MOVE_UP));

		listener.getValue().fire();

		verify(proModel).moveItemUp(testPos, testItems);
		verify(proView).replaceItems(testItems);
		verify(proView).highlightAndSelect(testPos - 1);
	}

	@Test
	public void whenMoveUpButtonIsClickedWhenTopItemIsSelected_ThenNothingShouldHappen() {
		ArrayList<String> testItems = makeStringList("First", "Second", "Third");
		when(proView.getSelectedPosition()).thenReturn(0);
		when(proView.getAllItems()).thenReturn(testItems);
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.MOVE_UP));

		listener.getValue().fire();

		verify(proModel, never()).moveItemUp(0, testItems);
		verify(proView, never()).replaceItems(testItems);
		verify(proView, never()).highlightAndSelect(any(Integer.class));
	}

	@Test
	public void whenMovedDownButtonIsClicked_ThenCurrentSelectedItemShouldMoveDown() {
		int testPos = 1;
		ArrayList<String> testItems = makeStringList("First", "Second", "Third");
		when(proView.getSelectedPosition()).thenReturn(testPos);
		when(proView.getAllItems()).thenReturn(testItems);
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.MOVE_DOWN));

		listener.getValue().fire();

		verify(proModel).moveItemDown(testPos, testItems);
		verify(proView).replaceItems(testItems);
		verify(proView).highlightAndSelect(testPos + 1);
	}

	@Test
	public void whenMoveDownButtonIsClickedWhenBottomItemIsSelected_ThenNothingShouldHappen() {
		ArrayList<String> testItems = makeStringList("First", "Second", "Third");
		when(proView.getSelectedPosition()).thenReturn(2);
		when(proView.getAllItems()).thenReturn(testItems);
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.MOVE_DOWN));

		listener.getValue().fire();

		verify(proModel, never()).moveItemDown(0, testItems);
		verify(proView, never()).replaceItems(testItems);
		verify(proView, never()).highlightAndSelect(any(Integer.class));
	}

	@Test
	public void whenRemoveButtonIsPressed_ThenSelectedItemIsRemoved() {
		int testPos = 1;
		ArrayList<String> testItems = makeStringList("First", "Second", "Third");
		ArrayList<String> expectedItems = makeStringList("First", "Third");
		when(proView.getSelectedPosition()).thenReturn(testPos);
		when(proView.getAllItems()).thenReturn(testItems);
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.REMOVE));

		listener.getValue().fire();

		verify(proView).replaceItems(expectedItems);
	}

	@Test
	public void whenRemoveButtonIsPressedOnLastItem_ThenTheNextLastItemShouldBeManuallySelected() {
		int testPos = 2;
		ArrayList<String> testItems = makeStringList("First", "Second", "Third");
		when(proView.getSelectedPosition()).thenReturn(testPos);
		when(proView.getAllItems()).thenReturn(testItems);
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.REMOVE));

		listener.getValue().fire();

		verify(proView).highlightAndSelect(testPos - 1);
	}

	@Test
	public void whenRemoveButtonIsPressedForOnlyItem_ThenControllerDisappears() {
		int testPos = 0;
		ArrayList<String> testItems = makeStringList("First");
		when(proView.getSelectedPosition()).thenReturn(testPos);
		when(proView.getAllItems()).thenReturn(testItems);
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.REMOVE));

		listener.getValue().fire();

		verify(proView).hideController();
	}

	@Test
	public void whenUserResetsListFromMenu_ThenListShouldBeRestoredToNormalAndNoItemSelected() {
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.MENU_RESET_LIST));

		listener.getValue().fire();

		verify(proView).resetList();
		verify(proView).hideController();
		verify(proView).clearChoices();
		verify(proView).setSelectedPosition(-1);
	}

	@Test
	public void whenInfoButtonIsPressed_ThenEquationForSelectedItemIsDisplayed() {
		String selection = Constants.SIZE;
		String url = "file:///fake/dir";
		when(proView.getSelectedItem()).thenReturn(selection);
		when(proModel.getEquationUrl(selection)).thenReturn(url);
		createPresenter();

		verify(proView).addListener(listener.capture(), eq(ProPCView.ProTypes.INFO));

		listener.getValue().fire();

		verify(proModel).getEquationUrl(selection);
		verify(proView).displayItemInfo(url);
	}

	private ArrayList<String> makeStringList(String... args) {
		ArrayList<String> result = new ArrayList<String>();
		for (String item : args) {
			result.add(item);
		}
		return result;
	}
}
