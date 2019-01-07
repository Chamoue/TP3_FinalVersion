package ca.csf.pobj.tp3.activity.Task;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ca.csf.pobj.tp3.activity.Model.CypherRequestResult;
import ca.csf.pobj.tp3.activity.Model.HttpRequestCypherKey;
import ca.csf.pobj.tp3.activity.Model.HttpRequestListener;

public class HttpCypherTask extends AsyncTask<String, Void, CypherRequestResult> {

    private final List<HttpRequestListener> listeners = new ArrayList<>();

    @Override
    protected CypherRequestResult doInBackground(String... key) {
        HttpRequestCypherKey keyGetter = new HttpRequestCypherKey();
        return keyGetter.getCypherKey(Integer.parseInt(key[0]));
    }

    @Override
    protected void onPostExecute(CypherRequestResult cypherRequestResult) {
        super.onPostExecute(cypherRequestResult);

        for (HttpRequestListener listener : listeners) {
            listener.onHttpRequestDone(cypherRequestResult);
        }
    }

    public void addListener(HttpRequestListener listener) {
        this.listeners.add(listener);
    }
}
