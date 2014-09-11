package so.born.tracker.jersey;

import io.dropwizard.jersey.errors.ErrorMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class SyndFeedWriter implements MessageBodyWriter<SyndFeed> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return SyndFeed.class.isAssignableFrom(type) &&
                MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType);
    }

    @Override
    public long getSize(SyndFeed t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(SyndFeed t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
       throws IOException, WebApplicationException
    {
        SyndFeedOutput output = new SyndFeedOutput();
        try {
            output.output(t, new OutputStreamWriter(entityStream, "utf8"));
        } catch (FeedException e) {
            throw new WebApplicationException(e,
                    Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build());
        }
        
    }
}
