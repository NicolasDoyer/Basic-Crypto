package encryption;

public class Sdes {

    private static final int IC[] = {1,1,1,1,0,1,1,0,0,1};
    private static final int s0Matrice[] = {1,0,3,2,3,2,1,0,0,2,1,3,3,1,3,2};
    private static final int s1Matrice[] = {0,1,2,3,2,0,1,3,3,0,1,0,2,1,0,3};
    private static final int sboxSize = 4;
    private int[] firstKey;
    private int[] secondKey;

    public Sdes(){
        this.firstKey = new int[8];
        this.secondKey = new int[8];
    }

   public char run(char toEncrypt){
        generateKeys(Sdes.IC);

        String binaryString = Integer.toBinaryString( (int) toEncrypt );
        while(binaryString.length() < 8){
            binaryString = "0" + binaryString;
        }

        int[] binaryTab = {
                Character.getNumericValue(binaryString.charAt(0)),
                Character.getNumericValue(binaryString.charAt(1)),
                Character.getNumericValue(binaryString.charAt(2)),
                Character.getNumericValue(binaryString.charAt(3)),
                Character.getNumericValue(binaryString.charAt(4)),
                Character.getNumericValue(binaryString.charAt(5)),
                Character.getNumericValue(binaryString.charAt(6)),
                Character.getNumericValue(binaryString.charAt(7))
        };

        binaryTab = ip(binaryTab,false);
        binaryTab = fk(new int[] {binaryTab[0],binaryTab[1],binaryTab[2],binaryTab[3]}, new int[] {binaryTab[4],binaryTab[5],binaryTab[6],binaryTab[7]}, this.firstKey);
        binaryTab = sw(binaryTab);

        binaryTab = fk(new int[] {binaryTab[0],binaryTab[1],binaryTab[2],binaryTab[3]}, new int[] {binaryTab[4],binaryTab[5],binaryTab[6],binaryTab[7]}, this.secondKey);
        binaryTab = ip(binaryTab, true);


        String result = "";
        for(int i = 0; i < binaryString.length(); i++){
            result += binaryTab[i];
        }


        return (char) Integer.parseInt(result,2);
    }

    private void generateKeys(int[] key){
        int[] firstLs = new int[5], secondLs = new int[5];
        int[] resultP10 = p10(key);

        System.arraycopy(resultP10,0,firstLs,0,firstLs.length);
        System.arraycopy(resultP10,firstLs.length,secondLs,0,secondLs.length);

        firstLs = ls(firstLs, 1);
        secondLs = ls(secondLs, 1);
        this.firstKey = p8(firstLs, secondLs);

        firstLs = ls(firstLs, 2);
        secondLs = ls(secondLs, 2);
        this.secondKey = p8(firstLs, secondLs);
    }

    private int[] ls(int[] ls, int decrement){
        return new int[] {ls[Math.floorMod(decrement, 5)],ls[Math.floorMod(1 + decrement, 5)],ls[Math.floorMod(2 + decrement, 5)],ls[Math.floorMod(3 + decrement, 5)],ls[Math.floorMod(4 + decrement, 5)]};
    }

    private int[] p10(int[] key){
        if(key.length != 10){
            // TODO: Throw exception here
            return new int[] {};
        }
        return new int[] {key[2],key[4],key[1],key[6],key[3],key[9],key[0],key[8],key[7],key[5]};
    }

    private int[] p8(int[] firsPart, int[] secondPart){
        return new int[] {secondPart[0],firsPart[2],secondPart[1],firsPart[3],secondPart[2],firsPart[4],secondPart[4],secondPart[3]};
    }

    private int[] ip(int[] character, boolean reverse){
        if(character.length != 8){
            // TODO: Throw exception here
            return new int[] {};
        }
        if(!reverse)
            return new int[] {character[1],character[5],character[2],character[0],character[3],character[7],character[4],character[6]};
        else
            return new int[] {character[3],character[0],character[2],character[4],character[6],character[1],character[7],character[5]};
    }

    private int[] fk(int[] g, int[] d, int[] key){
        if(g.length != 4 || d.length != 4) {
            // TODO: Throw exception here
            return new int[]{};
        }

        int[] fResult = f(d, key);
        if(fResult.length != 4){
            // TODO: Throw exception here
            return new int[]{};
        }

        for(int i = 0; i < g.length; i++){
            g[i] = g[i] ^ fResult[i];
        }
        return new int[] {g[0],g[1],g[2],g[3],d[0],d[1],d[2],d[3]};
    }

    private int[] f(int[] fourBits, int[] key){
        if(fourBits.length != 4){
            // TODO: Throw exception here
            return new int[] {};
        }

        int[] resultEp = ep(fourBits);
        if(resultEp.length != 8){
            // TODO: Throw exception here
            return new int[] {};
        }

        int[] result = new int[8];
        for(int i = 0; i < resultEp.length; i++){
            result[i] = resultEp[i] ^ key[i];
        }

        int[] resultS0 = s(new int[] {result[0],result[1],result[2],result[3]},s0Matrice);
        int[] resultS1 = s(new int[] {result[4],result[5],result[6],result[7]},s1Matrice);

        return p4(resultS0, resultS1);
    }

    private int[] ep(int[] fourBits){
        if(fourBits.length != 4){
            // TODO: Throw exception here
            return new int[] {};
        }
        return new int[] {fourBits[3],fourBits[0],fourBits[1],fourBits[2],fourBits[1],fourBits[2],fourBits[3],fourBits[0]};
    }


    private int[] s(int[] fourBits, int[] sbox){
        if(fourBits.length != 4){
            // TODO: Throw exception here
            return new int[] {};
        }
        int row = Integer.parseInt(String.valueOf(fourBits[0]) + String.valueOf(fourBits[3]),2);
        int column = Integer.parseInt(String.valueOf(fourBits[1]) + String.valueOf(fourBits[2]),2);

        String toReturn = Integer.toBinaryString(sbox[ (row * sboxSize) + column ]);
        while(toReturn.length() < 2){
            toReturn = "0" + toReturn;
        }

        return new int [] {Character.getNumericValue(toReturn.charAt(0)), Character.getNumericValue(toReturn.charAt(1))};
    }

    private int[] p4(int[] firstPart, int[] secondPart){
        return new int[] {firstPart[1],secondPart[1],secondPart[0],firstPart[0]};
    }

    private int[] sw(int[] character){
        return new int[] {character[4],character[5],character[6],character[7],character[0],character[1],character[2],character[3]};
    }
}
