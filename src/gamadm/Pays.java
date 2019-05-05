package gamadm;

/**
 *
 * @author maj
 */
class Pays {
    String lib,code;

    @Override
    public String toString() {
        return lib;
    }

    public Pays(String lib, String code) {
        this.lib = lib;
        this.code = code;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
