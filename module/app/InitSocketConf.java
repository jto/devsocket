import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Router;

@OnApplicationStart
public class InitSocketConf extends Job{
    @Override
    public void doJob() throws Exception {
        if(Play.mode == Play.Mode.DEV){
            Router.addRoute("WS", "/@devsocket", "devsocket.WatchStatic.watch");
            Logger.debug("@devsocket route added");
        }
    }
}
