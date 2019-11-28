package com.uimainon.mareus.rooteur;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.uimainon.mareus.R;
import com.uimainon.mareus.controlleur.MeetingService;
import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.rooteur.utils.DeleteMeetingAction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.uimainon.mareus.rooteur.utils.RecyclerViewItemCountAssertion.addMeetingForTest;
import static com.uimainon.mareus.rooteur.utils.RecyclerViewItemCountAssertion.deleteItemRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class List_Meeting_ActivityTest {

    private static List<Meeting> mListMeeting;
    private static String mMail = "caroline@lamzone.com";
    private static Participant mParticipant = new Participant(1, mMail  ,0);
    private ParticipantsList mListParticipant = new ParticipantsList();
    private static Room mRoom = new Room(1, "Réunion A", "#fed1c8");

    @Rule
    public ActivityTestRule<ListMeetingActivity> mActivityRule  =
            new ActivityTestRule(ListMeetingActivity.class);


    @Before
    public void setUp() {
        ListMeetingActivity mActivity = mActivityRule.getActivity();
        assertThat(mActivity, notNullValue());
        MeetingService mService = DI.getMeetingService();
        mListMeeting = mService.AllMeetings();

    }

    /** Start application, show view "textViewNothing" */
    @Test
    public void ifThereAreNoMeetinginList_showEmptyView() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        onView(withId(R.id.textViewNothing)).check(matches(isDisplayed()));
        onView(withId(R.id.list_meeting)).check(matches(not(isDisplayed())));
    }

    /** If we click on "add button meeting, show new activity */
    @Test
    public void ifClickOnAddMeetingButton_showNewActivity() {
        onView(withId(R.id.fab_add_new_metting )).perform(click());
        onView(allOf(withText("PARTICIPANTS"), isDescendantOfA(withId(R.id.tabs))));
    }

    /** If we click on "menu filter by" + "filtrer par room", if we have meeting, show room dialog */
    @Test
    public void ifClickOnMenuItem_filterByRoom_showRoomDialogWindows() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        addMeetingForTest();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings2)).perform(click());

        onView(withId(R.id.gridviewRoom)).check(matches(isDisplayed()));
    }

    /** If we click on "menu filter by" + "filtrer par date", if we have meeting, show date dialog */
    @Test
    public void ifClickOnMenuItem_filterByDate_showDateDialogWindows() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        addMeetingForTest();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());

        onView(withId(R.id.contain_calendar_dialog_meeting)).check(matches(isDisplayed()));
    }

    /** If we click on "menu filter by" +item, if we have not meeting, show nothing dialog */
    @Test
    public void ifClickOnMenuItem_noMeetingIsPresent_showNothingDialogWindows() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());

        onView(withId(R.id.nothingMeetingDial)).check(matches(isDisplayed()));
    }


    /** If we add a meeting, show new view in first Activity*/
    @Test
    public void ifAddNewMeeting_showNewActivity_withGoodView() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        addMeetingForTest();
        onView(withId(R.id.textViewNothing)).check(matches(not(isDisplayed())));
        onView(withId(R.id.list_meeting)).check(matches(isDisplayed()));
    }

    /** If we add a meeting, show first Activity with the new item*/
    @Test
    public void ifAddNewMeeting_showNewActivity_withTheNewItem() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        addMeetingForTest();
        onView(withId(R.id.list_meeting)).check(matches(hasMinimumChildCount(1)));
    }


    /** If we click on item recyclerView, show new Activity with the good item attributes */
    @Test
    public void ifClickOnItemMeeting_showNewActivity_withGoodAttributes() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        addMeetingForTest();
        String mail = mListMeeting.get(0).getParticipants().get(0).getEmail();
        onView(withId(R.id.list_meeting)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(allOf(withText("Ma réu"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.listParticipantAdded )).check(matches(withText(mail +"\n")));
    }


    /** If we click on delette button on recyclerView item, delete the meeting */
    @Test
    public void ifClickOndeleteButton_removeItem() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        addMeetingForTest();
        addMeetingForTest();
        onView(withId(R.id.list_meeting))
                .check(matches(hasMinimumChildCount(2)));
        onView(withId(R.id.list_meeting)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new DeleteMeetingAction()));
        onView(withId(R.id.list_meeting))
                .check(matches(hasMinimumChildCount(1)));
    }

    /** If we delete every meeting, show the view "textViewNothing" */
    @Test
    public void ifDeleteEveryMeeting_ShowTheGoodView() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        addMeetingForTest();
        onView(withId(R.id.list_meeting)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.textViewNothing)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.list_meeting))
                .check(matches(hasMinimumChildCount(1)));
        onView(withId(R.id.list_meeting)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new DeleteMeetingAction()));
        onView(withId(R.id.list_meeting)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.textViewNothing)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }


   //---------------------------------------------------------------------------------------------------------


}