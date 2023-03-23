package com.sidpatchy.ostrich.Commands;

import com.sidpatchy.albatross.File.AlbatrossLanguageManager;
import com.sidpatchy.ostrich.Ostrich;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;

public class InfoCommand {

    public static void getInfo(Ostrich plugin, CommandSender sender) throws IOException, InvalidConfigurationException {

        AlbatrossLanguageManager languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);

        String infoVersion;
        String infoAuthor;
        String infoGitHub;

        if (sender instanceof Player player) {
            infoVersion = languageManager.getLocalizedString("infoVersion", player);
            infoAuthor = languageManager.getLocalizedString("infoAuthor", player);
            infoGitHub = languageManager.getLocalizedString("infoGitHub", player);
        }
        else {
            infoVersion = languageManager.getFallbackLocaleString("infoVersion");
            infoAuthor = languageManager.getFallbackLocaleString("infoAuthor");
            infoGitHub = languageManager.getFallbackLocaleString("infoGitHub");
        }

        sender.sendMessage(ChatColor.WHITE + "---------- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Ostrich" + ChatColor.WHITE + " ----------");
        sender.sendMessage(ChatColor.DARK_AQUA + "Version: " + ChatColor.WHITE + "v1.0.0-SNAPSHOT");
        sender.sendMessage(ChatColor.DARK_AQUA + "Author: " + ChatColor.WHITE + "Sidpatchy");
        sender.sendMessage(ChatColor.DARK_AQUA + "GitHub: " + ChatColor.WHITE + "https://github.com/Sidpatchy/Ostrich");
    }
}
