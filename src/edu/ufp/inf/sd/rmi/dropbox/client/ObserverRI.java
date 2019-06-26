package edu.ufp.inf.sd.rmi.dropbox.client;

import edu.ufp.inf.sd.rmi.dropbox.server.State;

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
public interface ObserverRI extends Remote {
    public void update() throws IOException, RemoteException, InterruptedException;
    public State getLastObserverState() throws RemoteException;
    public String getUsername() throws RemoteException;
    public void stopThread() throws RemoteException;
}
