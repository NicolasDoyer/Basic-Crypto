package exception;

public class UndefinedOption extends Exception {

    private String flag;

    public UndefinedOption(String flag){
        this.flag = flag;
    }

    @Override
    public String toString() {
        return ("Error: option '" + this.flag + "' does'nt exists");
    }
}
