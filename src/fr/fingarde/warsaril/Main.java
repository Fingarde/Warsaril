package fr.fingarde.warsaril;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.fingarde.warsaril.commands.SetSpawn;
import fr.fingarde.warsaril.commands.Spawn;
import fr.fingarde.warsaril.listeners.MovementEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin
{

    private final String DataBaseAdress = "localhost:3306";
    private final String DataBase = "warsaril";

    private static Main instance;
    private static Logger logger;

    private static HikariDataSource dataSource;


    /* ================================ ON ENABLE ================================ */

    public void onEnable()
    {

        logger = getLogger();
        instance = this;

        connectDataBase();

        registerEvents();
        registerCommands();
    }


    /* ================================ FUNCTIONS ================================ */

    private void connectDataBase()
    {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl( "jdbc:mysql://" + DataBaseAdress+ "/" + DataBase );

        config.setUsername("admin");
        config.setPassword("test");

        config.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource = new HikariDataSource( config );
    }

    private void registerCommands()
    {

        getCommand("spawn").setExecutor(new Spawn());
        getCommand("setspawn").setExecutor(new SetSpawn());
    }

    private void registerEvents()
    {
      registerEvent(new MovementEvent());
    }

    private void registerEvent(Listener Class)
    {
        getServer().getPluginManager().registerEvents(Class, this);
    }
}
