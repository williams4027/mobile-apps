package blake.com.flashtalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import blake.com.flashtalk.dao.Card;
import blake.com.flashtalk.dao.DatabaseHandler;
import blake.com.flashtalk.dao.Deck;


public class DeckMainActivity extends Activity {

    private final Context activityContext = this;

    private ImageButton btnAddCards;
    private ImageButton btnQuiz;

    private Deck selectedDeck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_main);

        selectedDeck = getIntent().getParcelableExtra("SelectedDeck");
        // Reading deck used
        Log.d("DEBUG: DeckMain", "Printing Deck..");
        Log.d("Deck ID: ", String.valueOf(selectedDeck.getId()));
        Log.d("Deck DeckName: ", selectedDeck.getDeckName());
        Log.d("Deck AnswerLocale: ", selectedDeck.getAnswerLocale());
        Log.d("Deck HintLocale: ", selectedDeck.getHintLocale());

        btnAddCards = (ImageButton)findViewById(R.id.btnAddCards);
        btnAddCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCardIntent = new Intent(DeckMainActivity.this, AddCardActivity.class);

                // Get the deck selected and attach
                addCardIntent.putExtra("SelectedDeck", selectedDeck);
                startActivity(addCardIntent);
            }
        });

        btnQuiz = (ImageButton)findViewById(R.id.btnQuiz);
        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent quizIntent = new Intent(DeckMainActivity.this, CardFlipActivity.class);
                // Get the deck selected and attach
                quizIntent.putExtra("SelectedDeck", selectedDeck);
                startActivity(quizIntent);
            }
        });

        // Reading all contacts
        Log.d("DEBUG: DeckMain", "Reading all cards..");
        DatabaseHandler db = new DatabaseHandler(this);
        List<Card> cards = db.getAllDeckCards(selectedDeck.getId());
        for ( Card tempCard : cards ){
            Log.d("Card ID: ", String.valueOf(tempCard.getId()));
            Log.d("Card Deck ID: ", String.valueOf(tempCard.getHintString()));
            Log.d("Card Answer: ", tempCard.getAnswerString());
            Log.d("Card Hint: ", tempCard.getHintString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.deck_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
