package edu.ufp.inf.sd.rmi.dropbox.server;

import edu.ufp.inf.sd.rmi.dropbox.client.ObserverRI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: UFP </p>
 * @author Rui S. Moreira
 * @version 3.0
 */
public class SubjectImpl extends UnicastRemoteObject implements SubjectRI {

    private ArrayList<ObserverRI> observers = new ArrayList<>();
    private State state = new State();
    private String name;
    private String baseServerFolder = "\\Program Files\\dropbox\\data\\server\\";

    // Uses RMI-default sockets-based transport
    // Runs forever (do not passivates) - Do not needs rmid (activation deamon)
    // Constructor must throw RemoteException due to export()
    public SubjectImpl(String name) throws RemoteException {
        // Invokes UnicastRemoteObject constructor which exports remote object
        super();
        this.name = name;
        File file = new File(baseServerFolder + name);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    @Override
    public void attach(ObserverRI o) throws RemoteException {
        if(this.observers.contains(o)){
            return;
        }
        this.observers.add(o);
    }

    @Override
    public void detach(ObserverRI o) throws RemoteException {
        this.observers.remove(o);
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void setState(State s) throws IOException, InterruptedException {
        synchronized (this.state) {

            System.out.println(this.getClass().getName()+"->setState(): "+s.toString());

            this.state.setState(s);

            if (this.state.getKind().equals(String.valueOf(StandardWatchEventKinds.ENTRY_CREATE))) {
                if (this.state.getType().equals("directory")) {
                    File file = new File(baseServerFolder + this.state.getUserame() + "\\" + this.state.getFolderName());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else if (this.state.getType().equals("file")) {
                    Path path = Paths.get(baseServerFolder + this.state.getUserame() + "\\" + this.state.getFolderName());
                    FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
                    fileOutputStream.write(this.state.getBytes());
                    fileOutputStream.close();
                }
            } else if (this.state.getKind().equals(String.valueOf(StandardWatchEventKinds.ENTRY_DELETE))) {
                File file = new File(baseServerFolder  + this.state.getUserame() + "\\" + this.state.getFolderName());
                if (file.exists()) {
                    file.delete();
                }
            } else if (this.state.getKind().equals(String.valueOf(StandardWatchEventKinds.ENTRY_MODIFY))) {
                File file = new File(baseServerFolder + this.state.getUserame() + "\\" + this.state.getFolderName());
                if(file.exists() && (file.length() == this.state.getBytes().length))
                    return;
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(this.state.getBytes());
                fos.close();
            }
            notifyAllObservers();
        }
    }

    @Override
    public void notifyAllObservers() throws IOException, InterruptedException {
        for(ObserverRI observerRI : this.observers)
        {
            //System.out.println(this.getClass().getName()+"->notifyAllObservers(): "+observerRI.getUsername());
            if(!this.state.getUserame().equals(observerRI.getUsername())){
                observerRI.update();
            }
        }
    }

    @Override
    public String getName() throws RemoteException {
        return this.name;
    }

    @Override
    public String getBaseServerFolder() throws RemoteException{
        return this.baseServerFolder;
    }

    @Override
    public ObserverRI getObserver(String username) throws RemoteException {
        for (ObserverRI observerRI:this.observers) {
            if(observerRI.getUsername().equals(username))
                return observerRI;
        }
        return null;
    }
}
