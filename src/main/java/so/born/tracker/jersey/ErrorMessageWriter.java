package so.born.tracker.jersey;

import io.dropwizard.jersey.errors.ErrorMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class ErrorMessageWriter implements MessageBodyWriter<ErrorMessage> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ErrorMessage.class == type && MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType);
    }

    @Override
    public long getSize(ErrorMessage t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return string(t).length();
    }

    @Override
    public void writeTo(ErrorMessage t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
       throws IOException, WebApplicationException
    {
        entityStream.write(string(t).getBytes("utf8"));
    }

    private String string(ErrorMessage t) {
        return String.format("<error>%s<error>", t.getMessage());
    }

}
