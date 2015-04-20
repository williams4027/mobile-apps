package blake.com.flashtalk.test;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.robotium.solo.Timeout;

import blake.com.flashtalk.FlashTalkSplashScreenActivity;
import blake.com.flashtalk.R;


public class TerminateDeck extends ActivityInstrumentationTestCase2<FlashTalkSplashScreenActivity> {
  	private Solo solo;

  	public TerminateDeck() {
		super(FlashTalkSplashScreenActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
  	}

   	@Override
   	public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
  	}

	public void testRun() {
        //Wait for activity: 'blake.com.flashtalk.FlashTalkSplashScreenActivity'
		solo.waitForActivity(FlashTalkSplashScreenActivity.class, 2000);
        //Wait for activity: 'blake.com.flashtalk.HomeActivity'
		assertTrue("blake.com.flashtalk.HomeActivity is not found!", solo.waitForActivity(blake.com.flashtalk.HomeActivity.class));
        //Set default small timeout to 37354 milliseconds
		Timeout.setSmallTimeout(37354);
        //Click on testtt
		solo.clickOnView(solo.getView(android.R.id.text1));
        //Wait for activity: 'blake.com.flashtalk.DeckMainActivity'
		assertTrue("blake.com.flashtalk.DeckMainActivity is not found!", solo.waitForActivity(blake.com.flashtalk.DeckMainActivity.class));
        //Click on hello
		solo.clickOnView(solo.getView(R.id.btnDeleteDeck));
        //Wait for dialog
		solo.waitForDialogToOpen(2000);
        //Click on Delete
		solo.clickOnView(solo.getView(android.R.id.button1));
	}
}
