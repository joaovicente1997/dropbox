package edu.ufp.inf.sd.rmi.dropbox.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: UFP </p>
 * @author Rui S. Moreira
 * @version 3.0
 */
public class FactoryImpl extends UnicastRemoteObject implements FactoryRI {

    private final DBMockup dbMockup;

    // Uses RMI-default sockets-based transport
    // Runs forever (do not passivates) - Do not needs rmid (activation deamon)
    // Constructor must throw RemoteException due to export()
    public FactoryImpl(DBMockup dbMockup) throws RemoteException {
        // Invokes UnicastRemoteObject constructor which exports remote object
        super();
        this.dbMockup = dbMockup;
    }

    @Override
    public SessionRI login(String u, String p) throws RemoteException {
        //DigLibSessionRI digLibSessionRI = new DigLibSessionImpl();
        if(dbMockup.exists(u)) {
            if(!dbMockup.existsSession(u, p)) {
                SessionRI sessionRI = new SessionImpl(u, p, this.dbMockup);
                this.dbMockup.getSessions().add(sessionRI);
                return sessionRI;
            }
        }
        System.out.println("ERRO NO LOGIN\n");
        return null;
    }

    @Override
    public Boolean register(String u, String p) throws RemoteException {
        if(!dbMockup.exists(u)) {
            return dbMockup.register(u, p);
        }
        return false;
    }
}
