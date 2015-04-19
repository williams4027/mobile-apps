package blake.com.flashtalk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import blake.com.flashtalk.dao.DatabaseHandler;
import blake.com.flashtalk.dao.Deck;
import blake.com.flashtalk.data.LocaleSpinnerObject;


public class EditDeckActivity extends Activity {

    private final Context activityContext = this;

    private Deck selectedDeck;

    private AutoCompleteTextView spinnerHintLocale;
    private AutoCompleteTextView spinnerAnswerLocale;

    private ImageButton btnSaveDeck;
    private ImageButton btnDeleteDeck;
    private EditText txtNewDeckName;

    private LocaleSpinnerObject selectedAnswerLocale;
    private LocaleSpinnerObject selectedHintLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);
        selectedDeck = getIntent().getParcelableExtra("SelectedDeck");
        if (selectedDeck == null){
            selectedDeck = new Deck();
        }

        txtNewDeckName = (EditText)findViewById(R.id.editTextDeckName);

        spinnerAnswerLocale = (AutoCompleteTextView)findViewById(R.id.spinnerAnswerLocaleList);
        spinnerHintLocale = (AutoCompleteTextView)findViewById(R.id.spinnerHintLocaleList);

        List<LocaleSpinnerObject> spinnerList = new ArrayList<LocaleSpinnerObject>();
        LocaleSpinnerObject defaultLocale = null;

        LocaleSpinnerObject defaultAnswerLocale =null;
        LocaleSpinnerObject defaultHintLocale = null;
        for ( Locale tempLocale : Locale.getAvailableLocales() ){
            LocaleSpinnerObject tempObject = new LocaleSpinnerObject(tempLocale.getDisplayName(), tempLocale);
            if (tempLocale.equals(Locale.US)){
                defaultLocale = tempObject;
            }
            if (tempLocale.equals(selectedDeck.getAnswerLocale())){
                defaultAnswerLocale = tempObject;
            }
            if (tempLocale.equals(selectedDeck.getHintLocale())){
                defaultHintLocale = tempObject;
            }
            spinnerList.add(tempObject);
        }

        final ArrayAdapter<LocaleSpinnerObject> answerAdapter = new ArrayAdapter<LocaleSpinnerObject>(activityContext,
                android.R.layout.simple_dropdown_item_1line, spinnerList);
        final ArrayAdapter<LocaleSpinnerObject> hintAdapter = new ArrayAdapter<LocaleSpinnerObject>(activityContext,
                android.R.layout.simple_dropdown_item_1line, spinnerList);

        spinnerAnswerLocale.setThreshold(1);
        spinnerAnswerLocale.setDropDownAnchor(R.id.spinnerAnswerLocaleList);
        spinnerAnswerLocale.setAdapter(answerAdapter);
        spinnerAnswerLocale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedAnswerLocale = answerAdapter.getItem(position);
            }
        });
        spinnerHintLocale.setThreshold(1);
        spinnerHintLocale.setAdapter(hintAdapter);
        spinnerHintLocale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedHintLocale = hintAdapter.getItem(position);
            }
        });

        int defaultIndex = hintAdapter.getPosition(defaultLocale);

        btnSaveDeck = (ImageButton)findViewById(R.id.btnSaveDeck);
        btnSaveDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Insert: ", "Inserting ..");
                if ( selectedAnswerLocale != null &&
                        selectedHintLocale != null ) {

                    // Persist the new deck to the database
                    final Deck newDeck = new Deck(txtNewDeckName.getText().toString(),
                            selectedAnswerLocale.getValue().toString(),
                            selectedHintLocale.getValue().toString());
                    DatabaseHandler db = DatabaseHandler.getInstance(activityContext);
                    db.addDeck(newDeck);
                    db.close();
                    finish();
                } else {
                    Toast.makeText(activityContext, "Please enter a valid locale for both answers and hints.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.d("LIFECYCLE DEBUG:", "Edit Deck Activity: In On Create");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LIFECYCLE DEBUG:", "Edit Deck Activity: In On Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LIFECYCLE DEBUG:", "Edit Deck Activity: In On Pause");
    }
}
