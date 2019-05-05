package gamadm;

/**
 *
 * @author maj
 */
public class Sexe {
    String lib, code;

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String libc) {
        this.code = libc;
    }

    public Sexe(String lib, String libc) {
        this.lib = lib;
        this.code = libc;
    }
    
    @Override
    public String toString(){
        return lib;
    }
}
