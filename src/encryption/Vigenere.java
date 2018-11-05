package encryption;

import utils.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Classe permettant le cryptage/decyptage via la méthode Vigenere
 *
 */
public class Vigenere {

    private static final String DEFAULT_KEY  = "vigenere";
    private static final int MAX_KEY_SIZE = 10;
    private static final HashMap<String, Double> icByCountry = new HashMap<>();

    static{
        icByCountry.put("EN", 0.0667);
        icByCountry.put("FR", 0.0778);
    }

    /** Fonction statique permettant de crypter un fichier
     * Cette fonction prend le nom du fichier en parametre et encrypte le texte selon une cle
     * @param fileName: Nom du fichier contenant les donnees a crypter
     * @param key: Cle de cryptage
     * @return Reussite ou non du cryptage du fichier
     */
    public static boolean encryptFile(String fileName, String key){
        return processFile(fileName, "encrypted_" + fileName, key, false);
    }


    /** Fonction statique permettant de decrypter un fichier
     * Cette fonction prend le nom du fichier en paramètre et décrypte le texte selon une clé
     * @param fileName: Nom du fichier contenant les données à décrypter
     * @param key: Clé de cryptage
     * @return: Réussite ou non du décryptage du fichier
     */
    public static boolean decryptFile(String fileName, String key){
        return processFile(fileName, "decrypted_" + fileName, key, true);
    }

