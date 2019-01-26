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
import java.util.ArrayList;

public class CreateWarp implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("warp command can only be used by player"); return false; }

        Player player = (Player) sender;

        if(!player.hasPermission("warsaril.createWarp")) { player.sendMessage(Main.noPermission(label)); return false;}

        if(args.length == 0)
        {
            player.sendMessage(Main.prefix + ChatColor.RED + "Il faut spécifier un nom pour le warp");
            return false;
        }

        String name = args[0];

        if(name.length() < 3)
        {
            player.sendMessage(Main.prefix + ChatColor.RED + "Le nom du warp doit contenir au moins 3 caractères");
            return false;
        }

        if(name.length() > 10)
        {
            player.sendMessage(Main.prefix + ChatColor.RED + "Le nom du warp doit contenir au maximun 10 caractères");
            return false;
        }

        try {
            Connection sql = Main.getDataSource().getConnection();
            ResultSet rs = sql.prepareStatement("SELECT * FROM warps WHERE type = 'warp'").executeQuery();

            ArrayList<String> warps = new ArrayList<>();

            boolean next = rs.next();

            if (!next) {
                player.sendMessage(Main.prefix + ChatColor.RED + "Il n\'y a pas de warp défini");
                return false;
            }

            while(next)
            {
                warps.add(rs.getString("name").toLowerCase());

                next = rs.next();
            }

            if(warps.contains(name.toLowerCase()))
            {
                player.sendMessage(Main.prefix + ChatColor.RED + "Un warp est déja connu sous ce nom");
                return false;
            }

            String worldName = player.getWorld().getName();

            Location playerLocation = player.getLocation();
            String xyz = (playerLocation.getBlockX() + 0.5) + "," + (playerLocation.getBlockY() + 0.2) + "," + (playerLocation.getBlockZ() + 0.5);

            sql.prepareStatement("INSERT INTO warps (name, type, world, xyz) VALUES ('" + name + "', 'warp', '" + worldName + "', '" + xyz + "')").execute();

            player.sendMessage(Main.prefix + ChatColor.GREEN + "Le warp " + ChatColor.YELLOW + name + ChatColor.GREEN + " a été créé");
            Main.getLog().warning( player.getName() + " a créé le warp " + name);
        } catch (SQLException e)
        {
            player.sendMessage(Main.prefix + ChatColor.RED + "Une erreur s'est produite");
            e.printStackTrace();
        }

        return false;
    }
}
