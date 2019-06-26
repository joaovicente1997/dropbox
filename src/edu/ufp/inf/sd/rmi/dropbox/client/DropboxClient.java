package edu.ufp.inf.sd.rmi.dropbox.client;

import edu.ufp.inf.sd.rmi.dropbox.server.FactoryRI;
import edu.ufp.inf.sd.rmi.dropbox.server.SessionRI;
import edu.ufp.inf.sd.util.rmisetup.SetupContextRMI;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2017</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui S. Moreira
 * @version 3.0
 */
public class DropboxClient {

    /**
     * Context for connecting a RMI client to a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private FactoryRI factoryRI;
    //private SessionRI sessionRI;

    public static void main(String[] args) throws InterruptedException {
        if (args != null && args.length < 3) {
            System.err.println("usage examples:\n\t- login 'username' 'password'\n\t- register 'username' 'password'");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            DropboxClient hwc = new DropboxClient(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            assert args != null;
            hwc.playService(args);
        }
    }

    public DropboxClient(String args[]) {
        try {
            //List ans set args
            printArgs(args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(DropboxClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);
                
                //============ Get proxy to HelloWorld service ============
                factoryRI = (FactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return factoryRI;
    }
    
    private void playService(String[] args) throws InterruptedException {

            try {
                if(args[3].compareTo("register") == 0) {
                    factoryRI.register(args[4], args[5]);
                }
                else if(args[3].compareTo("login") == 0) {
                    SessionRI sessionRI = factoryRI.login(args[4], args[5]);
                    Thread thread = new Thread(new MonitorPermissions(sessionRI));
                    thread.start();
                    ArrayList<String> permissions = sessionRI.listSubjects();
                    for(String perm : permissions) {
                        new ObserverImpl(args[4], sessionRI.getSubject(perm));
                        sessionRI.updateClientFolder(args[4], perm);
                    }
                    if(args.length > 7) {
                        if (args[6].compareTo("share") == 0) {
                            sessionRI.shareWith(args[4], args[7]);
                        }
                    }
                    Signal.handle(new Signal("INT"), sig -> {
                        try {
                            if(sessionRI.logout(args[4])) {
                                System.out.println("LOGGED OUT!\n");
                                System.exit(0);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                }
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "goint to finish, bye. ;)");
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }

    }

    private void printArgs(String args[]) {
        for (int i = 0; args != null && i < args.length; i++) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "args[{0}] = {1}", new Object[]{i, args[i]});
        }
    }
}
