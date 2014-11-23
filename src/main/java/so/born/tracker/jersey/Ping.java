package so.born.tracker.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/ping")
public class Ping {

    @Produces("text/plain")
    @GET
    public String ping() {
        return "pong";
    }
}
