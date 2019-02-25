package com.movies.mybakingapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.movies.mybakingapp.activities.MainRecipeListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class MainRecipeListTest {

    @Rule
    public ActivityTestRule<MainRecipeListActivity> mainRecipeListTestRule = new ActivityTestRule<>(MainRecipeListActivity.class);

//    @Before
//    public void init(){
//        mainRecipeListTestRule.getActivity().getSupportFragmentManager().beginTransaction();
//    }

    @Test
    public void clickListItem_OpensRecipeInstrictionsActivity() {
        // Click on the first item in the list
        onView(withId(R.id.recipe_recycle_list)).perform(actionOnItemAtPosition(0, click()));

        onView(allOf(instanceOf(TextView.class),
                withParent(withResourceName("action_bar"))))
                .check(matches(withText("Nutella Pie")));
    }

}
