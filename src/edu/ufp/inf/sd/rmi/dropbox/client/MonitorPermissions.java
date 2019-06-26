package edu.ufp.inf.sd.rmi.dropbox.client;

import edu.ufp.inf.sd.rmi.dropbox.server.SessionRI;

import java.rmi.RemoteException;

public class MonitorPermissions implements Runnable {
    private final SessionRI sessionRI;

    public MonitorPermissions(SessionRI sessionRI) {
        this.sessionRI = sessionRI;
    }

    public void runMonitor() throws RemoteException {
        int size = sessionRI.listSubjects().size();
        while(true) {
            int newSize = sessionRI.listSubjects().size();
            //System.out.println(newSize + "\n");
            if(newSize != size) {
                if(newSize > size) {
                    String subject = sessionRI.listSubjects().get(newSize - 1);
                    new ObserverImpl(sessionRI.getT(), sessionRI.getSubject(subject));
                    sessionRI.updateClientFolder(sessionRI.getT(), subject);
                    size = newSize;
                }
                else {
                    /*
                    User u = sessionRI.getDB().findUser(sessionRI.getT());
                    for(String s : u.getSubjectsName()) {
                        sessionRI.getSubject(s).getObserver()
                            sessionRI.getSubject(s).detach();
                        }
                    }*/
                }
            }
        }
    }

    @Override
    public void run() {
        try{
            runMonitor();
            Thread.sleep(2000);
        }catch(InterruptedException | RemoteException e){
            e.printStackTrace();
        }
    }
}
