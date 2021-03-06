package blake.com.flashtalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import blake.com.flashtalk.dao.DatabaseHandler;
import blake.com.flashtalk.dao.Deck;


public class HomeActivity extends Activity {

    private final Context activityContext = this;

    private ImageButton btnAddDeck;

    private List<Deck> currentDecks;
    private ArrayAdapter<Deck> deckAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deck_list);

        updateDeckList();

        // Reading all contacts
        Log.d("DEBUG: HomeActivity", "Logging Decks..");
        for ( Deck tempDeck : currentDecks ){
            Log.d("Deck ID: ", String.valueOf(tempDeck.getId()));
            Log.d("Deck DeckName: ", tempDeck.getDeckName());
            Log.d("Deck AnswerLocale: ", tempDeck.getAnswerLocale());
            Log.d("Deck HintLocale: ", tempDeck.getHintLocale());
        }

        btnAddDeck = (ImageButton)findViewById(R.id.btnAddDeck);
        btnAddDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent deckEditIntent = new Intent(HomeActivity.this, EditDeckActivity.class);
                startActivity(deckEditIntent);
            }
        });

        Log.d("LIFECYCLE DEBUG:", "Home Activity: In On Stop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDeckList();

        Log.d("LIFECYCLE DEBUG:", "Home Activity: In On Resume");
    }

    private void updateDeckList() {
        // Populate the displayed user decks
        DatabaseHandler db = DatabaseHandler.getInstance(activityContext);
        currentDecks = db.getAllDecks();
        db.close();
        Log.d("Deck Count: ", String.valueOf(currentDecks.size()));

        final ListView deckListView = (ListView)findViewById(R.id.deckList);
        deckAdapter = new ArrayAdapter<Deck>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, currentDecks);
        deckListView.setAdapter(deckAdapter);
        registerForContextMenu(deckListView);

        // Set click listener
        deckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent deckMainIntent = new Intent(HomeActivity.this, DeckMainActivity.class);

                // Get the deck selected and attach
                int itemPosition = position;
                Deck selectedDeck = (Deck) deckListView.getItemAtPosition(itemPosition);
                deckMainIntent.putExtra("SelectedDeck", selectedDeck);
                startActivity(deckMainIntent);
            }
        });

        deckAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LIFECYCLE DEBUG:", "Home Activity: In On Stop");
    }


}
