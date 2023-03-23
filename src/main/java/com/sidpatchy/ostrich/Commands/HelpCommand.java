package com.sidpatchy.ostrich.Commands;

import com.sidpatchy.albatross.File.AlbatrossLanguageManager;
import com.sidpatchy.ostrich.Ostrich;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;

public class HelpCommand {

    public static void getHelp(Ostrich plugin, CommandSender sender) throws IOException, InvalidConfigurationException {

        AlbatrossLanguageManager languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);

        String ostrichHelp;
        String ostrichInfo;
        String ostrichReload;

        if (sender instanceof Player player) {
            ostrichHelp = languageManager.getLocalizedString("ostrichHelp", player);
            ostrichInfo = languageManager.getLocalizedString("ostrichInfo", player);
            ostrichReload = languageManager.getLocalizedString("ostrichReload", player);
        }
        else {
            ostrichHelp = languageManager.getFallbackLocaleString("ostrichHelp");
            ostrichInfo = languageManager.getFallbackLocaleString("ostrichInfo");
            ostrichReload = languageManager.getFallbackLocaleString("ostrichReload");
        }

        sender.sendMessage(ChatColor.WHITE + "------------------- " + ChatColor.DARK_AQUA + "Ostrich" + ChatColor.WHITE + " -------------------");
        sender.sendMessage(ChatColor.DARK_AQUA + "/ostrich help" + ChatColor.WHITE + " - " + ostrichHelp);
        sender.sendMessage(ChatColor.DARK_AQUA + "/ostrich info" + ChatColor.WHITE + " - " + ostrichInfo);
        sender.sendMessage(ChatColor.DARK_AQUA + "/ostrich reload" + ChatColor.WHITE + " - " + ostrichReload);
    }
}
