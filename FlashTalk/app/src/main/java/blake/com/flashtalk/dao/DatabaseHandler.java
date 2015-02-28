package blake.com.flashtalk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blake on 8/25/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "FlashTalk";

    // Table Names
    private static final String TABLE_DECKS = "decks";
    private static final String TABLE_CARDS = "cards";

    // Deck Table Columns names
    private static final String DECK_KEY_ID = "id";
    private static final String DECK_KEY_DECK_NAME = "deckName";
    private static final String DECK_KEY_ANSWER_LOCALE = "answerLocale";
    private static final String DECK_KEY_HINT_LOCALE = "hintLocale";

    // Card Table Columns names
    private static final String CARD_KEY_ID = "id";
    private static final String CARD_DECK_ID = "deckId";
    private static final String CARD_KEY_ANSWER_STRING = "answerString";
    private static final String CARD_KEY_HINT_STRING = "hintString";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_DECK_TABLE = "CREATE TABLE " + TABLE_DECKS + "("
                + DECK_KEY_ID + " INTEGER PRIMARY KEY," + DECK_KEY_DECK_NAME + " TEXT,"
                + DECK_KEY_ANSWER_LOCALE + " TEXT,"
                + DECK_KEY_HINT_LOCALE + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_DECK_TABLE);

        String CREATE_CARD_TABLE = "CREATE TABLE " + TABLE_CARDS + "("
                + CARD_KEY_ID + " INTEGER PRIMARY KEY," + CARD_DECK_ID + " INTEGER,"
                + CARD_KEY_ANSWER_STRING + " TEXT,"
                + CARD_KEY_HINT_STRING + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CARD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * All Deck CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new deck
    public void addDeck(Deck deck) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DECK_KEY_DECK_NAME, deck.getDeckName());
        values.put(DECK_KEY_ANSWER_LOCALE, deck.getAnswerLocale());
        values.put(DECK_KEY_HINT_LOCALE, deck.getHintLocale());

        // Inserting Row
        db.insert(TABLE_DECKS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single deck
    public Deck getDeck(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DECKS, new String[] {DECK_KEY_ID,
                        DECK_KEY_DECK_NAME, DECK_KEY_ANSWER_LOCALE, DECK_KEY_HINT_LOCALE}, DECK_KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Deck deck = new Deck(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));

        return deck;
    }

    // Getting All Decks
    public List<Deck> getAllDecks() {
        List<Deck> deckList = new ArrayList<Deck>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DECKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Deck deck = new Deck();
                deck.setId(Integer.parseInt(cursor.getString(0)));
                deck.setDeckName(cursor.getString(1));
                deck.setAnswerLocale(cursor.getString(2));
                deck.setHintLocale(cursor.getString(3));
                deckList.add(deck);
            } while (cursor.moveToNext());
        }
        return deckList;
    }
//
//    // Updating single deck
//    public int updateContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//
//        // updating row
//        return db.update(TABLE_CONTACTS, values, DECK_KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//    }

    // Deleting single deck
    public void deleteDeck(Deck deck) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DECKS, DECK_KEY_ID + " = ?",
                new String[] { String.valueOf(deck.getId()) });
        db.close();
    }

//
//    // Getting contacts Count
//    public int getContactsCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
//    }

    /**
     * All Card CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new card
    public void addCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CARD_DECK_ID, card.getDeckId());
        values.put(CARD_KEY_ANSWER_STRING, card.getAnswerString());
        values.put(CARD_KEY_HINT_STRING, card.getHintString());

        // Inserting Row
        db.insert(TABLE_CARDS, null, values);
        db.close(); // Closing database connection
    }

    // Deleting single card
    public void deleteCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARDS, CARD_KEY_ID + " = ?",
                new String[] { String.valueOf(card.getId()) });
        db.close();
    }

    // Getting single card
    public Card getCard(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CARDS, new String[] {CARD_KEY_ID,
                        CARD_DECK_ID, CARD_KEY_ANSWER_STRING, CARD_KEY_HINT_STRING}, CARD_KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Card card = new Card(Long.parseLong(cursor.getString(0)),
                Long.parseLong(cursor.getString(1)), cursor.getString(2), cursor.getString(3));

        return card;
    }

    // Getting All Cards
    public List<Card> getAllCards() {
        List<Card> cardList = new ArrayList<Card>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CARDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setId(Long.parseLong(cursor.getString(0)));
                card.setDeckId(Long.parseLong(cursor.getString(1)));
                card.setAnswerString(cursor.getString(2));
                card.setHintString(cursor.getString(3));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        return cardList;
    }

    // Getting All Deck Cards
    public List<Card> getAllDeckCards(long deckId) {
        List<Card> cardList = new ArrayList<Card>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CARDS + " WHERE " + CARD_DECK_ID + " = " + String.valueOf(deckId);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setId(Long.parseLong(cursor.getString(0)));
                card.setDeckId(Long.parseLong(cursor.getString(1)));
                card.setAnswerString(cursor.getString(2));
                card.setHintString(cursor.getString(3));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        return cardList;
    }
}
