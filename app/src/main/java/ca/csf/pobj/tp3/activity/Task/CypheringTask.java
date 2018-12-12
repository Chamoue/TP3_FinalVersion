package ca.csf.pobj.tp3.activity.Task;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ca.csf.pobj.tp3.activity.Model.CryptingCypher;
import ca.csf.pobj.tp3.activity.Model.Cypher;
import ca.csf.pobj.tp3.activity.Model.DecryptingCypher;
import ca.csf.pobj.tp3.activity.Model.Listener;

public class CypheringTask extends AsyncTask<String, Void, String> {

    private final List<Listener> listeners = new ArrayList<>();
    private final Boolean taskToDo;
    private final String stringToCypher;
    private Cypher currentCypherKey = new Cypher();
    private String resultString;

    public CypheringTask(Boolean isTaskCrypting, String stringToCypher, Cypher key) {
        this.taskToDo = isTaskCrypting;
        this.stringToCypher = stringToCypher;
        this.currentCypherKey = key;
    }

    @Override
    protected String doInBackground(String... key) {
        if (this.taskToDo) {
            CryptingCypher cryptingCypher = new CryptingCypher();
            this.resultString = cryptingCypher.cryptString(this.stringToCypher, this.currentCypherKey);
        } else {
            DecryptingCypher decryptingCypher = new DecryptingCypher();
            this.resultString = decryptingCypher.decryptString(this.stringToCypher, this.currentCypherKey);
        }
        return this.resultString;
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);

        for (Listener listener : listeners) {
            listener.onCypherTaskEnded(s);
        }
    }

    public void addListener(Listener listener) {

        this.listeners.add(listener);
    }
}
