package gamadm;

/**
 *
 * @author maj
 */
class Nationalite {
    String lib,code;

    @Override
    public String toString() {
        return lib;
    }

    public Nationalite(String lin, String code) {
        this.lib = lin;
        this.code = code;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lin) {
        this.lib = lin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
