package controllers.devsocket;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import play.mvc.*;
import play.libs.F;
import play.mvc.Http.*;
import play.vfs.VirtualFile;
import play.Logger;

import static play.libs.F.Matcher.ClassOf;
import static play.mvc.Http.WebSocketEvent.SocketClosed;

public class WatchStatic extends WebSocketController {

    public static void watch() {
        final F.EventStream<String> stream = new F.EventStream<String>();
        
        TimerTask watcher = new TimerTask(){
           private void checkForModification(VirtualFile f){
               if(f.isDirectory()){
                   for(VirtualFile c : f.list())
                       checkForModification(c);
               }
               else{
                    if(System.currentTimeMillis() - f.lastModified() < 1000){
                        Logger.debug("[devsocket] File modified: " + f);
                        stream.publish(Router.reverse(f));
                    }
               }
           } 
            
           @Override
            public void run() {
               VirtualFile f = play.Play.getVirtualFile("/public");
               checkForModification(f);
            } 
        };
        
        Timer t = new Timer();
        t.scheduleAtFixedRate(watcher, 0, 200);

        while (inbound.isOpen()) {
            F.Either<Http.WebSocketEvent, String> e = await(F.Promise.waitEither(
                    inbound.nextEvent(),
                    stream.nextEvent()
            ));
            
            //Service event
            for(String evt: ClassOf(String.class).match(e._2)) {
                outbound.send(e._2.get());
            }
            
            // Case: The socket has been closed
            for(Http.WebSocketClose closed: SocketClosed.match(e._1)) {
                disconnect();
            }

        }
    }
}