    private static String getFileData(String fileName){
        File file = new File(fileName); // File to open
        byte[] buffer = new byte[1]; // File buffer
        String fileData = "";

        // Check if file exists
        if (!file.exists() || !file.isFile()) {
            /* TODO Throw exception here */
            return "";
        }
        try{
            FileInputStream originalFile = new FileInputStream(file);
            while(originalFile.read(buffer) >= 0){
                fileData += new String(buffer, StandardCharsets.UTF_8).toLowerCase();

            }
            originalFile.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return fileData;

    }

    /**
     * Cette fonctione essaye de retrouver la clé de cryptage des données d'un fichier
     * @param fileName: Nom du fichier à cracker
     * @return Reussite ou non du crackage
     */
    public static boolean crackFile(String fileName) {

        double ic[] = new double[MAX_KEY_SIZE];
        String[][] allTextParts = new String[MAX_KEY_SIZE][MAX_KEY_SIZE];
        int[][][] occurences = new int[MAX_KEY_SIZE][MAX_KEY_SIZE][26];
        String fileData = getFileData(fileName);

        try{
            // Taille de la clé à tester sur le texte
            for(int i = 1; i <= MAX_KEY_SIZE; i++){
                // Boucle servant à contruire X sous-textes par rapport à la taille de clé
                for(int x = 0; x < i; x++){
                    // 26 premiers = occurences des lettre de l'alpahbet | dernier indice = nombre de lettre du texte
                    Data data = processCrackFile(fileData, x, i);
                    allTextParts[i - 1][x] = data.text;
                    occurences[i - 1][x] = data.occurences;
                    // Calcul indice coincidence
                    double currentIc = getIc(data.occurences, data.nbLetters);
                    if(ic[i - 1] < currentIc)
                        ic[i - 1] = currentIc;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        // Print results and get IC MAX + possible key length
        int i = 1, keyLength = 1;
        double icMax = 0;
        for(double iic: ic){
            if(icMax < iic){
                icMax = iic;
                keyLength = i;
            }
            System.out.println("Taille " + i + ": " + iic);
            i++;
        }

        // Get text language
        String country = "";
        double temp = 1;
        for (Map.Entry mapentry : icByCountry.entrySet()){
            double ecart = (double) mapentry.getValue() - icMax;
            if(Math.abs(ecart) < temp) {
                temp = ecart;
                country = (String) mapentry.getKey();
            }
        }

        System.out.println("Langue du texte : " + country);
        System.out.println("Taille de la clé : " + keyLength);

        // Get letter frequency for each part of the text
        String key = "";
        for(i = 0; i < keyLength; i++){
            double maxFrequency = 0;
            int encryptedLetter = 0;
            for(int j = 0; j < 26; j ++){
                double frequency = (double) occurences[keyLength - 1][i][j] / allTextParts[keyLength - 1][i].length();
                if(frequency > maxFrequency) {
                    maxFrequency = frequency;
                    encryptedLetter = j;
                }
            }
            // Supposing its a 'e'
            char letter = (char) (encryptedLetter + 'a');
            int decalage = Math.floorMod((letter - 'e'), 26);
            key += (char) (decalage + (int)'a');
        }

        System.out.println("Clé : " + key);
        processFile(fileName,"decrypted_" + fileName, key, true);
        return true;
    }

    /**
     * Cette fonction permet de procéder au décryptage/cryptage d'un fichier selon une clé
     * @param originalFileName: Nom du fichier à crypter/décrypter
     * @param outputFileName: Nom du fichier où seront sauvegarder les données cryptées/décryptées du fuchier original
     * @param decrypt: true = procede au decryptage du fichier, sinon procede au cryptage
     */
    private static boolean processFile(String originalFileName, String outputFileName, String key, boolean decrypt){

        // Check if the file exists
        File file = new File(originalFileName);
        if(!file.exists() || !file.isFile()){
            return false;
        }

        // If no key specified, get the default key defined in class
        if(key == null)
            key = DEFAULT_KEY;

        try{
            // Read from original file, and write to output file
            FileInputStream originalFile = new FileInputStream(file);
            FileOutputStream outputFile = new FileOutputStream(new File(outputFileName));
            byte[] buffer = new byte[1];
            int keyIndex = 0;

            // Read each character of the file
            while(originalFile.read(buffer) >= 0){
                // Reset key index
                if(keyIndex >= key.length()){
                    keyIndex = 0;
                }

                // Converting buffer to string. Then, see if it's an alphabetical character
                String toConvert = new String(buffer, StandardCharsets.UTF_8).toLowerCase();
                int keyValue = key.toLowerCase().charAt(keyIndex) - (int)'a';
                if(toConvert.matches("[a-z]+")){
                    int toConvertAscii = (int) toConvert.charAt(0) - (int)'a';
                    // If we want to decrypt
                    if(decrypt)
                        outputFile.write(Math.floorMod((toConvertAscii - keyValue), 26) + (int)'a');
                    // Or encrypt
                    else
                        outputFile.write(Math.floorMod((toConvertAscii + keyValue), 26) + (int)'a');
                    // Move the key index by 1
                    keyIndex++;
                }
                else{
                    // Keep non-alphabetical character
                    outputFile.write((int) toConvert.charAt(0));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Retourne le nombre d'occurences de chaque lettre et la taille du texte
     * @param fileData: Données du fichier à lire
     * @param indiceLecture: Indice de lecture dans le fichier à lire
     * @param keySize:
     * @return Données avec les occurences des lettres trouvées et le nombre de lettres traitées
     */
    private static Data processCrackFile(String fileData, int indiceLecture, int keySize){

        Data data = new Data(); // Data to return
        int tempIndiceLecture = 0;

        for(int i = 0; i < fileData.length(); i++){
            String character = Character.toString(fileData.charAt(i));
            if(character.matches("[a-z]+")){
                    int value = character.charAt(0) - (int)'a';
                    if( Math.floorMod(tempIndiceLecture, keySize) == indiceLecture){
                        data.text += character;
                        data.occurences[value] ++;
                        data.nbLetters ++;
                    }
                    tempIndiceLecture ++;
            }
        }

        return data;
    }

    /**
     * Calcule l'indice de coincidence a l'aide d'un tableau d'occurance et d'un nombre total de lettres d'un texte
     * @param occurences: Tableau représentant les occurences de toutes les lettres de l'alphabet présentes dans un texte
     * @param nbLetters: Nombre total de lettre d'un texte
     * @return Indice de coincidence
     */
    private static double getIc(int[] occurences, int nbLetters){
        double ic = 0;
        for(int nb: occurences){
            ic += ((nb * (nb - 1.0))) / (nbLetters * (nbLetters - 1.0));
        }

        return ic;
    }

}
