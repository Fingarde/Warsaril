package fr.fingarde.warsaril;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.fingarde.warsaril.commands.SetSpawn;
import fr.fingarde.warsaril.commands.Spawn;
import fr.fingarde.warsaril.commands.Warp;
import fr.fingarde.warsaril.listeners.MovementEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin
{

    public static final String prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Warsaril" + ChatColor.GRAY + "] " + ChatColor.RESET;

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

    /* ================================ GETTER ================================ */

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static Logger getLog() {
        return logger;
    }

    public static Main getInstance() {
        return instance;
    }

    public static String noPermission(String cmd)
    {
        return ChatColor.RED + "Vous n'avez pas la permisison de faire " + ChatColor.BOLD + "/" + cmd;
    }

    /* ================================ FUNCTIONS ================================ */

    private void connectDataBase()
    {
        HikariConfig config = new HikariConfig();

        config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("port", 3306);
        config.addDataSourceProperty("databaseName", "warsaril");
        config.addDataSourceProperty("user", "admin");
        config.addDataSourceProperty("password", "test");
        config.addDataSourceProperty("useSSL", false);
        config.addDataSourceProperty("autoReconnect", true);

        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        dataSource = new HikariDataSource( config );
    }

    private void registerCommands()
    {
        getCommand("setspawn").setExecutor(new SetSpawn());
        getCommand("spawn").setExecutor(new Spawn());
        getCommand("warp").setExecutor(new Warp());
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
