package blake.com.flashtalk.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Blake on 8/25/2014.
 */
public class Deck implements Parcelable {
    long _id;
    String _deckName;
    String _answerLocale;
    String _hintLocale;

    public Deck(){}

    public Deck(long id, String deckName, String answerLocale, String hintLocale){
        this._id = id;
        this._deckName = deckName;
        this._answerLocale = answerLocale;
        this._hintLocale = hintLocale;
    }

    public Deck(String deckName, String answerLocale, String hintLocale){
        this._deckName = deckName;
        this._answerLocale = answerLocale;
        this._hintLocale = hintLocale;
    }

    public Deck(Parcel deckParcel){
        String[] parcelData= new String[4];
        deckParcel.readStringArray(parcelData);
        this._id= Long.parseLong(parcelData[0]);
        this._deckName= parcelData[1];
        this._answerLocale = parcelData[2];
        this._hintLocale = parcelData[3];
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public String getDeckName() {
        return _deckName;
    }

    public void setDeckName(String _deckName) {
        this._deckName = _deckName;
    }

    public String getAnswerLocale() {
        return _answerLocale;
    }

    public void setAnswerLocale(String _answerLocale) {
        this._answerLocale = _answerLocale;
    }

    public String getHintLocale() {
        return _hintLocale;
    }

    public void setHintLocale(String _hintLocale) {
        this._hintLocale = _hintLocale;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{String.valueOf(this.getId()),this.getDeckName(),this.getAnswerLocale(),this.getHintLocale()});
    }

    public static final Parcelable.Creator<Deck> CREATOR= new Parcelable.Creator<Deck>() {
        @Override
        public Deck createFromParcel(Parcel source) {
            return new Deck(source);  //using parcelable constructor
        }

        @Override
        public Deck[] newArray(int size) {
            return new Deck[size];
        }
    };

    @Override
    public String toString() {
        return this._deckName;
    }
}
