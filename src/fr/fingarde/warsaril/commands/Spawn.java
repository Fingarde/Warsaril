package fr.fingarde.warsaril.commands;

import fr.fingarde.warsaril.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Spawn implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(!(sender instanceof Player)) { sender.sendMessage("spawn command can only be used by player"); return false;}

        Player player = (Player) sender;

        if(!player.hasPermission("warsaril.spawn")) { player.sendMessage(Main.noPermission(label)); return false;}

        try
        {
            Connection sql = Main.getDataSource().getConnection();
            ResultSet rs = sql.prepareStatement("SELECT * FROM warps WHERE type = 'spawn' and name = 'spawn'").executeQuery();

            if(!rs.next())
            {
                player.sendMessage(Main.prefix + ChatColor.RED + "Il n\'y a pas de spawn défini");
                return false;
            }

            String[] xyz = rs.getString("xyz").split(",");

            Location loc = new Location(Bukkit.getWorld(rs.getString("world")), Integer.valueOf(xyz[0]) + 0.5, Integer.valueOf(xyz[1]) + 0.5, Integer.valueOf(xyz[2]) + 0.5);

            player.teleport(loc);

            player.sendMessage(Main.prefix + ChatColor.GREEN + "Téléportation au spawn");
        } catch (SQLException e)
        {
            player.sendMessage(Main.prefix + ChatColor.RED + "Une erreur s'est produite");
            e.printStackTrace();
        }

        return false;
    }
}
