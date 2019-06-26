package edu.ufp.inf.sd.rmi.dropbox.server;

import edu.ufp.inf.sd.rmi.dropbox.client.ObserverRI;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: UFP </p>
 * @author Rui Moreira
 * @version 1.0
 */
public interface SubjectRI extends Remote {
    public void attach(ObserverRI o) throws RemoteException;
    public void detach(ObserverRI o) throws RemoteException;
    public State getState() throws RemoteException;
    public void setState(State s) throws IOException, InterruptedException;
    public void notifyAllObservers() throws IOException, InterruptedException;
    public String getName() throws RemoteException;
    public String getBaseServerFolder() throws RemoteException;
    public ObserverRI getObserver(String username) throws RemoteException;
}
