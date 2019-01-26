package fr.fingarde.warsaril.commands;

import fr.fingarde.warsaril.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SetSpawn implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(!(sender instanceof Player)) { sender.sendMessage("setSpawn command can only be used by player"); return false;}

        Player player = (Player) sender;

        if((!player.isOp()) && (!player.hasPermission("warsaril.setSpawn"))) { player.sendMessage(Main.noPermission(label)); return false;}

        try
        {
            String worldName = player.getWorld().getName();

            Location playerLocation = player.getLocation();
            String xyz = playerLocation.getBlockX() + "," + playerLocation.getBlockY() + "," + playerLocation.getBlockZ();

            Connection sql = Main.getDataSource().getConnection();
            ResultSet rs = sql.prepareStatement("SELECT * FROM warps WHERE type = 'spawn' and name = 'spawn'").executeQuery();

            if(! rs.next())
            {
                sql.prepareStatement("INSERT INTO warps (name, type, world, xyz) VALUES ('spawn', 'spawn', '" + worldName + "', '" + xyz + "')").execute();
            }else
            {
                sql.prepareStatement("UPDATE warps SET world = '" + worldName + "', xyz = '" + xyz + "' WHERE (`name`='spawn') AND (`type`='spawn') LIMIT 1").execute();
            }

            player.sendMessage(Main.prefix + ChatColor.GREEN + "Le spawn a été defini");
            Main.getLog().warning( player.getName() + " a défini le lieu de spawn");

        } catch (SQLException e)
        {
            player.sendMessage(Main.prefix + ChatColor.RED + "Une erreur s'est produite");
            e.printStackTrace();
        }

        return false;
    }
}
