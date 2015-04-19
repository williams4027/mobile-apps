package blake.com.flashtalk.test;

import blake.com.flashtalk.FlashTalkSplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class TerminateCard extends ActivityInstrumentationTestCase2<FlashTalkSplashScreenActivity> {
  	private Solo solo;
  	
  	public TerminateCard() {
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
		solo.waitForActivity(blake.com.flashtalk.FlashTalkSplashScreenActivity.class, 2000);
        //Wait for activity: 'blake.com.flashtalk.HomeActivity'
		assertTrue("blake.com.flashtalk.HomeActivity is not found!", solo.waitForActivity(blake.com.flashtalk.HomeActivity.class));
        //Set default small timeout to 37354 milliseconds
		Timeout.setSmallTimeout(37354);
        //Click on testtt
		solo.clickOnView(solo.getView(android.R.id.text1));
        //Wait for activity: 'blake.com.flashtalk.DeckMainActivity'
		assertTrue("blake.com.flashtalk.DeckMainActivity is not found!", solo.waitForActivity(blake.com.flashtalk.DeckMainActivity.class));
        //Click on hello
		solo.clickOnView(solo.getView(android.R.id.text1, 1));
        //Wait for activity: 'blake.com.flashtalk.EditCardActivity'
		assertTrue("blake.com.flashtalk.EditCardActivity is not found!", solo.waitForActivity(blake.com.flashtalk.EditCardActivity.class));
        //Click on ImageView
		solo.clickOnView(solo.getView(blake.com.flashtalk.R.id.btnDeleteCard));
        //Wait for dialog
		solo.waitForDialogToOpen(5000);
        //Click on Delete
		solo.clickOnView(solo.getView(android.R.id.button1));
	}
}
