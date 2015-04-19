package blake.com.flashtalk.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Card implements Parcelable, Serializable {
    long _id;
    long _deckId;
    String _answerString;
    String _hintString;

    CardStatistic cardStatistic;

    private final int MAX_DISPLAY_CHAR = 20;

    public Card(){}

    public Card(long id, long deckId, String answerString, String hintString){
        this(deckId, answerString, hintString);
        getCardStatistic();
    }

    public Card(long deckId, String answerString, String hintString){
        this._deckId = deckId;
        this._answerString = answerString;
        this._hintString = hintString;
        getCardStatistic();
    }

    public Card(Parcel deckParcel){
        String[] parcelData= new String[4];
        deckParcel.readStringArray(parcelData);
        this._id= Long.parseLong(parcelData[0]);
        this._deckId= Long.parseLong(parcelData[1]);
        this._answerString = parcelData[2];
        this._hintString = parcelData[3];
        getCardStatistic();
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public long getDeckId() {
        return _deckId;
    }

    public void setDeckId(long _deckId) {
        this._deckId = _deckId;
    }

    public String getAnswerString() {
        return _answerString;
    }

    public void setAnswerString(String _answerString) {
        this._answerString = _answerString;
    }

    public String getHintString() {
        return _hintString;
    }

    public void setHintString(String _hintString) {
        this._hintString = _hintString;
    }

    public CardStatistic getCardStatistic() {
        if (cardStatistic == null){
            DatabaseHandler db = DatabaseHandler.getInstance(null);
            this.cardStatistic = db.getCardStatistic(this.getId());
            db.close();
            if (this.cardStatistic == null){
                this.cardStatistic = new CardStatistic(this.getId());
            }
        }
        return cardStatistic;
    }

    public void updateCardStatistic(){
        DatabaseHandler db = DatabaseHandler.getInstance(null);
        if (getCardStatistic().getId() == 0){
            db.addCardStatistic(this.cardStatistic);
        } else {
            db.updateCardStatistic(this.cardStatistic);
        }
        db.close();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{String.valueOf(this.getId()),String.valueOf(this.getDeckId()),this.getAnswerString(),this.getHintString()});
    }

    public static final Creator<Card> CREATOR= new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);  //using parcelable constructor
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public String getDisplayAnswer(){
        String displayAnswer = "";
        if (this.getAnswerString().length() > MAX_DISPLAY_CHAR){
            displayAnswer = this.getAnswerString().substring(0, 14) + "...";
        } else {
            displayAnswer = this.getAnswerString();
        }
        return displayAnswer;
    }

    @Override
    public String toString() {
        return getDisplayAnswer();
    }
}
