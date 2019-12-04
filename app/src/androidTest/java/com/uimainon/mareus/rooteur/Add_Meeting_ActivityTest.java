package com.uimainon.mareus.rooteur;

import android.widget.TimePicker;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import com.uimainon.mareus.R;
import com.uimainon.mareus.controlleur.MeetingService;
import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.rooteur.utils.AddParticipantAction;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.uimainon.mareus.rooteur.utils.RecyclerViewItemCountAssertion.addMeetingWithEveryParticipantForTest;
import static com.uimainon.mareus.rooteur.utils.RecyclerViewItemCountAssertion.deleteItemRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class Add_Meeting_ActivityTest {

    private static List<Meeting> mListMeeting;
    private static List<Participant> mAllParticipant;
    private static List<Room> mAllRoom;
    private static String mMail = "caroline@lamzone.com";
    private static Participant mParticipant = new Participant(1, mMail, 0);
    private ParticipantsList mListParticipant = new ParticipantsList();
    private static Room mRoom = new Room(1, "Réunion A", "#fed1c8");
    private static int mHour = 10;
    private static int mMinute = 43;

    @Rule
    public ActivityTestRule<ListMeetingActivity> mActivityRule =
            new ActivityTestRule(ListMeetingActivity.class);

    @Before
    public void setUp() {
        ListMeetingActivity mActivity = mActivityRule.getActivity();
        assertThat(mActivity, notNullValue());
        MeetingService mService = DI.getMeetingService();
        mListMeeting = mService.AllMeetings();
        Meeting mMeeting = new Meeting(0, "14-11-2019", 14, 10, "Aucun sujet", mListParticipant, mRoom);
        mAllParticipant = mService.AllParticipants();
        mAllRoom = mService.AllRooms();
    }

    /**
     * Show the same Participant in fragment "Participant", fragment "Ma réu" and in recyclerView in the first Activity
     */
    @Test
    public void showTheSameParticipant_InTwoDifferentFragment() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        onView(withId(R.id.fab_add_new_metting)).perform(click());
        onView(allOf(withText("PARTICIPANTS"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        Espresso.onView(withId(R.id.list_participant))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(mMail)), new AddParticipantAction()));
        onView(allOf(withText("Ma réu"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.listParticipantAdded)).check(matches(withText(mMail + "\n")));
        onView(withId(R.id.valid_meeting)).perform(click());
        onView(withId(R.id.list_meeting)).check(matches(hasDescendant(withText(mMail))));
    }

    /**
     * when we add new participant in new meeting, show this mail in the "Ma réu" fragment,
     * and after,
     * if we remove this participant in the "Participants" fragment, remove this mail in the "Ma réu" fragment
     * and show text "Il n'y a aucun participant pour le moment"
     */
    @Test
    public void showTheSameMail_andRemoveTheSameMail() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        onView(withId(R.id.fab_add_new_metting)).perform(click());
        onView(allOf(withText("PARTICIPANTS"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        Espresso.onView(withId(R.id.list_participant))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(mMail)), new AddParticipantAction()));
        onView(allOf(withText("Ma réu"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.listParticipantAdded)).check(matches(withText(mMail + "\n")));
        onView(allOf(withText("PARTICIPANTS"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        Espresso.onView(withId(R.id.list_participant))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(mMail)), new AddParticipantAction()));
        onView(allOf(withText("Ma réu"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.listParticipantAdded)).check(matches(withText("Il n'y a aucun participant pour le moment")));
    }

    /**
     * when we change hour in the "Date" fragment, the same time in Fragment "DATE" and "MA REU"
     */
    @Test
    public void changeHourInOneFragment_andShowTheSameHourInOtherFragment() {
        onView(withId(R.id.fab_add_new_metting)).perform(click());
        onView(allOf(withText("DATE"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(mHour, mMinute));
        onView(allOf(withText("Ma réu"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.hourMeeting)).check(matches(withText(mHour + " : " + mMinute)));
    }

    /**
     * Show the good view if there are no participants in the fragment "Participants"
     * (when every participant are always in meeting)
     */
    @Test
    public void showNoParticiapnt_inFragment_whenEveryParticipantAreAlwaysInMeeting() {
        deleteItemRecyclerView(mListMeeting);
        mListMeeting.clear();
        addMeetingWithEveryParticipantForTest(mAllParticipant);
        onView(withId(R.id.fab_add_new_metting)).perform(click());
        onView(allOf(withText("PARTICIPANTS"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.containerRecap)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textNoParticpantDispo)).check(matches(isDisplayed()));
    }
}


