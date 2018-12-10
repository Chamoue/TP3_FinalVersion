package ca.csf.pobj.tp3.activity.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import ca.csf.pobj.tp3.activity.Task.CypheringTask;
import ca.csf.pobj.tp3.activity.Model.Listener;
import ca.csf.pobj.tp3.activity.Task.HttpCypherTask;
import ca.csf.pobj.tp3.utils.view.CharactersFilter;
import ca.csf.pobj.tp3.utils.view.KeyPickerDialog;

public class MainActivity extends AppCompatActivity implements Listener {

    private static final int KEY_LENGTH = 5;
    private static final int MAX_KEY_VALUE = (int) Math.pow(10, KEY_LENGTH) - 1;
    public static final String CURRENT_KEY = "currentKey";

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

        if (savedInstanceState == null) {
            Random random = new Random();
            this.currentKeyTextView.setText(Integer.toString(random.nextInt(10000)));
        }

        getCypherKey();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(CURRENT_KEY)) {
            this.currentKeyTextView.setText(savedInstanceState.getInt(CURRENT_KEY));
        }

        if (this.currentCypher == null) {
            getCypherKey();
        }
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
        this.currentKeyTextView.setText(Integer.toString(key));
    }

    private void getCypherKey() {
        HttpCypherTask httpCypherTask = new HttpCypherTask();
        httpCypherTask.addListener(this);
        httpCypherTask.execute(this.currentKeyTextView.getText().toString());
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
        CypheringTask cypherTask = new CypheringTask(getString(R.string.task_decrypt), getInputToCypher(), this.currentCypher);
        cypherTask.addListener(this);
        cypherTask.execute(this.currentKeyTextView.getText().toString());

    }

    public void onEncryptButtonClicked(View view) {
        CypheringTask cypherTask = new CypheringTask(getString(R.string.task_crypt), getInputToCypher(), this.currentCypher);
        cypherTask.addListener(this);
        cypherTask.execute(this.currentKeyTextView.getText().toString());
    }

    public String getInputToCypher() {
        return this.inputEditText.getText().toString();
    }


    @Override
    public void onCypherTaskEnded(Object cypherTaskResult) {
        if (cypherTaskResult.getClass().getName().equals(CypherRequestResult.class.getName())) {
            this.currentCypherRequest = (CypherRequestResult) cypherTaskResult;
            if (this.currentCypherRequest.isConnnectivityError()) {
                this.showConnectionError();
            } else if (this.currentCypherRequest.isServerError()) {
                this.showServerError();
            } else if (this.currentCypherRequest.getCypher() != null) {
                this.currentCypher = this.currentCypherRequest.getCypher();
            }

        } else if (cypherTaskResult.getClass().getName().equals(String.class.getName())) {
            this.outputTextView.setText((String)cypherTaskResult);
        }
    }
}
