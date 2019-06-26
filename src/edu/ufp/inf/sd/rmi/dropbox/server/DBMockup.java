package edu.ufp.inf.sd.rmi.dropbox.server;

import edu.ufp.inf.sd.rmi.dropbox.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.dropbox.client.ObserverRI;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class simulates a DBMockup for managing users and books.
 *
 * @author rmoreira
 *
 */
public class DBMockup {

    private final ArrayList<User> users;// = new ArrayList();
    private final ArrayList<SubjectRI> subjects;
    private final ArrayList<SessionRI> sessions;

    /**
     * This constructor creates and inits the database with some books and users.
     */
    public DBMockup() throws RemoteException{
        users = new ArrayList();
        subjects = new ArrayList<>();
        sessions = new ArrayList<>();
    }

    /**
     * Registers a new user.
     * 
     * @param u username
     * @param p passwd
     */
    public Boolean register(String u, String p) throws RemoteException {
        if (!exists(u)) {
            User user = new User(u, p);
            SubjectRI subjectRI = new SubjectImpl(u);
            user.getSubjectsName().add(subjectRI.getName());
            subjects.add(subjectRI);
            users.add(user);
            return true;
        }
        return false;
    }

    /**
     * Checks the credentials of an user.
     * 
     * @param u username
     * @return
     */
    public boolean exists(String u) {
        if(this.users.isEmpty()){
            return false;
        } else{
            for (User usr : this.users) {
                if (usr.getUname().compareTo(u) == 0) {
                    return true;
                }
            }
        }
        return false;
        //return ((u.equalsIgnoreCase("guest") && p.equalsIgnoreCase("ufp")) ? true : false);
    }

    public SubjectRI getSubject(String subjectName) throws RemoteException {
        if(!this.subjects.isEmpty()) {
            for(int i=0; i<this.subjects.size(); i++) {
                if(this.subjects.get(i).getName().equals(subjectName)){
                    return this.subjects.get(i);
                }
            }
        }
        return null;
    }

    public ArrayList<String> getPermissions(String username, String password) {
        ArrayList<String> permissions = new ArrayList<>();
        if(exists(username)){
            for(User u : users){
                if(u.getUname().equals(username) && u.getPword().equals(password)) {
                    if(u.getSubjectsName().size() < 1) {
                        return null;
                    }
                    else {
                        permissions.addAll(u.getSubjectsName());
                    }
                }
            }
            return permissions;
        }
        return null;
    }

    public User findUser(String username) throws RemoteException{
        if(exists(username)) {
            for(User u : users) {
                if(u.getUname().equals(username)) {
                    return u;
                }
            }
        }
        return null;
    }

    public boolean shareWith(String srcName, String destName) throws RemoteException {
        User dest = findUser(destName);
        for(String perm : dest.getSubjectsName()) {
            if(perm.equals(srcName)) {
                return false;
            }
        }
        dest.getSubjectsName().add(srcName);
        return true;
    }

    public ArrayList<SessionRI> getSessions() {
        return this.sessions;
    }

    public boolean existsSession(String u, String p) throws RemoteException {
        for(SessionRI sessionRI : this.sessions) {
            if(sessionRI.getT().equals(u) && sessionRI.getA().equals(p)) {
                return true;
            }
        }
        return false;
    }
}
