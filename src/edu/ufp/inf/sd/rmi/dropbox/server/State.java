package edu.ufp.inf.sd.rmi.dropbox.server;

import java.io.Serializable;
import java.util.Arrays;

public class State implements Serializable {

    private String kind;
    private String type;
    private byte[] bytes;
    private String username;
    private String folderName;

    public State(){}

    public State(String kind, String type, byte[] bytes, String username, String folderName){
        this.kind = kind;
        this.type = type;
        this.bytes = bytes;
        this.username = username;
        this.folderName = folderName;
    }

    public void setState(State s){
        this.kind=s.kind;
        this.type=s.type;
        this.bytes=s.bytes;
        this.username=s.username;
        this.folderName=s.folderName;
    }

    @Override
    public String toString() {
        return "State{" +
                "kind='" + kind + '\'' +
                ", type='" + type + '\'' +
                ", bytes='" + Arrays.toString(bytes) + '\'' +
                ", username='" + username + '\'' +
                ", folderName='" + folderName + '\'' +
                '}';
    }

    public String getKind() { return kind; }

    public void setKind(String kind) { this.kind = kind; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getUserame() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getFolderName() { return folderName; }

    public void setFolderName(String folderName) { this.folderName = folderName; }

    public byte[] getBytes() { return bytes; }

    public void setBytes(byte[] bytes) { this.bytes = bytes; }
}
