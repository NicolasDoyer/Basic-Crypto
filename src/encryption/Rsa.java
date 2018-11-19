package encryption;

import utils.Data;
import utils.Helper;
import utils.Key;

import java.math.BigInteger;
import java.util.Random;

/* REMARQUES:

    1) Pour de grand nombre, le calcul des puissance devient compliqué. -> Utiliser la puissance modulaire (FAIT)
    2) Un même caractère sera toujours chiffré de la même façon dans un texte -> Chiffrer par blocs

 */


public class Rsa {

    private static final int BIT_LENGT = 16;
    private static final int BLOCK_SIZE = 4;

    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger ind_euler;
    private Key publicKey, privateKey;

    public Rsa() throws Exception{
        Random random = new Random();
        BigInteger p = BigInteger.probablePrime(Rsa.BIT_LENGT,random);
        BigInteger q = BigInteger.probablePrime(Rsa.BIT_LENGT,random);
        if(!p.isProbablePrime(1) || !q.isProbablePrime(1)){
            throw new Exception("error");
        }
        else{
            this.p = p;
            this.q = q;
            this.n = p.multiply(q);
            this.ind_euler = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        }

        // Init public/private keys
        initialize();
    }

    public Rsa(BigInteger p, BigInteger q) throws Exception{

        if(!p.isProbablePrime(1) || !q.isProbablePrime(1)){
            throw new Exception("error");
        }
        else{
            this.p = p;
            this.q = q;
            this.n = p.multiply(q);
            this.ind_euler = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        }


        // Init public/private keys
        initialize();
    }

    public Key getPublicKey() {
        return this.publicKey;
    }

    private void initialize(){

        boolean findPremier = false;
        BigInteger maximumBetweenPQ = this.p.max(this.q);
        BigInteger e = BigInteger.valueOf(maximumBetweenPQ.longValue() + 1);
        BigInteger[] resultEuclide;

        while(!findPremier){
            e = e.add(BigInteger.ONE);
            resultEuclide = euclide(e,this.ind_euler);
            if(resultEuclide[0].equals(BigInteger.ONE)){
                findPremier = true;
            }
        }

        this.publicKey = new Key(this.n, e);
        this.privateKey = new Key(this.n, e.modInverse(this.ind_euler));
    }

    private BigInteger[] euclide(BigInteger a, BigInteger b){
        BigInteger r = a, u = BigInteger.ONE, v = BigInteger.ZERO;
        BigInteger rp = b, up = BigInteger.ZERO, vp = BigInteger.ONE;
        BigInteger rs, us, vs;

        while(!rp.equals(BigInteger.ZERO)){
            q  = r.divide(rp); rs = r; us = u; vs = v; r = rp; u = up; v = vp;
            rp = rs.subtract(q.multiply(rp)); up = us.subtract(q.multiply(up)); vp = vs.subtract(q.multiply(vp));
        }

        return new BigInteger[] {r, u, v};
    }

    public static String[] formatMessage(String message){
        String messageFormated = "";
        int charValue;
        for(int x = 0; x < message.length(); x++){
            charValue = (int)message.charAt(x);
            if(charValue < 100){
                messageFormated += "0" + charValue;
            }
            else{
                messageFormated += charValue;
            }
        }
        return Helper.splitStringEvery(messageFormated,Rsa.BLOCK_SIZE);
    }

    public static String unformatMessage(String message){
        String[] unformatedMessage = message.split(" ");
        String result = "";
        int u = 1;
        for(String messagePiece : unformatedMessage){
            if(messagePiece.length() < 4){
                for(int i = 0; i <= 4 - messagePiece.length(); i++)
                    if(u != unformatedMessage.length)
                    messagePiece = '0' + messagePiece;
            }
            result += messagePiece;
            u++;
        }
        return result;
    }

    private static BigInteger encrypt(BigInteger character, Key publicKey){
        return character.modPow(publicKey.getB(),publicKey.getA());
    }

    public static String encryptMessage(String message, Key publicKey){
        String encrypted = "";
        String[] formatedMessage = Rsa.formatMessage(message);
        BigInteger charValue;
        for(String formatedMessagePiece: formatedMessage){
            charValue = BigInteger.valueOf(Long.valueOf(formatedMessagePiece));
            encrypted += Rsa.encrypt(charValue,publicKey) + " ";
        }
        return encrypted;
    }

    private BigInteger decrypt(BigInteger encryptedChar){
        return encryptedChar.modPow(this.privateKey.getB(),this.privateKey.getA());
    }

    public static String decryptMessage(String message, Rsa encryptor){

        // Decrypt message using encryptor private key
        String decrypted = "";
        String[] splitedMessage = message.split(" ");
        BigInteger stringValue;
        for(String messagePiece: splitedMessage){
            stringValue = BigInteger.valueOf(Long.valueOf(messagePiece));
            decrypted += encryptor.decrypt(stringValue) + " ";
        }

        //Unformat message
        splitedMessage = Helper.splitStringEvery(Rsa.unformatMessage(decrypted),3);
        decrypted = "";
        for(String character: splitedMessage){
                decrypted += (char) Integer.parseInt(character);
        }

        return decrypted;
    }

    public static void main(String[] args){
        try{
            // Creating RSA instance
            Rsa encryptor = new Rsa();
            String message = "Testastos";

            // Encrypting message
            String messageEncrypted = Rsa.encryptMessage(message,encryptor.getPublicKey());
            System.out.println(messageEncrypted);

            // Decrypting message
            String messageDecrypted = Rsa.decryptMessage(messageEncrypted,encryptor);
            System.out.println(messageDecrypted);

        }catch(Exception e){
            System.err.println(e.toString());
        }
    }
}
