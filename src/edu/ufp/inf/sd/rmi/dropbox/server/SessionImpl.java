package edu.ufp.inf.sd.rmi.dropbox.server;

import edu.ufp.inf.sd.rmi.dropbox.client.ObserverRI;
import edu.ufp.inf.sd.rmi.dropbox.server.SessionRI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: UFP </p>
 * @author Rui S. Moreira
 * @version 3.0
 */
public class SessionImpl implements SessionRI {

    private String t;
    private String a;
    private DBMockup db;
    // Uses RMI-default sockets-based transport
    // Runs forever (do not passivates) - Do not needs rmid (activation deamon)
    // Constructor must throw RemoteException due to export()
    public SessionImpl(String t, String a, DBMockup db) throws RemoteException {
        super();
        this.t = t;
        this.a = a;
        this.db = db;
        export();
    }

    public void export() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public ArrayList<String> listSubjects() throws RemoteException {
        return getDB().getPermissions(t, a);
    }


    @Override
    public DBMockup getDB() throws RemoteException {
        return this.db;
    }

    @Override
    public SubjectRI getSubject(String name) throws RemoteException {
        return getDB().getSubject(name);
    }

    @Override
    public boolean shareWith(String srcName, String destName) throws RemoteException {
        return getDB().shareWith(srcName, destName);
    }

    @Override
    public void updateClientFolder(String username, String folder) throws RemoteException {
        String baseServerFolder = getSubject(folder).getBaseServerFolder();
        if(baseServerFolder != null) {
            File serverSideFolder = new File(baseServerFolder + folder);
            if(serverSideFolder.exists()) {
                SubjectRI subjectRI = getSubject(folder);
                for(File file : Objects.requireNonNull(serverSideFolder.listFiles())) {
                    try{
                        State s = null;
                        if(file.isDirectory()) {
                            s = new State("ENTRY_CREATE", "directory", null, username, file.getName());
                        }
                        else {
                            byte[] bytes = new byte[(int)file.length()];
                            FileInputStream fis = new FileInputStream(file);
                            fis.read(bytes);
                            fis.close();
                            s = new State("ENTRY_MODIFY", "file", bytes, username, file.getName());
                        }
                        subjectRI.setState(s);
                        subjectRI.getObserver(username).update();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public boolean logout(String username) throws RemoteException {
        if(getDB().exists(username)) {
            for (String perm : getDB().findUser(username).getSubjectsName()) {
                getSubject(perm).getObserver(username).stopThread();
                getSubject(perm).detach(getSubject(perm).getObserver(username));
            }
            getDB().getSessions().remove(this);
            return true;
        }
        return false;
    }

    @Override
    public String getT() throws RemoteException {
        return t;
    }

    @Override
    public String getA() throws RemoteException {
        return a;
    }
}
