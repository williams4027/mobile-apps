package blake.com.flashtalk.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import blake.com.flashtalk.R;
import blake.com.flashtalk.dao.Card;
import blake.com.flashtalk.dao.CardStatistic;

public class StatisticsArrayAdapter extends ArrayAdapter<Card> {
    Context context;
    int layoutResourceId;
    List<Card> data = null;

    public StatisticsArrayAdapter(Context context, int layoutResourceId, List<Card> data) {
        super(context, layoutResourceId, android.R.id.text1, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StatHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StatHolder();
            holder.cardAnswer = (TextView)row.findViewById(R.id.statCardAnswer);
            holder.correctPercentage = (TextView)row.findViewById(R.id.statCardPercentage);

            row.setTag(holder);
        }
        else
        {
            holder = (StatHolder)row.getTag();
        }

        Card card = data.get(position);
        holder.cardAnswer.setText(card.getDisplayAnswer());

        CardStatistic cardStatistic = card.getCardStatistic();
        double correctPercentage = StatisticsArrayAdapter.calculateCardStatistics(cardStatistic);
        holder.correctPercentage.setText(String.format("%.2f", correctPercentage) + "%");

        if (correctPercentage < 70){
            Drawable background = this.context.getResources().getDrawable(R.drawable.flashtalk_elements_transparentbox);
            background.setBounds(row.getLeft(), row.getTop(), row.getRight(), row.getBottom());
            row.setBackgroundColor(this.context.getResources().getColor(R.color.translucent));
        }

        return row;
    }

    static class StatHolder
    {
        TextView cardAnswer;
        TextView correctPercentage;
    }

    public static double calculateCardStatistics(CardStatistic cardStatistic){
        if (cardStatistic != null && (cardStatistic.get_correctCount() + cardStatistic.get_incorrectCount() > 0)){
            return (cardStatistic.get_correctCount() * 1.0 / (cardStatistic.get_correctCount() + cardStatistic.get_incorrectCount())) * 100;
        }
        return 0;
    }
}