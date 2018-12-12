package ca.csf.pobj.tp3.activity.Model;

public class CryptingCypher {

    public String cryptString(String stringToCrypt, Cypher cypherKey) {
        StringBuilder stringBuilder = new StringBuilder();
        String letterToCrypt;
        int position;

        for (int i = 0; i < stringToCrypt.length(); i++) {
            letterToCrypt = String.valueOf(stringToCrypt.charAt(i));
            position = cypherKey.getInputCharacters().indexOf(letterToCrypt);
            stringBuilder.append(cypherKey.getOutputCharacters().charAt(position));
        }

        return stringBuilder.toString();
    }

}
