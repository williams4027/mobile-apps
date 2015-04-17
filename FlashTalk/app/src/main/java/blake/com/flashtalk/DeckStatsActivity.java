package blake.com.flashtalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import blake.com.flashtalk.dao.Card;
import blake.com.flashtalk.dao.Deck;
import blake.com.flashtalk.data.StatisticsArrayAdapter;


public class DeckStatsActivity extends Activity {

    private final Context activityContext = this;

    private Deck selectedDeck;

    private StatisticsArrayAdapter cardAdapter;

    private boolean flaggedOnly = false;
    private ImageButton toggleFlaggedCardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_stats);

        selectedDeck = getIntent().getParcelableExtra("SelectedDeck");
        // Reading deck used
        Log.d("DEBUG: DeckMain", "Printing Deck..");
        Log.d("Deck ID: ", String.valueOf(selectedDeck.getId()));
        Log.d("Deck DeckName: ", selectedDeck.getDeckName());
        Log.d("Deck AnswerLocale: ", selectedDeck.getAnswerLocale());
        Log.d("Deck HintLocale: ", selectedDeck.getHintLocale());

        updateStatList();

        // TODO: Implement flagged cards in the future, supporting when studying
        //       toggleFlaggedCardButton = (ImageButton)findViewById(R.id.btnToggleFlagged);
        //        toggleFlaggedCardButton.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                flaggedOnly = !flaggedOnly;
        //                updateStatList();
        //            }
        //        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatList();
    }

    private void updateStatList() {
        // Populate the displayed user decks
        List<Card> sortedCards = new ArrayList<Card>();
        if (this.flaggedOnly) {
            List<Card> allCards = selectedDeck.getCards();
            for (Card card : allCards) {
                if (card.getCardStatistic().is_isFlagged()) {
                    sortedCards.add(card);
                }
            }
        } else {
            sortedCards = selectedDeck.getCards();
        }
        Collections.sort(sortedCards, new Comparator<Card>() {
            @Override
            public int compare(Card card, Card card2) {
                return Double.compare(StatisticsArrayAdapter.calculateCardStatistics(card.getCardStatistic()), StatisticsArrayAdapter.calculateCardStatistics(card2.getCardStatistic()));
            }
        });

        // Populate the card list
        final ListView cardListView = (ListView)findViewById(R.id.cardList);
        cardAdapter = new StatisticsArrayAdapter(this,
                R.layout.card_statistic_row, sortedCards);
        cardListView.setAdapter(cardAdapter);
        registerForContextMenu(cardListView);

        // Set click listener
        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent cardEditIntent = new Intent(DeckStatsActivity.this, EditCardActivity.class);

                // Get the deck selected and attach
                int itemPosition = position;
                Card selectedCard = (Card) cardListView.getItemAtPosition(itemPosition);
                cardEditIntent.putExtra("SelectedDeck", selectedDeck);
                cardEditIntent.putExtra("SelectedCard", (android.os.Parcelable) selectedCard);
                startActivity(cardEditIntent);
            }
        });
    }
}