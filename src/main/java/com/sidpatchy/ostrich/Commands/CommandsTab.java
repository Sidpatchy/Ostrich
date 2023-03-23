package com.sidpatchy.ostrich.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandsTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commandList = new ArrayList<String>();

        if (args.length == 1) {
            if (sender.hasPermission("ostrich.command")) {
                commandList.add("help");
                commandList.add("info");
            }
            if (sender.hasPermission("ostrich.reload")) {
                commandList.add("reload");
            }
        }
        else {
            return null;
        }
        return commandList;
    }
}
