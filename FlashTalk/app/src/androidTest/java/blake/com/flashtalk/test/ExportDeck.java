package blake.com.flashtalk.test;

import blake.com.flashtalk.FlashTalkSplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class ExportDeck extends ActivityInstrumentationTestCase2<FlashTalkSplashScreenActivity> {
  	private Solo solo;
  	
  	public ExportDeck() {
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
        //Click on testtt
		solo.clickOnView(solo.getView(android.R.id.text1));
        //Wait for activity: 'blake.com.flashtalk.DeckMainActivity'
		assertTrue("blake.com.flashtalk.DeckMainActivity is not found!", solo.waitForActivity(blake.com.flashtalk.DeckMainActivity.class));
        //Click on ImageView
		solo.clickOnView(solo.getView(blake.com.flashtalk.R.id.btnExportDeck));
        //Wait for activity: 'blake.com.flashtalk.ExportDeckActivity'
		assertTrue("blake.com.flashtalk.ExportDeckActivity is not found!", solo.waitForActivity(blake.com.flashtalk.ExportDeckActivity.class));
        //Wait for dialog
		solo.waitForDialogToOpen(5000);
        //Click on OK
		solo.clickOnView(solo.getView(android.R.id.button1));
	}
}
