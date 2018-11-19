package utils;

import java.math.BigInteger;

public class Key {

    private BigInteger a,b;

    public Key(BigInteger a, BigInteger b){
        this.a = a;
        this.b = b;
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getB() {
        return b;
    }
}
