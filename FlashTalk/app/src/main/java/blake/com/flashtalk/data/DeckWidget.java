package blake.com.flashtalk.data;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import blake.com.flashtalk.dao.Deck;

/**
 * Widget that will be created and saved to memory to load on the Home page.
 * It will contain an ID reference to the deck in the database,
 * as well as the languages that are being
 * Created by Blake on 8/22/2014.
 */
public class DeckWidget extends Button {

    private String deckName;
    private Locale answerLocale;
    private Locale hintLocale;
    private long deckID;

    public DeckWidget(Context context, Deck deck) {
        super(context);
        this.deckID = deck.getId();
        this.answerLocale = new Locale(deck.getAnswerLocale());
        this.hintLocale = new Locale(deck.getHintLocale());
        this.deckName = deck.getDeckName();
        this.setText(deckName);
    }
}
