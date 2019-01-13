package ca.csf.pobj.tp3.activity.Model;

//BEN_REVIEW : Je vois mal pourquoi vous avez séparé "CryptingCypher" et
//             "DecryptingCypher".
//BEN_CORRECTION : Duplication de code avec "DecryptingCypher".
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
