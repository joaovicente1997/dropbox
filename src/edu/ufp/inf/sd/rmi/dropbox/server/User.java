package edu.ufp.inf.sd.rmi.dropbox.server;

import java.util.ArrayList;

/**
 *
 * @author rmoreira
 */
public class User {

    private String uname;
    private String pword;
    private ArrayList<String> subjectsName;

    public User(String uname, String pword) {
        this.uname = uname;
        this.pword = pword;
        subjectsName = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User{" + "uname=" + uname + ", pword=" + pword + '}';
    }

    /**
     * @return the uname
     */
    public String getUname() {
        return uname;
    }

    /**
     * @param uname the uname to set
     */
    public void setUname(String uname) {
        this.uname = uname;
    }

    /**
     * @return the pword
     */
    public String getPword() {
        return pword;
    }

    /**
     * @param pword the pword to set
     */
    public void setPword(String pword) {
        this.pword = pword;
    }

    public ArrayList<String> getSubjectsName() {
        return subjectsName;
    }
}
