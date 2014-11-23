package so.born.tracker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/ping")
public class TrackerPing {

    @Produces("text/plain")
    @GET
    public String ping() {
        return "pong";
    }
}
