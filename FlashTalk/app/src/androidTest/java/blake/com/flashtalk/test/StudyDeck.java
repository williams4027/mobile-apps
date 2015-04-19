package blake.com.flashtalk.test;

import blake.com.flashtalk.FlashTalkSplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class StudyDeck extends ActivityInstrumentationTestCase2<FlashTalkSplashScreenActivity> {
  	private Solo solo;
  	
  	public StudyDeck() {
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
        //Set default small timeout to 37262 milliseconds
		Timeout.setSmallTimeout(37262);
        //Click on testtt
		solo.clickOnView(solo.getView(android.R.id.text1));
        //Wait for activity: 'blake.com.flashtalk.DeckMainActivity'
		assertTrue("blake.com.flashtalk.DeckMainActivity is not found!", solo.waitForActivity(blake.com.flashtalk.DeckMainActivity.class));
        //Click on ImageView
		solo.clickOnView(solo.getView(blake.com.flashtalk.R.id.btnQuiz));
        //Wait for activity: 'blake.com.flashtalk.CardFlipActivity'
		assertTrue("blake.com.flashtalk.CardFlipActivity is not found!", solo.waitForActivity(blake.com.flashtalk.CardFlipActivity.class));
        //Click on hola
		solo.clickOnView(solo.getView(blake.com.flashtalk.R.id.cardContainer));
        //Click on ImageView
		solo.clickOnView(solo.getView(blake.com.flashtalk.R.id.correctAnswerButton));
	}
}
