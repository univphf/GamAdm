package gamadm;

/**
 *
 * @author maj
 */
class Intitule {

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

    public Intitule(String lib, String libc) {
        this.lib = lib;
        this.code = libc;
    }
   
    private String lib, code;

    @Override
    public String toString() {
        return lib;
    }
    
}
