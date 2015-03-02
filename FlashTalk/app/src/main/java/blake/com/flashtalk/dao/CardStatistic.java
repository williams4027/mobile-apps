package blake.com.flashtalk.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class CardStatistic implements Parcelable {
    long _id;
    long _cardId;
    boolean _isFlagged = false;
    long _correctCount = 0;
    long _incorrectCount = 0;
    long _skipCount = 0;

    public CardStatistic(){}

    public CardStatistic(long id, long cardId, boolean isFlagged, long correctCount, long incorrectCount, long skipCount){
        this._id = id;
        this._cardId = cardId;
        this._isFlagged = isFlagged;
        this._correctCount = correctCount;
        this._incorrectCount = incorrectCount;
        this._skipCount = skipCount;
    }

    public CardStatistic(long id, long cardId){
        this(cardId);
        this._cardId = cardId;
    }

    public CardStatistic(long cardId){
        this._cardId = cardId;
    }

    public CardStatistic(Parcel statisticParcel){
        String[] parcelData= new String[6];
        statisticParcel.readStringArray(parcelData);
        this._id= Long.parseLong(parcelData[0]);
        this._cardId= Long.parseLong(parcelData[1]);
        this._isFlagged = Boolean.parseBoolean(parcelData[2]);
        this._correctCount= Long.parseLong(parcelData[3]);
        this._incorrectCount= Long.parseLong(parcelData[4]);
        this._skipCount= Long.parseLong(parcelData[5]);
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public long get_cardId() {
        return _cardId;
    }

    public void set_cardId(long _cardId) {
        this._cardId = _cardId;
    }

    public boolean is_isFlagged() {
        return _isFlagged;
    }

    public void set_isFlagged(boolean _isFlagged) {
        this._isFlagged = _isFlagged;
    }

    public long get_skipCount() {
        return _skipCount;
    }

    public void set_skipCount(long _skipCount) {
        this._skipCount = _skipCount;
    }

    public long get_incorrectCount() {
        return _incorrectCount;
    }

    public void set_incorrectCount(long _incorrectCount) {
        this._incorrectCount = _incorrectCount;
    }

    public long get_correctCount() {
        return _correctCount;
    }

    public void set_correctCount(long _correctCount) {
        this._correctCount = _correctCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{String.valueOf(this.getId()),
                String.valueOf(this.is_isFlagged()),
                String.valueOf(this.get_correctCount()),
                String.valueOf(this.get_incorrectCount()),
                String.valueOf(this.get_skipCount())});
    }

    public static final Creator<CardStatistic> CREATOR= new Creator<CardStatistic>() {
        @Override
        public CardStatistic createFromParcel(Parcel source) {
            return new CardStatistic(source);  //using parcelable constructor
        }

        @Override
        public CardStatistic[] newArray(int size) {
            return new CardStatistic[size];
        }
    };
}
