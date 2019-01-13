package ca.csf.pobj.tp3.activity.Model;

public final class CypherRequestResult<T> {

    private Cypher cypher;
    private boolean isServerError;
    //BEN_REVIEW : Typo : isConnnectivityError => isConnectivityError
    private boolean isConnnectivityError;

    private CypherRequestResult(Cypher cypher, boolean isServerError, boolean isConnnectivityError) {
        this.cypher = cypher;
        this.isServerError = isServerError;
        this.isConnnectivityError = isConnnectivityError;
    }

    public static <T> CypherRequestResult<T> requestCompleted(Cypher cypher) {
        return new CypherRequestResult<>(cypher, false, false);
    }

    //BEN_REVIEW : Typo : servorError => serverError
    public static <T> CypherRequestResult<T> servorError() {
        return new CypherRequestResult<>(null, true, false);
    }

    public static <T> CypherRequestResult<T> connectivityError() {
        return new CypherRequestResult<>(null, false, true);
    }

    public Cypher getCypher() {
        return cypher;
    }

    public boolean isServerError() {
        return isServerError;
    }

    public boolean isConnnectivityError() {
        return isConnnectivityError;
    }
}
