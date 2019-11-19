package com.uimainon.mareus.rooteur.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.uimainon.mareus.R;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final Matcher<Integer> matcher;


        public static RecyclerViewItemCountAssertion withItemCount(int expectedCount) {
            return withItemCount(Matchers.is(expectedCount));
        }

        public static RecyclerViewItemCountAssertion withItemCount(Matcher<Integer> matcher) {
            return new RecyclerViewItemCountAssertion(matcher);
        }

        private RecyclerViewItemCountAssertion(Matcher<Integer> matcher) {
            this.matcher = matcher;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            Assert.assertThat(adapter.getItemCount(), matcher);
        }

    /** ajoute une réunion avec 1 participant a la liste pour les test*/
    public static void addMeetingForTest(){
        onView(withId(R.id.fab_add_new_metting )).perform(click());
        onView(allOf(withText("PARTICIPANTS"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.list_participant)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new AddParticipantAction()));
        onView(allOf(withText("Ma réu"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.valid_meeting )).perform(scrollTo(),click());
    }

    /** ajoute une réunion avec TOUS les participant a la liste pour les test
     * @param mAllParticipant*/
    public static void addMeetingWithEveryParticipantForTest(List<Participant> mAllParticipant){
        onView(withId(R.id.fab_add_new_metting )).perform(click());
        onView(allOf(withText("PARTICIPANTS"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        int sizeListParticiapnt = mAllParticipant.size();
        for(int i = 0 ; i <sizeListParticiapnt ; i++){
            onView(withId(R.id.list_participant)).perform(RecyclerViewActions.actionOnItemAtPosition(i, new AddParticipantAction()));
        }
        onView(allOf(withText("Ma réu"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
        onView(withId(R.id.valid_meeting)).perform(scrollTo(), click());
    }
    /** ajoute autant de réunion qu'il y a de salle de réunion
     * @param mAllRoom*/
    public static void addMeetingWithEveryRoomForTest(List<Room> mAllRoom){
        int sizeListRoom = mAllRoom.size();
        for(int i = 0 ; i <sizeListRoom ; i++){
            onView(withId(R.id.fab_add_new_metting )).perform(click());
            onView(allOf(withText("PARTICIPANTS"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
            onView(withId(R.id.list_participant)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new AddParticipantAction()));
            onView(allOf(withText("Ma réu"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
            onView(withId(R.id.valid_meeting)).perform(scrollTo(), click());
        }
    }

    /** supprime TOUS les item du recyclerView pour les test*/
    public static void deleteItemRecyclerView(List<Meeting> mMeeting){
        if(mMeeting.size()!=0){
            int sizeMeeting = mMeeting.size();
            for(int i = 0 ; i < sizeMeeting ; i++){
                onView(withId(R.id.list_meeting)).perform(RecyclerViewActions.actionOnItemAtPosition(i, new DeleteMeetingAction()));
            }
        }
    }
    }