import encryption.Rsa;
import encryption.Sdes;
import encryption.Vigenere;
import exception.UndefinedOption;

import java.util.HashMap;

public class Main {

    private HashMap<String,String> options;
    private static final String[] OPTIONS_NAME = {
            "-encrypt-vig",
            "-decrypt-vig",
            "-key",
            "-crack-vig",
            "-sdes",
            "-char",
            "-rsa",
            "-h",
            "-help"
    };

    private Main(){
        this.options = new HashMap<>();
        for(String name : OPTIONS_NAME){
            this.options.put(name, null);
        }
    }

    private HashMap<String,String> getOptions(){
        return this.options;
    }

    private void setOption(String flag, String value) throws UndefinedOption{
        if(this.options.containsKey(flag)){
            this.options.put(flag,value);
        }
        else{
            throw new UndefinedOption(flag);
        }
    }

    private void parseOptions(String[] args){
        for(int i = 0; i < args.length; i++){
            switch (args[i].charAt(0)){
                case '-':
                    try{
                        this.setOption(args[i], args[i+1]);
                    }catch(UndefinedOption exception){
                        System.err.println(exception.toString());
                        System.exit(-1);
                    }catch(ArrayIndexOutOfBoundsException exception){
                        try{
                            this.setOption(args[i],"");
                        }catch (UndefinedOption exceptionOption){
                            System.err.println(exceptionOption.toString());
                            System.exit(-1);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void printHelp(){
        System.out.println("Usage: ");
        System.out.println("      Decrypt file (Vigenere): java Main -decrypt-vig [filename] -key [key]");
        System.out.println("      Encrypt file (Vigenere): java Main -encrypt-vig [filename] -key [key]");
        System.out.println("      Crack file   (Vigenere): java Main -crack-vig [filename]");
        System.out.println("");
        System.out.println("      Encrypt char (SDES): java Main -sdes [character]");
        System.out.println("");
        System.out.println("      RSA : java Main -rsa \"[message]\"");
    }

    public static void main(String[] args) {

        Main executable = new Main();
        executable.parseOptions(args);

        if(args.length <= 0)
            executable.printHelp();

        executable.getOptions().forEach((flag,value) -> {
            if(value != null){
                switch (flag){
                    case "-encrypt-vig":
                        if(executable.getOptions().get("-key") == null || executable.getOptions().get("-key").equals(""))
                            executable.printHelp();
                        else
                            Vigenere.encryptFile(value, executable.getOptions().get("-key"));
                        break;
                    case "-decrypt-vig":
                        if(executable.getOptions().get("-key") == null || executable.getOptions().get("-key").equals(""))
                            executable.printHelp();
                        else
                            Vigenere.decryptFile(value, executable.getOptions().get("-key"));
                        break;
                    case "-crack-vig":
                        if(executable.getOptions().get("-crack-vig") == null || executable.getOptions().get("-crack-vig").equals(""))
                            executable.printHelp();
                        else
                            Vigenere.crackFile(executable.getOptions().get("-crack-vig"));
                        break;
                    case "-sdes":
                        if(executable.getOptions().get("-char") == null || executable.getOptions().get("-char").equals(""))
                            executable.printHelp();
                        else {
                            Sdes sdes = new Sdes();
                            System.out.println(sdes.run((executable.getOptions().get("-char")).charAt(0)));
                        }
                        break;
                    case "-rsa":
                        try{
                            // Creating RSA instance
                            Rsa encryptor = new Rsa();
                            String message = executable.getOptions().get("-rsa");

                            // Encrypting message
                            System.out.println("Message encrypté: ");
                            String messageEncrypted = Rsa.encryptMessage(message,encryptor.getPublicKey());
                            System.out.println(messageEncrypted);

                            // Decrypting message
                            System.out.println("Message décrypté: ");
                            String messageDecrypted = Rsa.decryptMessage(messageEncrypted,encryptor);
                            System.out.println(messageDecrypted);

                        }catch(Exception e){
                            System.err.println(e.toString());
                        }
                    case "-h":
                    case "-help":
                        executable.printHelp();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
