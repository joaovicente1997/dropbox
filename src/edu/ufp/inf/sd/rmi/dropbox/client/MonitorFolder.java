package edu.ufp.inf.sd.rmi.dropbox.client;

import edu.ufp.inf.sd.rmi.dropbox.server.State;
import edu.ufp.inf.sd.rmi.dropbox.server.SubjectRI;

import java.io.FileInputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorFolder implements Runnable {
    private final String observerUserName;
    private final SubjectRI subject;
    private final String baseClientFolder;

    public MonitorFolder (String observerUserName, SubjectRI subject, String baseClientFolder)
    {
        this.observerUserName = observerUserName;
        this.subject=subject;
        this.baseClientFolder = baseClientFolder;
    }

    public void runMonitor()
    {

        try(WatchService service = FileSystems.getDefault().newWatchService()){
            String type = "";
            Map<WatchKey, Path> KeyMap = new HashMap<>();
            Path path= Paths.get(baseClientFolder + observerUserName + "\\" + subject.getName());
            KeyMap.put(path.register(service,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY),
                    path);
            WatchKey watchKey;

            do {
                watchKey = service.take();
                Path eventDir = KeyMap.get(watchKey);
                List<WatchEvent<?>> events = watchKey.pollEvents();
                System.out.println(this.getClass().getName()+": events.size() = "+events.size());
                for(WatchEvent<?> event : events)
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path)event.context();
                    Path verify = Paths.get(baseClientFolder + observerUserName + "\\" + subject.getName() + "\\" + eventPath.toString());
                    FileInputStream fileInputStream = null;
                    byte[] bytes = new byte[0];
                    if(verify.toFile().isDirectory()) {
                        type = "directory";
                    } else if(verify.toFile().isFile()) {
                        type = "file";
                        bytes = new byte[(int)verify.toFile().length()];
                        fileInputStream = new FileInputStream(verify.toFile());
                        fileInputStream.read(bytes);
                        fileInputStream.close();
                    }
                    State newState = new State(kind.toString(),type,bytes,observerUserName,eventPath.toString());
                    System.out.println(newState.toString());
                    subject.setState(newState);
                }
            } while (watchKey.reset());

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try{
            runMonitor();
            Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}