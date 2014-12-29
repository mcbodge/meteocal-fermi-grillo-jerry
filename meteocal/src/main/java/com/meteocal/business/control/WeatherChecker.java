import com.meteocal.business.entity.WeatherCondition;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Manuel
 */

public class WeatherChecker {

    private static final Logger logger = Logger.getLogger(WeatherChecker.class.getName());
    
    private Client client;
    
    @PostConstruct
    private void constructed() {
        //logger.log(Level.INFO, "WeatherChecker created");
        client = ClientBuilder.newClient();
    }

    public WeatherCondition checkWeather(Integer geoname) {
        //logger.log(Level.INFO, "{0}: checking the weather", new Date());
        WeatherCondition forecast = client.target("api.openweathermap.org/data/2.5/weather?id=" + geoname.toString())
                .request(MediaType.APPLICATION_JSON).get(WeatherCondition.class);
        //logger.log(Level.INFO, "Oracle says: {0}", forecast.getResult());
        
        return forecast;
    }
    
    public String test(Integer geoname){
        WeatherCondition wc;
        wc=checkWeather(geoname);
        return wc.getId().toString();
    }

}
