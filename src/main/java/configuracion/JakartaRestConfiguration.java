package configuracion;

import API.PerfilesResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.itson.api_matchmaker.resources.JakartaEE10Resource;
import java.util.Set;

/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */
@ApplicationPath("resources")
public class JakartaRestConfiguration extends Application {
   @Override
   public Set<Class<?>> getClasses(){
       final Set<Class<?>> geClasses = new HashSet<>();
       geClasses.add(PerfilesResource.class);
       geClasses.add(JakartaEE10Resource.class);
       geClasses.add(JacksonFeature.class);
       return geClasses;
    }
}
