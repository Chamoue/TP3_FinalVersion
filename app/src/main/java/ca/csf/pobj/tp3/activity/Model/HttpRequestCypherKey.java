package ca.csf.pobj.tp3.activity.Model;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequestCypherKey {

    public static final String URL = "https://m1t2.csfpwmjv.tk/api/key/%d";


    public CypherRequestResult getCypherKey(int keyToGet) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(URL, keyToGet))
                .build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {

                String responseBody = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                Cypher currentCypher = mapper.readValue(responseBody, Cypher.class);

                return CypherRequestResult.requestCompleted(currentCypher);
            } else {

                return CypherRequestResult.servorError();
            }

        } catch (IOException e) {
            return CypherRequestResult.connectivityError();
        }
    }
}
