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
import java.util.HashMap;
import java.util.Map;

public class Warp implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("warp command can only be used by player"); return false; }

        Player player = (Player) sender;

        try {
            Connection sql = Main.getDataSource().getConnection();
            ResultSet rs = sql.prepareStatement("SELECT * FROM warps WHERE type = 'warp'").executeQuery();

            HashMap<String, Location> warps = new HashMap<>();

            boolean next = rs.next();

            if (!next) {
                player.sendMessage(Main.prefix + ChatColor.RED + "Il n\'y a pas de warp défini");
                return false;
            }

            while(next)
            {
                String name = rs.getString("name").toLowerCase();

                if((!player.hasPermission("warsaril.warp." + name)) && !(player.hasPermission("warsaril.warp.*"))) { next = rs.next();continue; }

                String[] xyz = rs.getString("xyz").split(",");

                Location loc = new Location(Bukkit.getWorld(rs.getString("world")), Integer.valueOf(xyz[0]) + 0.5, Integer.valueOf(xyz[1]) + 0.5, Integer.valueOf(xyz[2]) + 0.5);

                warps.put(name, loc);

                next = rs.next();
            }

            String warpList = "";

            for(Map.Entry<String, Location> warp : warps.entrySet())
            {
                warpList += ", " + warp.getKey();
            }
            warpList = warpList.substring(2);

            if(args.length == 0) {
                player.sendMessage(Main.prefix + ChatColor.GRAY + "Les warps sont: " + ChatColor.YELLOW + warpList.replaceAll(",", ChatColor.GRAY + "," + ChatColor.YELLOW));
                return false;
            }

            if(warps.containsKey(args[0].toLowerCase()))
            {
                player.teleport(warps.get(args[0].toLowerCase()));
                player.sendMessage(Main.prefix + ChatColor.GREEN + "Téléportation vers " + args[0]);
                return false;
            }else
            {
                player.sendMessage(Main.prefix + ChatColor.RED + "Aucun warp ne porte ce nom");
                return false;
            }

        } catch (SQLException e)
        {
            player.sendMessage(Main.prefix + ChatColor.RED + "Une erreur s'est produite");
            e.printStackTrace();
        }

        return false;
    }
}
