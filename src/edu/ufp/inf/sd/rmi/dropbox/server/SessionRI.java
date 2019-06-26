package edu.ufp.inf.sd.rmi.dropbox.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: UFP </p>
 * @author Rui Moreira
 * @version 1.0
 */
public interface SessionRI extends Remote {
    //public SubjectRI getSubject(String username) throws RemoteException;
    public ArrayList<String> listSubjects() throws RemoteException;
    public DBMockup getDB() throws RemoteException;
    public SubjectRI getSubject(String name) throws RemoteException;
    public boolean shareWith(String srcName, String destName) throws RemoteException;
    public void updateClientFolder(String username, String folder) throws  RemoteException;
    public boolean logout(String username) throws RemoteException;
    public String getA() throws RemoteException;
    public String getT() throws RemoteException;
}
