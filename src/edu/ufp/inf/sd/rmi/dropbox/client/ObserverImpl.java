package edu.ufp.inf.sd.rmi.dropbox.client;

import edu.ufp.inf.sd.rmi.dropbox.server.State;
import edu.ufp.inf.sd.rmi.dropbox.server.SubjectRI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.StandardWatchEventKinds;
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
public class ObserverImpl implements ObserverRI {

    private State lastObsState;
    private String username;
    public SubjectRI subjectRI;
    private static final String clientBaseFolder = "\\Users\\dropbox\\data\\clients\\";
    private Thread thread;

    // Uses RMI-default sockets-based transport
    // Runs forever (do not passivates) - Do not needs rmid (activation deamon)
    // Constructor must throw RemoteException due to export()
    public ObserverImpl(String username, SubjectRI subjectRI) throws RemoteException {
        this.username = username;
        this.subjectRI = subjectRI;

        File file = new File(clientBaseFolder + "\\" + username + "\\" + subjectRI.getName());
        if(!file.exists()){
            file.mkdirs();
            file.setExecutable(true);
            file.setReadable(true);
            file.setWritable(true);
        }

        thread = new Thread(new MonitorFolder(username, subjectRI, clientBaseFolder));
        thread.start();

        export();
        this.subjectRI.attach(this);
    }

    private void export() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void update() throws IOException, InterruptedException {
        //System.out.println(this.getClass().getName()+"->update():...");
        if(this.lastObsState != this.subjectRI.getState()) {
            this.lastObsState = this.subjectRI.getState();
            //System.out.println(this.getClass().getName()+"->update(): state = "+this.lastObsState.toString());

            if(this.lastObsState.getKind().equals(String.valueOf(StandardWatchEventKinds.ENTRY_CREATE))) {
                if(this.lastObsState.getType().equals("directory")){
                    File file = new File(clientBaseFolder + this.getUsername() +"\\"+subjectRI.getName()+"\\"+this.lastObsState.getFolderName());
                    if(!file.exists()) {
                        file.mkdirs();
                        file.setExecutable(true);
                        file.setReadable(true);
                        file.setWritable(true);
                    }
                }
                else if(this.lastObsState.getType().equals("file")) {
                    transferFile();
                }
            }
            else if(this.lastObsState.getKind().equals(String.valueOf(StandardWatchEventKinds.ENTRY_DELETE))) {
                File file = new File(clientBaseFolder + this.getUsername() +"\\"+subjectRI.getName()+"\\"+this.lastObsState.getFolderName());
                if(file.exists()) {
                    file.delete();
                }
            }
            else if(this.lastObsState.getKind().equals(String.valueOf(StandardWatchEventKinds.ENTRY_MODIFY))) {
                thread.sleep(1000);
                transferFile();
            }
        }
    }

    private void transferFile() throws IOException {
        File file = new File(clientBaseFolder + this.getUsername() +"\\"+subjectRI.getName()+"\\"+this.lastObsState.getFolderName());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(this.lastObsState.getBytes());
        fos.close();
    }

    @Override
    public void stopThread() throws RemoteException {
        this.thread.interrupt();
    }

    @Override
    public State getLastObserverState() throws RemoteException {
        return this.lastObsState;
    }

    public String getUsername() throws RemoteException {
        return username;
    }
}
