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
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import blake.com.flashtalk.dao.Card;
import blake.com.flashtalk.dao.DatabaseHandler;
import blake.com.flashtalk.dao.Deck;

/**
 * Demonstrates a "card-flip" animation using custom fragment transactions when the user
 * taps the card to see the opposite side (hint/answer). Then the user can mark the card as
 * correctly identified or not, which will be logged in the card's stats. A new card will then
 * be populated until the whole deck has been finished.
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

    private ArrayList<Card> currentCards;
    private Card currentCard;

    private CardFrontFragment cardFrontFragment;
    private ImageButton correctButton;
    private ImageButton incorrectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        // Create new deck starting position
        if (savedInstanceState == null) {
            // Grab all the cards to test
            Deck selectedDeck = getIntent().getParcelableExtra("SelectedDeck");
            DatabaseHandler db = DatabaseHandler.getInstance(activityContext);
            db.getAllCards();
            currentCards = (ArrayList<Card>) db.getAllDeckCards(selectedDeck.getId());
            db.close();
        }
        // Load past status (screen rotation/app pause)
        else {
            currentCard = (Card) savedInstanceState.getSerializable("currentCard");
            currentCards = savedInstanceState.getParcelableArrayList("currentCards");
        }

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

                // Next card is not null, so set the front card fragment to the hint
                if (currentCard != null) {
                    args.putString("FrontHint", currentCard.getHintString());
                    cardFrontFragment.setArguments(args);
                    getFragmentManager().popBackStack();
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.cardContainer, cardFrontFragment,"frontCardFragment")
                            .commit();
                }
            } else {
                mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
            }
        }
        
        // Create the correct button
        correctButton = (ImageButton) findViewById(R.id.correctAnswerButton);
        correctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long correctCount = currentCard.getCardStatistic().get_correctCount();
                currentCard.getCardStatistic().set_correctCount(correctCount + 1);
                currentCard.updateCardStatistic();
                if ( changeCurrentCard(true) ) {
                    getFragmentManager().popBackStack();
                    cardFrontFragment = new CardFrontFragment();
                    Bundle args = new Bundle();
                    args.putString("FrontHint", currentCard.getHintString());
                    cardFrontFragment.setArguments(args);
                    getFragmentManager().popBackStack();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.cardContainer, cardFrontFragment, "frontCardFragment")
                            .commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Congrats! Deck finished!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        // Create the incorrect button
        incorrectButton = (ImageButton) findViewById(R.id.incorrectAnswerButton);
        incorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long incorrectCount = currentCard.getCardStatistic().get_incorrectCount();
                currentCard.getCardStatistic().set_incorrectCount(incorrectCount + 1);
                currentCard.updateCardStatistic();
                if ( changeCurrentCard(false) ) {
                    cardFrontFragment = new CardFrontFragment();
                    Bundle args = new Bundle();
                    args.putString("FrontHint", currentCard.getHintString());
                    cardFrontFragment.setArguments(args);
                    getFragmentManager().popBackStack();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.cardContainer, cardFrontFragment, "frontCardFragment")
                            .commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Congrats! Deck finished!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        
        getFragmentManager().addOnBackStackChangedListener(this);
    }

    private boolean changeCurrentCard(boolean correctAnswer){

        // Increase progress bar value, and remove the card from the temp deck
        if (correctAnswer) {
            ProgressBar deckProgress = (ProgressBar) findViewById(R.id.deckProgressBar);
            deckProgress.incrementProgressBy(1);
            currentCards.remove(currentCard);
        }
        // If there are more cards to go through, randomly pick one
        if ( currentCards != null && currentCards.size() > 0 ){
            Random randGen = new Random(System.currentTimeMillis());
            int cardIndex = randGen.nextInt(currentCards.size());
            currentCard = currentCards.get(cardIndex);
            return true;
        }

        // No more cards remain, so return false
        return false;
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
                .replace(R.id.cardContainer, cardBackFragment, "backCardFragment")

                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)

                        // Commit the transaction.
                .commit();
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Take the current variables and save them to the bundle
        state.putSerializable("currentCard", currentCard);
        state.putParcelableArrayList("currentCards", currentCards);
    }

    /**
     * A fragment representing the front of the card.
     */
    public static class CardFrontFragment extends Fragment {

        public CardFrontFragment(){
            setRetainInstance(true);
        }

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
        public CardBackFragment() {
            setRetainInstance(true);
        }

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
