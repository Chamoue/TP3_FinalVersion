package ca.csf.pobj.tp3.activity.Model;

public class Cypher {
    private String id;
    private String outputCharacters;
    private String inputCharacters;
    private String cypherResult;

    public void setId(String id) {
        this.id = id;
    }

    public void setOutputCharacters(String outputCharacters) {
        this.outputCharacters = outputCharacters;
    }

    public void setInputCharacters(String inputCharacters) {
        this.inputCharacters = inputCharacters;
    }

    public String getId() {
        return id;
    }

    public String getOutputCharacters() {
        return outputCharacters;
    }

    public String getInputCharacters() {
        return inputCharacters;
    }

    public String getCypherResult() {
        return cypherResult;
    }

    public void setCypherResult(String cypherResult) {
        this.cypherResult = cypherResult;
    }
}
