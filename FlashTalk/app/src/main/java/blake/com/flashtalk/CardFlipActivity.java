/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package blake.com.flashtalk;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import blake.com.flashtalk.dao.Card;
import blake.com.flashtalk.dao.DatabaseHandler;
import blake.com.flashtalk.dao.Deck;

/**
 * Demonstrates a "card-flip" animation using custom fragment transactions ({@link
 * android.app.FragmentTransaction#setCustomAnimations(int, int)}).
 *
 * <p>This sample shows an "info" action bar button that shows the back of a "card", rotating the
 * front of the card out and the back of the card in. The reverse animation is played when the user
 * presses the system Back button or the "photo" action bar button.</p>
 */
public class CardFlipActivity extends Activity
        implements FragmentManager.OnBackStackChangedListener {
    /**
     * A handler object, used for deferring UI operations.
     */
    private Handler mHandler = new Handler();

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;

    Context activityContext = this;

    private List<Card> currentCards;
    private Card currentCard;

    private CardFrontFragment cardFrontFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        // Grab all the cards to test
        Deck selectedDeck = getIntent().getParcelableExtra("SelectedDeck");
        DatabaseHandler db = new DatabaseHandler(activityContext);
        db.getAllCards();
        currentCards = db.getAllDeckCards(selectedDeck.getId());
        db.close();

        // Set Progress Bar max value
        ProgressBar deckProgress = (ProgressBar)findViewById(R.id.deckProgressBar);
        deckProgress.setMax(currentCards.size());

        // Set the card currently being viewed
        if (changeCurrentCard(false)) {

            FrameLayout cardContainer = (FrameLayout) findViewById(R.id.cardContainer);
            cardContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flipCard();
                }
            });

            if (savedInstanceState == null) {
                cardFrontFragment = new CardFrontFragment();
                Bundle args = new Bundle();
                if (currentCard != null) {
                    args.putString("FrontHint", currentCard.getHintString());
                    cardFrontFragment.setArguments(args);
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.cardContainer, cardFrontFragment)
                            .commit();
                }
            } else {
                mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
            }
        }

        // Monitor back stack changes to ensure the action bar shows the appropriate
        // button (either "photo" or "info").
        getFragmentManager().addOnBackStackChangedListener(this);
    }

    private boolean changeCurrentCard(boolean correctAnswer){
        if ( currentCard != null && currentCards != null ){
            if (correctAnswer) {
                ProgressBar deckProgress = (ProgressBar) findViewById(R.id.deckProgressBar);
                deckProgress.incrementProgressBy(1);
                currentCards.remove(currentCard);
            }
            flipCard();
        }
        if ( currentCards != null && currentCards.size() > 0 ){
            Random randGen = new Random(System.currentTimeMillis());
            int cardIndex = randGen.nextInt(currentCards.size());
            currentCard = currentCards.get(cardIndex);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Add either a "photo" or "finish" button to the action bar, depending on which page
        // is currently selected.
        if (mShowingBack) {
            MenuItem correctItem = menu.add(Menu.NONE, R.id.action_correct, Menu.NONE, mShowingBack ? "Hint" : "Answer");
            correctItem.setIcon(R.drawable.checkmark_dw);
            correctItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            MenuItem wrongItem = menu.add(Menu.NONE, R.id.action_wrong, Menu.NONE, mShowingBack ? "Hint" : "Answer");
            wrongItem.setIcon(R.drawable.x_dw);
            wrongItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_correct:
                boolean cardsLeft = changeCurrentCard(true);
                if ( cardsLeft ) {
                    cardFrontFragment = new CardFrontFragment();
                    Bundle args = new Bundle();
                    args.putString("FrontHint", currentCard.getHintString());
                    cardFrontFragment.setArguments(args);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.cardContainer, cardFrontFragment)
                            .commit();
                    return true;
                } else {
                    Intent deckMainIntent = new Intent(CardFlipActivity.this, DeckMainActivity.class);

                    // Get the deck selected and attach
                    deckMainIntent.putExtra("SelectedDeck", getIntent().getParcelableExtra("SelectedDeck"));
                    startActivity(deckMainIntent);
                    Toast.makeText(getApplicationContext(), "Congrats! Deck finished!", Toast.LENGTH_LONG).show();
                }
            case R.id.action_wrong:
                cardsLeft = changeCurrentCard(false);
                if ( cardsLeft ) {
                    cardFrontFragment = new CardFrontFragment();
                    Bundle args = new Bundle();
                    args.putString("FrontHint", currentCard.getHintString());
                    cardFrontFragment.setArguments(args);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.cardContainer, cardFrontFragment)
                            .commit();
                    return true;
                } else {
                    Intent deckMainIntent = new Intent(CardFlipActivity.this, DeckMainActivity.class);

                    // Get the deck selected and attach
                    deckMainIntent.putExtra("SelectedDeck", getIntent().getParcelableExtra("SelectedDeck"));
                    startActivity(deckMainIntent);
                    Toast.makeText(getApplicationContext(), "Congrats! Deck finished!", Toast.LENGTH_LONG).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        CardBackFragment cardBackFragment = new CardBackFragment();
        Bundle args = new Bundle();
        args.putString("BackAnswer", currentCard.getAnswerString());
        cardBackFragment.setArguments(args);
        getFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.cardContainer, cardBackFragment)

                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)

                        // Commit the transaction.
                .commit();

        // Defer an invalidation of the options menu (on modern devices, the action bar). This
        // can't be done immediately because the transaction may not yet be committed. Commits
        // are asynchronous in that they are posted to the main thread's message loop.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        // When the back stack changes, invalidate the options menu (action bar).
        invalidateOptionsMenu();
    }

    /**
     * A fragment representing the front of the card.
     */
    public static class CardFrontFragment extends Fragment {

        public CardFrontFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_card_front, container, false);
            if ( getArguments().getString("FrontHint") != null ) {
                TextView hintText = (TextView)view.findViewById(R.id.txtFrontCardHint);
                hintText.setText(getArguments().getString("FrontHint"));
            }
            return view;
        }
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment {
        public CardBackFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_card_back, container, false);
            if (getArguments().getString("BackAnswer") != null) {
                TextView answerText = (TextView) view.findViewById(R.id.txtBackCardAnswer);
                answerText.setText(getArguments().getString("BackAnswer"));
            }
            return view;
        }
    }
}
