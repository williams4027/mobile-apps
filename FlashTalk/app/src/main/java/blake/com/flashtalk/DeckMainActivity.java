package blake.com.flashtalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import blake.com.flashtalk.dao.Card;
import blake.com.flashtalk.dao.DatabaseHandler;
import blake.com.flashtalk.dao.Deck;


public class DeckMainActivity extends Activity {

    private final Context activityContext = this;

    private ImageButton deckStats;
    private ImageButton deleteDeckButton;

    private ImageButton btnQuiz;

    private Deck selectedDeck;
    private List<Card> currentCards = new ArrayList<Card>();

    private ArrayAdapter<Card> cardAdapter;
    private Card createCardPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deck_main);

        selectedDeck = getIntent().getParcelableExtra("SelectedDeck");
        // Reading deck used
        Log.d("DEBUG: DeckMain", "Printing Deck..");
        Log.d("Deck ID: ", String.valueOf(selectedDeck.getId()));
        Log.d("Deck DeckName: ", selectedDeck.getDeckName());
        Log.d("Deck AnswerLocale: ", selectedDeck.getAnswerLocale());
        Log.d("Deck HintLocale: ", selectedDeck.getHintLocale());

        updateCardList();

//        btnAddCards = (ImageButton)findViewById(R.id.btnAddCards);
//        btnAddCards.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent addCardIntent = new Intent(DeckMainActivity.this, AddCardActivity.class);
//
//                // Get the deck selected and attach
//                addCardIntent.putExtra("SelectedDeck", selectedDeck);
//                startActivity(addCardIntent);
//            }
//        });

        deckStats = (ImageButton)findViewById(R.id.btnDeckStats);
        deckStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent statsIntent = new Intent(DeckMainActivity.this, DeckStatsActivity.class);
                // Get the deck selected and attach
                statsIntent.putExtra("SelectedDeck", selectedDeck);
                startActivity(statsIntent);
            }
        });

        deleteDeckButton = (ImageButton)findViewById(R.id.btnDeleteDeck);
        deleteDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeckMainActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Confirm Delete...");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want delete this deck and all the associated cards?");

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        Log.d("DEBUG","Delete Selected");
                        // Get the deck selected and delete
                        DatabaseHandler db = DatabaseHandler.getInstance(activityContext);
                        db.deleteDeck(selectedDeck);
                        db.close();
                        finish();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        });

        btnQuiz = (ImageButton)findViewById(R.id.btnQuiz);
        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentCards.size() > 1) {
                    Intent quizIntent = new Intent(DeckMainActivity.this, CardFlipActivity.class);
                    // Get the deck selected and attach
                    quizIntent.putExtra("SelectedDeck", selectedDeck);
                    startActivity(quizIntent);
                } else {
                    Toast.makeText(activityContext, "Please create a card before studying.", Toast.LENGTH_SHORT).show();
                }
            }
        });
//
//        // Reading all contacts
//        Log.d("DEBUG: DeckMain", "Reading all cards..");
//        DatabaseHandler db = new DatabaseHandler(this);
//        List<Card> cards = db.getAllDeckCards(selectedDeck.getId());
//        for ( Card tempCard : cards ){
//            Log.d("Card ID: ", String.valueOf(tempCard.getId()));
//            Log.d("Card Deck ID: ", String.valueOf(tempCard.getHintString()));
//            Log.d("Card Answer: ", tempCard.getAnswerString());
//            Log.d("Card Hint: ", tempCard.getHintString());
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCardList();
    }

    private void updateCardList() {
        // Populate the displayed user decks
        createCardPlaceholder = new Card();
        createCardPlaceholder.setAnswerString("Create a New Card");
        currentCards = new ArrayList<Card>();
        currentCards.add(createCardPlaceholder);
        currentCards.addAll(selectedDeck.getCards());
        Log.d("Card Count: ", String.valueOf(currentCards.size()));

        // Populate the card list
        final ListView cardListView = (ListView)findViewById(R.id.cardList);
        cardAdapter = new ArrayAdapter<Card>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, currentCards);
        cardListView.setAdapter(cardAdapter);
        registerForContextMenu(cardListView);

        // Set click listener
        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent cardEditIntent = new Intent(DeckMainActivity.this, EditCardActivity.class);

                // Get the deck selected and attach
                int itemPosition = position;
                Card selectedCard = (Card) cardListView.getItemAtPosition(itemPosition);
                cardEditIntent.putExtra("SelectedDeck", selectedDeck);
                if (selectedCard !=  createCardPlaceholder) {
                    cardEditIntent.putExtra("SelectedCard", selectedCard);
                }
                startActivity(cardEditIntent);
            }
        });
    }
}
