package ca.csf.pobj.tp3.activity.Model;

public class DecryptingCypher {
    public String decryptString(String stringToDecrypt, Cypher cypherKey) {
        StringBuilder stringBuilder = new StringBuilder();
        String letterToDecrypt;
        int position;

        for (int i = 0; i < stringToDecrypt.length(); i++) {
            letterToDecrypt = String.valueOf(stringToDecrypt.charAt(i));
            position = cypherKey.getOutputCharacters().indexOf(letterToDecrypt);
            stringBuilder.append(cypherKey.getInputCharacters().charAt(position));
        }

        return stringBuilder.toString();
    }
}
