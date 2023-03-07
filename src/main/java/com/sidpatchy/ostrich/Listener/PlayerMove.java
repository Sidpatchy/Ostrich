package com.sidpatchy.ostrich.Listener;

import com.sidpatchy.ostrich.ElytraBlockerRunnable;
import com.sidpatchy.ostrich.FlightBlockerRunnable;
import com.sidpatchy.ostrich.Ostrich;
import com.sidpatchy.ostrich.Util.GriefPrevention.Claims;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    private final Ostrich plugin;
    public PlayerMove(Ostrich plugin) {
        this.plugin = plugin;
    }

    public Claims claims = new Claims();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Prevent unnecessary code execution by checking if the player has moved blocks.
        if (event.getTo() != null && event.getFrom().getBlock().getLocation() == event.getTo().getBlock().getLocation()) {
            return;
        }

        if (player.isFlying()) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                    new FlightBlockerRunnable(plugin, player), 0);
        }
        if (player.isGliding()) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                    new ElytraBlockerRunnable(plugin, player), 0);
        }
    }
}
