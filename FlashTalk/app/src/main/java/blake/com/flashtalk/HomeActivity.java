package blake.com.flashtalk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import blake.com.flashtalk.dao.DatabaseHandler;
import blake.com.flashtalk.dao.Deck;
import blake.com.flashtalk.data.LocaleSpinnerObject;


public class HomeActivity extends Activity {

    private final Context activityContext = this;

    private ImageButton btnAddDeck;

    private List<Deck> currentDecks;
    private ArrayAdapter<Deck> deckAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deck_list);

        updateDeckList();

        // Reading all contacts
        Log.d("DEBUG: HomeActivity", "Logging Decks..");
        for ( Deck tempDeck : currentDecks ){
            Log.d("Deck ID: ", String.valueOf(tempDeck.getId()));
            Log.d("Deck DeckName: ", tempDeck.getDeckName());
            Log.d("Deck AnswerLocale: ", tempDeck.getAnswerLocale());
            Log.d("Deck HintLocale: ", tempDeck.getHintLocale());
        }

        btnAddDeck = (ImageButton)findViewById(R.id.btnAddDeck);

        btnAddDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // custom dialog
                final Dialog dialog = new Dialog(activityContext);
                dialog.setContentView(R.layout.activity_add_deck);
                dialog.setTitle("Enter Deck Name");

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                final EditText txtNewDeckName = (EditText)dialog.findViewById(R.id.editTextDeckName);

                final Spinner spinnerAnswerLocale = (Spinner)dialog.findViewById(R.id.spinnerAnswerLocaleList);
                final Spinner spinnerHintLocale = (Spinner)dialog.findViewById(R.id.spinnerHintLocaleList);

                List<LocaleSpinnerObject> spinnerList = new ArrayList<LocaleSpinnerObject>();
                LocaleSpinnerObject defaultLocale = null;

                for ( Locale tempLocale : Locale.getAvailableLocales() ){
                    System.out.println(tempLocale.getDisplayLanguage());
                    System.out.println(tempLocale.getDisplayName());
                    System.out.println(tempLocale.getLanguage());
                    LocaleSpinnerObject tempObject = new LocaleSpinnerObject(tempLocale.getDisplayName(), tempLocale);
                    if ( tempLocale.equals(Locale.US)){
                        defaultLocale = tempObject;
                    }
                    spinnerList.add(tempObject);
                }

                ArrayAdapter<LocaleSpinnerObject> dataAdapter = new ArrayAdapter<LocaleSpinnerObject>(activityContext,
                        android.R.layout.simple_spinner_item, spinnerList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                int defaultIndex = dataAdapter.getPosition(defaultLocale);

                spinnerAnswerLocale.setAdapter(dataAdapter);
                spinnerAnswerLocale.setSelection(defaultIndex);

                spinnerHintLocale.setAdapter(dataAdapter);
                spinnerHintLocale.setSelection(defaultIndex);

                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();


                        /**
                         * CRUD Operations
                         * */
                        // Inserting Contacts
                        Log.d("Insert: ", "Inserting ..");
                        if ( ((LocaleSpinnerObject)spinnerAnswerLocale.getSelectedItem()).getValue() != null &&
                                ((LocaleSpinnerObject)spinnerHintLocale.getSelectedItem()).getValue() != null ) {
                            final Deck newDeck = new Deck(txtNewDeckName.getText().toString(),
                                    ((LocaleSpinnerObject) spinnerAnswerLocale.getSelectedItem()).getValue().toString(),
                                    ((LocaleSpinnerObject) spinnerHintLocale.getSelectedItem()).getValue().toString());
                            DatabaseHandler db = DatabaseHandler.getInstance(activityContext);
                            db.addDeck(newDeck);
                            currentDecks.add(newDeck);
                            deckAdapter.notifyDataSetChanged();
                            db.close();
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDeckList();
    }

    private void updateDeckList() {
        // Populate the displayed user decks
        DatabaseHandler db = DatabaseHandler.getInstance(activityContext);
        currentDecks = db.getAllDecks();
        db.close();
        Log.d("Deck Count: ", String.valueOf(currentDecks.size()));

        final ListView deckListView = (ListView)findViewById(R.id.deckList);
        deckAdapter = new ArrayAdapter<Deck>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, currentDecks);
        deckListView.setAdapter(deckAdapter);
        registerForContextMenu(deckListView);

        // Set click listener
        deckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent deckMainIntent = new Intent(HomeActivity.this, DeckMainActivity.class);

                // Get the deck selected and attach
                int itemPosition = position;
                Deck selectedDeck = (Deck) deckListView.getItemAtPosition(itemPosition);
                deckMainIntent.putExtra("SelectedDeck", selectedDeck);
                startActivity(deckMainIntent);
            }
        });

        deckAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.deckList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(currentDecks.get(info.position).getDeckName());
            String[] menuItems = getResources().getStringArray(R.array.deck_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex){
            case 0:
                Log.d("DEBUG","Edit Selected");
                break;
            case 1:
                Log.d("DEBUG","Delete Selected");
                // Get the deck selected and delete
                Deck selectedDeck = currentDecks.get(info.position);
                DatabaseHandler db = DatabaseHandler.getInstance(activityContext);
                db.deleteDeck(selectedDeck);
                currentDecks.remove(selectedDeck);
                deckAdapter.notifyDataSetChanged();
                db.close();
                break;
        }
        return true;
    }
}
