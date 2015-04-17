package blake.com.flashtalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender.SendIntentException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import blake.com.flashtalk.dao.Card;
import blake.com.flashtalk.dao.Deck;

public class ExportDeckActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLUTION = 3;

    private GoogleApiClient mGoogleApiClient;
    private Deck selectedDeck;
    private List<String>orderedCards = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_deck);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        selectedDeck = getIntent().getParcelableExtra("SelectedDeck");
        List<Card> allHints = selectedDeck.getCards();
        for (Card card : allHints) {
            orderedCards.add(card.getHintString() + ", " + card.getAnswerString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveContentsResult>() {

                    @Override
                    public void onResult(DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(ExportDeckActivity.this);
                            alert.setTitle("Connection Issues!");
                            alert.setMessage("The application was unable to reach Google Drive. Check connection and try again.");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    finish();
                                }
                            });
                            alert.show();
                            return;
                        }
                        final DriveContents driveContents = result.getDriveContents();

                        // Perform I/O off the UI thread.
                        new Thread() {
                            @Override
                            public void run() {
                                // write content to DriveContents
                                OutputStream outputStream = driveContents.getOutputStream();
                                Writer writer = new OutputStreamWriter(outputStream);
                                try
                                {
                                    for (String line : orderedCards) {
                                        writer.write(line);
                                        writer.write("\n");
                                    }
                                    writer.close();
                                } catch (IOException e) {
                                    Log.e("Error:", e.getMessage());
                                }

                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(ExportDeckActivity.this.selectedDeck.getDeckName() + ".csv")
                                        .setMimeType("text/csv")
                                        .setStarred(true).build();

                                // create a file on root folder
                                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                        .createFile(mGoogleApiClient, changeSet, driveContents);
                            }
                        }.start();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.e("Error: ", "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e("Error: ", "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("Info: ", "API client connected.");
        saveFileToDrive();
        if (isNetworkAvailable()){
            AlertDialog.Builder alert = new AlertDialog.Builder(ExportDeckActivity.this);
            alert.setTitle("Complete!");
            alert.setMessage("The file has been successfully uploaded to Google Drive");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    finish();
                }
            });
            alert.show();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(ExportDeckActivity.this);
            alert.setTitle("Connection Issues!");
            alert.setMessage("The application was unable to reach Google Drive. File will be uploaded when the connection is restored.");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    finish();
                }
            });
            alert.show();

        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("Info: ", "GoogleApiClient connection suspended");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}