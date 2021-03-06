package dao;

import com.beust.jcommander.JCommander;
import db.Sql2oModel;
import model.Event;
import model.User;
import org.sql2o.Sql2o;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;
import utils.CommandLineOptions;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static spark.Spark.port;

public class AppController implements UserDao, EventDao{

    private static final Logger logger = Logger.getLogger(AppController.class.getCanonicalName());

    private Sql2oModel dbModel;

    public AppController(String[] args) {

        CommandLineOptions options = new CommandLineOptions();
        new JCommander(options, args);

        logger.finest("Options.debug = " + options.debug);
        logger.finest("Options.database = " + options.database);
        logger.finest("Options.dbHost = " + options.dbHost);
        logger.finest("Options.dbUsername = " + options.dbUsername);
        logger.finest("Options.dbPort = " + options.dbPort);
        logger.finest("Options.servicePort = " + options.servicePort);

        port(options.servicePort);

//        Sql2o sql2o = new Sql2o("jdbc:postgresql://" + options.dbHost + ":" + options.dbPort + "/" + options.database,
//                options.dbUsername, options.dbPassword, new PostgresQuirks() {
//            {
//                converters.put(UUID.class, new UUIDConverter());
//            }
//        });
        Sql2o sql2o = new Sql2o("jdbc:postgresql://ec2-23-23-110-26.compute-1.amazonaws.com:5432/d77l275udotqkm",
                "lukhrwwuhmzshz", "0d05578accf9531f810085d616f8e325928c6db0c82d84396aadeec051470ce6", new PostgresQuirks() {
            {
                converters.put(UUID.class, new UUIDConverter());
            }
        });

        dbModel = new Sql2oModel(sql2o);
    }

    @Override
    public User getUserbyUsername(String username) {
        return dbModel.getUserByUsername(username);
    }

    @Override
    public void registerUser(User user) {
        dbModel.createUser(user);
    }

    @Override
    public void insertEvent(Event e) {
        dbModel.createEvent(e);
    }

    @Override
    public List<Event> getPublicEvents() {
        return dbModel.getAllEvents();
    }

    @Override
    public List<Event> getUserEvents(User user) {
        return null;
    }

    @Override
    public void addGoingUser(User user, Event e) {
        dbModel.addGoingUser(user.getUsername(), e.getEvent_uuid());
    }

    @Override
    public void addInterestedUser(User user, Event e) {
        dbModel.addInterestedUser(user.getUsername(), e.getEvent_uuid());

    }
}
