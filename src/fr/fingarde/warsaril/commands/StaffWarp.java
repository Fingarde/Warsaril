package fr.fingarde.warsaril.commands;

import fr.fingarde.warsaril.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R2.command.CraftBlockCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Warp implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        try {
            Connection sql = Main.getDataSource().getConnection();
            ResultSet rs = sql.prepareStatement("SELECT * FROM warps WHERE type = 'warp';").executeQuery();

            HashMap<String, Location> warps = new HashMap<>();

            boolean next = rs.next();

            if (!next) {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Il n\'y a pas de warp défini");
                return false;
            }

            while(next)
            {
                String name = rs.getString("name").toLowerCase();

                if((!sender.hasPermission("warsaril.warp." + name)) && !(sender.hasPermission("warsaril.warp.*"))) { next = rs.next();continue; }

                String[] xyz = rs.getString("xyz").split(",");

                Location loc = new Location(Bukkit.getWorld(rs.getString("world")), Float.valueOf(xyz[0]), Float.valueOf(xyz[1]), Float.valueOf(xyz[2]));

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
                sender.sendMessage(Main.prefix + ChatColor.GRAY + "Les warps sont: " + ChatColor.YELLOW + warpList.replaceAll(",", ChatColor.GRAY + "," + ChatColor.YELLOW));
                return false;
            }


            Player player = null;

            if(args.length > 1)
            {
                if(args[1].equalsIgnoreCase("@p"))
                {
                   if(sender instanceof CraftBlockCommandSender)
                   {
                       double distance = 500;
                       Location loc = ((CraftBlockCommandSender) sender).getBlock().getLocation();

                       for(Entity entity :((CraftBlockCommandSender) sender).getBlock().getWorld().getNearbyEntities(loc, 50 , 50 , 50))
                       {
                           if(!(entity instanceof Player)) continue;

                           double entityDistance = entity.getLocation().distance(loc);

                           if(entityDistance < distance)
                           {
                               player = (Player) entity;
                               distance = entityDistance;
                           }
                       }
                   }
                }else
                {
                    for(Player onlinePlayer : Bukkit.getOnlinePlayers())
                    {
                        if(onlinePlayer.getName().equalsIgnoreCase(args[1]))
                        {
                            player = onlinePlayer;
                            break;
                        }
                    }
                }
            }
            else {
                if(sender instanceof Player)
                {
                    player = (Player) sender;
                }
            }

            if(player == null)
            {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Aucun joueur n'est spécifié");
                return false;
            }

            if(warps.containsKey(args[0].toLowerCase()))
            {
                player.teleport(warps.get(args[0].toLowerCase()));

                player.sendMessage(Main.prefix + ChatColor.GREEN + "Téléportation vers " + args[0]);

                if(!sender.getName().equalsIgnoreCase(player.getName())) sender.sendMessage(Main.prefix + ChatColor.GREEN + "Téléportation de " + player.getName() + " au warp " + args[0]);
            }else
            {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Aucun warp ne porte ce nom");
                return false;
            }

        } catch (SQLException e)
        {
            sender.sendMessage(Main.prefix + ChatColor.RED + "Une erreur s'est produite");
            e.printStackTrace();
        }

        return false;
    }
}
