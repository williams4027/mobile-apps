package blake.com.flashtalk;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import blake.com.flashtalk.dao.Card;
import blake.com.flashtalk.dao.DatabaseHandler;
import blake.com.flashtalk.dao.Deck;


public class AddCardActivity extends Activity {

    private final Context activityContext = this;

    protected static final int ANSWER_RESULT_SPEECH = 1;
    protected static final int HINT_RESULT_SPEECH = 2;

    private ImageButton btnAnswerSpeak;
    private EditText txtAnswerResult;
    private TextView labelAnswer;

    private ImageButton btnHintSpeak;
    private EditText txtHintResult;
    private TextView labelHint;

    private Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        deck = getIntent().getParcelableExtra("SelectedDeck");

        txtAnswerResult = (EditText) findViewById(R.id.textAnswer);
        btnAnswerSpeak = (ImageButton) findViewById(R.id.btnAnswerSpeak);
        labelAnswer = (TextView)findViewById(R.id.txtAnswerLabel);
        if ( deck.getAnswerLocale() != null ){
            labelAnswer.setText(new Locale(deck.getAnswerLocale()).getDisplayName());
        }

        txtHintResult = (EditText) findViewById(R.id.textHint);
        btnHintSpeak = (ImageButton) findViewById(R.id.btnHintSpeak);
        labelHint = (TextView)findViewById(R.id.txtHintLabel);
        if ( deck.getHintLocale() != null ){
            labelHint.setText(new Locale(deck.getHintLocale()).getDisplayName());
        }

        btnAnswerSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                generateLanguageIntent(deck.getAnswerLocale(), ANSWER_RESULT_SPEECH, txtAnswerResult);
            }
        });

        btnHintSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                generateLanguageIntent(deck.getHintLocale(), HINT_RESULT_SPEECH, txtHintResult);
            }
        });

        ImageButton btnCreateCard = (ImageButton)findViewById(R.id.btnCreateCard);
        btnCreateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( txtAnswerResult.getText() != null && txtHintResult.getText() != null) {
                    Card newCard = new Card(deck.getId(), txtAnswerResult.getText().toString(), txtHintResult.getText().toString());
                    DatabaseHandler db = new DatabaseHandler(activityContext);
                    db.addCard(newCard);
                    db.close();
                }
            }
        });

        // Reading all contacts
        DatabaseHandler db = new DatabaseHandler(activityContext);
        List<Card> currentCards = db.getAllCards();
        db.close();
        Log.d("Reading: ", "Reading all contacts..");
        for ( Card tempCard : currentCards ){
            Log.d("CARD: ", tempCard.getId() + tempCard.getDeckId() + tempCard.getAnswerString() + tempCard.getHintString());
        }
    }

    private void generateLanguageIntent(String languageCode, int intentCode, TextView txtOutput ){
        if ( languageCode != null ) {
            Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode);
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode);

            try {
                startActivityForResult(intent, intentCode);
                txtOutput.setText("");
            } catch (ActivityNotFoundException a) {
                Toast t = Toast.makeText(getApplicationContext(),
                        "Opps! Your device doesn't support Speech to Text",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        } else {
            Toast t = Toast.makeText(getApplicationContext(),
                    "We are experiencing translation errors. Please update and try later.",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ANSWER_RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtAnswerResult.setText(text.get(0));
                }
                break;
            }
            case HINT_RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtHintResult.setText(text.get(0));
                }
                break;
            }
        }
    }
}
