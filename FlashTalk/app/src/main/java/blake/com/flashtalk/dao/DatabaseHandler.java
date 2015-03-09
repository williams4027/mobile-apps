package blake.com.flashtalk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler instance = null;
    private Context context;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "FlashTalk";

    // Table Names
    private static final String TABLE_DECKS = "decks";
    private static final String TABLE_CARDS = "cards";
    private static final String TABLE_CARD_STATISTICS = "card_stats";

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

    // Card Table Columns names
    private static final String CARD_STAT_KEY_ID = "id";
    private static final String CARD_STAT_CARD_ID = "cardId";
    private static final String CARD_STAT_FLAGGED = "isFlagged";
    private static final String CARD_STAT_CORRECT_COUNT = "correctCount";
    private static final String CARD_STAT_INCORRECT_COUNT = "incorrectCount";
    private static final String CARD_STAT_SKIP_COUNT = "skipCount";

    public static DatabaseHandler getInstance(Context context) {
        if (instance == null){
            instance = new DatabaseHandler(context);
        }
        return instance;
    }

    private DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_DECK_TABLE = "CREATE TABLE " + TABLE_DECKS + "("
                + DECK_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DECK_KEY_DECK_NAME + " TEXT,"
                + DECK_KEY_ANSWER_LOCALE + " TEXT,"
                + DECK_KEY_HINT_LOCALE + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_DECK_TABLE);

        String CREATE_CARD_TABLE = "CREATE TABLE " + TABLE_CARDS + "("
                + CARD_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CARD_DECK_ID + " INTEGER,"
                + CARD_KEY_ANSWER_STRING + " TEXT,"
                + CARD_KEY_HINT_STRING + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CARD_TABLE);

        String CREATE_CARD_STATS_TABLE = "CREATE TABLE " + TABLE_CARD_STATISTICS + "("
                + CARD_STAT_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CARD_STAT_CARD_ID + " INTEGER,"
                + CARD_STAT_FLAGGED + " INTEGER,"
                + CARD_STAT_CORRECT_COUNT + " INTEGER,"
                + CARD_STAT_INCORRECT_COUNT + " INTEGER,"
                + CARD_STAT_SKIP_COUNT + " INTEGER" + ")";
        sqLiteDatabase.execSQL(CREATE_CARD_STATS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD_STATISTICS);

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
    public Deck getDeck(long id) {
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
        for(Card card : getAllDeckCards(deck.getId())){
            deleteCard(card);
        }
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

    // Update single card
    public void updateCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CARD_KEY_ID, card.getId());
        values.put(CARD_DECK_ID, card.getDeckId());
        values.put(CARD_KEY_ANSWER_STRING, card.getAnswerString());
        values.put(CARD_KEY_HINT_STRING, card.getHintString());

        db.update(TABLE_CARDS, values, CARD_KEY_ID + " = ?",
                new String[] { String.valueOf(card.getId()) });

        db.close();
    }

    // Deleting single card
    public void deleteCard(Card card) {
        deleteCardStatistic(card.getCardStatistic());
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARDS, CARD_KEY_ID + " = ?",
                new String[] { String.valueOf(card.getId()) });
        db.close();
    }

    // Getting single card
    public Card getCard(long id) {
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

    // Adding new card statistic
    public void addCardStatistic(CardStatistic cardStatistic) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CARD_STAT_CARD_ID, cardStatistic.get_cardId());
        values.put(CARD_STAT_FLAGGED, cardStatistic.is_isFlagged());
        values.put(CARD_STAT_CORRECT_COUNT, cardStatistic.get_correctCount());
        values.put(CARD_STAT_INCORRECT_COUNT, cardStatistic.get_incorrectCount());
        values.put(CARD_STAT_SKIP_COUNT, cardStatistic.get_skipCount());

        // Inserting Row
        db.insert(TABLE_CARD_STATISTICS, null, values);
        db.close(); // Closing database connection
    }

    // Update single card statistic
    public void updateCardStatistic(CardStatistic cardStatistic) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CARD_STAT_KEY_ID, cardStatistic.getId());
        values.put(CARD_STAT_CARD_ID, cardStatistic.get_cardId());
        values.put(CARD_STAT_FLAGGED, cardStatistic.is_isFlagged());
        values.put(CARD_STAT_CORRECT_COUNT, cardStatistic.get_correctCount());
        values.put(CARD_STAT_INCORRECT_COUNT, cardStatistic.get_incorrectCount());
        values.put(CARD_STAT_SKIP_COUNT, cardStatistic.get_skipCount());

        db.update(TABLE_CARD_STATISTICS, values, CARD_STAT_KEY_ID + " = ?",
                new String[] { String.valueOf(cardStatistic.getId()) });

        db.close();
    }

    // Deleting single card statistic
    public void deleteCardStatistic(CardStatistic cardStatistic) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARD_STATISTICS, CARD_STAT_KEY_ID + " = ?",
                new String[] { String.valueOf(cardStatistic.getId()) });
        db.close();
    }

    // Getting single card
    public CardStatistic getCardStatistic(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CARD_STATISTICS, new String[] {CARD_STAT_KEY_ID, CARD_STAT_CARD_ID,
                        CARD_STAT_FLAGGED, CARD_STAT_CORRECT_COUNT, CARD_STAT_INCORRECT_COUNT, CARD_STAT_SKIP_COUNT}, CARD_KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        CardStatistic cardStatistic = null;
        if (cursor != null && cursor.moveToFirst()) {
            cardStatistic = new CardStatistic(Long.parseLong(cursor.getString(0)), Long.parseLong(cursor.getString(1)),
                    Boolean.valueOf(cursor.getString(2)), Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)), Long.parseLong(cursor.getString(5)));
        }
        return cardStatistic;
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
