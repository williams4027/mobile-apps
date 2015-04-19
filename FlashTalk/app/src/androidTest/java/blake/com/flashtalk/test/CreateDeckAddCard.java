package blake.com.flashtalk.test;

import blake.com.flashtalk.FlashTalkSplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class CreateDeckAddCard extends ActivityInstrumentationTestCase2<FlashTalkSplashScreenActivity> {
  	private Solo solo;
  	
  	public CreateDeckAddCard() {
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
        //Click on Create a New Card
		solo.clickOnView(solo.getView(android.R.id.text1));
        //Wait for activity: 'blake.com.flashtalk.EditCardActivity'
		assertTrue("blake.com.flashtalk.EditCardActivity is not found!", solo.waitForActivity(blake.com.flashtalk.EditCardActivity.class));
        //Set default small timeout to 10629 milliseconds
		Timeout.setSmallTimeout(10629);
        //Enter the text: 'hello'
		solo.clearEditText((android.widget.EditText) solo.getView(blake.com.flashtalk.R.id.textAnswer));
		solo.enterText((android.widget.EditText) solo.getView(blake.com.flashtalk.R.id.textAnswer), "hello");
        //Click on Empty Text View
		solo.clickOnView(solo.getView(blake.com.flashtalk.R.id.textHint));
        //Enter the text: 'hola'
		solo.clearEditText((android.widget.EditText) solo.getView(blake.com.flashtalk.R.id.textHint));
		solo.enterText((android.widget.EditText) solo.getView(blake.com.flashtalk.R.id.textHint), "hola");
        //Click on ImageView
		solo.clickOnView(solo.getView(blake.com.flashtalk.R.id.btnSaveCard));
	}
}
