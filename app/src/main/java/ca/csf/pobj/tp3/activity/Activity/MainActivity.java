package ca.csf.pobj.tp3.activity.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

import ca.csf.pobj.tp3.R;
import ca.csf.pobj.tp3.activity.Model.Cypher;
import ca.csf.pobj.tp3.activity.Model.CypherRequestResult;
import ca.csf.pobj.tp3.activity.Model.CypherTaskListener;
import ca.csf.pobj.tp3.activity.Task.CypheringTask;
import ca.csf.pobj.tp3.activity.Model.HttpRequestListener;
import ca.csf.pobj.tp3.activity.Task.HttpCypherTask;
import ca.csf.pobj.tp3.utils.view.CharactersFilter;
import ca.csf.pobj.tp3.utils.view.KeyPickerDialog;

public class MainActivity extends AppCompatActivity implements HttpRequestListener, CypherTaskListener {

    private static final int KEY_LENGTH = 5;
    private static final String ID = "id";
    private static final String OUTPUT_CHARACTERS = "outputCharacters";
    private static final String INPUT_CHARACTERS = "inputCharacters";
    private static final String INPUT_TEXT = "inputText";
    private static final String OUTPUT_TEXT = "outputText";
    private static final String KEY_TEXT = "keyText";

    private View rootView;
    private EditText inputEditText;
    private TextView outputTextView;
    private TextView currentKeyTextView;
    private ProgressBar progressBar;
    private CypherRequestResult currentCypherRequest;
    private Cypher currentCypher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.rootView);
        progressBar = findViewById(R.id.progressbar);
        inputEditText = findViewById(R.id.input_edittext);
        inputEditText.setFilters(new InputFilter[]{new CharactersFilter()});
        outputTextView = findViewById(R.id.output_textview);
        currentKeyTextView = findViewById(R.id.current_key_textview);

        setDefaultRandomKey(savedInstanceState);
        getDefaultCypherKey(savedInstanceState);
    }

    private void getDefaultCypherKey(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            if (savedInstanceState.getString(ID).length() > 0
                    && savedInstanceState.getString(OUTPUT_CHARACTERS).length() > 0
                    && savedInstanceState.getString(INPUT_CHARACTERS).length() > 0) {

                getOldCypherKey(savedInstanceState);

            } else {
                getNewCypherKey();
            }

        } else {
            getNewCypherKey();
        }
    }

    private void getNewCypherKey() {
        startProgressBar();
        HttpCypherTask httpCypherTask = new HttpCypherTask();
        httpCypherTask.addListener(this);
        httpCypherTask.execute(this.currentKeyTextView.getText().toString());
    }

    private void getOldCypherKey(Bundle savedInstanceState) {
        this.currentCypher = new Cypher();
        this.currentCypher.setId(savedInstanceState.getString(ID));
        this.currentCypher.setOutputCharacters(savedInstanceState.getString(OUTPUT_CHARACTERS));
        this.currentCypher.setInputCharacters(savedInstanceState.getString(INPUT_CHARACTERS));
    }

    private void setDefaultRandomKey(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Random random = new Random();
            this.currentKeyTextView.setText(Integer.toString(random.nextInt(10000)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveData(outState);
    }

    private void saveData(Bundle outState) {
        outState.putString(ID, this.currentCypher.getId());
        outState.putString(OUTPUT_CHARACTERS, this.currentCypher.getOutputCharacters());
        outState.putString(INPUT_CHARACTERS, this.currentCypher.getInputCharacters());
        outState.putString(INPUT_TEXT, this.inputEditText.getText().toString());
        outState.putString(OUTPUT_TEXT, this.outputTextView.getText().toString());
        outState.putString(KEY_TEXT, this.currentKeyTextView.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreData(savedInstanceState);
    }

    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            getDefaultCypherKey(savedInstanceState);
            restoreTextViewData(savedInstanceState);
        }
    }

    private void restoreTextViewData(Bundle savedInstanceState) {
        this.inputEditText.setText(savedInstanceState.getString(INPUT_TEXT));
        this.outputTextView.setText(savedInstanceState.getString(OUTPUT_TEXT));
        this.currentKeyTextView.setText(savedInstanceState.getString(KEY_TEXT));
    }

    private void showKeyPickerDialog(int key) {
        KeyPickerDialog.make(this, KEY_LENGTH)
                .setKey(key)
                .setConfirmAction(this::fetchSubstitutionCypherKey)
                .show();
    }

    private void showCopiedToClipboardMessage() {
        Snackbar.make(rootView, R.string.text_copied_output, Snackbar.LENGTH_SHORT).show();
    }

    private void showConnectionError() {
        Snackbar.make(rootView, R.string.text_connectivity_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.text_activate_wifi, (view) -> showWifiSettings())
                .show();
    }

    private void showServerError() {
        Snackbar.make(rootView, R.string.text_server_error, Snackbar.LENGTH_INDEFINITE)
                .show();
    }

    private void showWifiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    private void fetchSubstitutionCypherKey(int key) {
        startProgressBar();
        this.currentKeyTextView.setText(Integer.toString(key));
        getNewCypherKey();
    }

    @SuppressWarnings("ConstantConditions")
    private void putTextInClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(getResources().getString(R.string.clipboard_encrypted_text), text));
    }

    public void onKeySelectButtonClicked(View view) {
        showKeyPickerDialog(Integer.parseInt(this.currentKeyTextView.getText().toString()));
    }

    public void onCopyButtonClicked(View view) {
        if (!(this.outputTextView.getText().toString().isEmpty())) {
            putTextInClipboard(this.outputTextView.getText().toString());
            showCopiedToClipboardMessage();
        } else {
            Snackbar.make(rootView, R.string.nothing_to_copy, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void onDecryptButtonClicked(View view) {
        startProgressBar();
        CypheringTask cypherTask = new CypheringTask(false, getInputToCypher(), this.currentCypher);
        cypherTask.addListener(this);
        cypherTask.execute(this.currentKeyTextView.getText().toString());

    }

    public void onEncryptButtonClicked(View view) {
        startProgressBar();
        CypheringTask cypherTask = new CypheringTask(true, getInputToCypher(), this.currentCypher);
        cypherTask.addListener(this);
        cypherTask.execute(this.currentKeyTextView.getText().toString());
    }

    public String getInputToCypher() {

        return this.inputEditText.getText().toString();
    }


    private void getCypheringTaskData(String cypherTaskResult) {
        this.outputTextView.setText(cypherTaskResult);
    }

    private void getHttpTaskData(CypherRequestResult cypherTaskResult) {
        this.currentCypherRequest = cypherTaskResult;
        if (this.currentCypherRequest.isConnnectivityError()) {
            this.showConnectionError();
        } else if (this.currentCypherRequest.isServerError()) {
            this.showServerError();
        } else if (this.currentCypherRequest.getCypher() != null) {
            this.currentCypher = new Cypher();
            this.currentCypher = this.currentCypherRequest.getCypher();
        }
    }

    private void startProgressBar() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    private void stopProgressBar() {
        SystemClock.sleep(500);
        this.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onHttpRequestDone(CypherRequestResult cypherRequestResult) {
        getHttpTaskData(cypherRequestResult);
        stopProgressBar();
    }

    @Override
    public void onCypherTaskDone(String result) {
        getCypheringTaskData(result);
        stopProgressBar();
    }
}
