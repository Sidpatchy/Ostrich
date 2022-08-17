package com.sidpatchy.ostrich;

import com.sidpatchy.albatross.File.AlbatrossLanguageManager;
import com.sidpatchy.ostrich.Util.GriefPrevention.Claims;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FlightBlockerRunnable implements Runnable{
    private final Ostrich plugin;
    private final Player player;

    public FlightBlockerRunnable(Ostrich plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {

        AlbatrossLanguageManager languageManager = new AlbatrossLanguageManager("eng", plugin);

        StateFlag regionFlightFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getFlightFlagName());

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);


        // This took an embarrassing amount of time to figure out because I was initially forgetting to check if the
        // player was even in a claim.
        if (set.testState(localPlayer, regionFlightFlag)) {
            // Flight is allowed in the region
            if (plugin.isGriefPreventionEnabled()) {
                Claims claims = new Claims();

                if (claims.isPlayerInClaim(player)) {
                    // Player is inside a claim

                    StateFlag claimFlightFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpFlightFlagName());
                    if (!set.testState(localPlayer, claimFlightFlag)) {
                        // Flight is not allowed in claims
                        disableFlight(languageManager.getLocalizedString("flightNotAllowedInClaims", player));
                    } //else, flight is allowed in claims, do nothing
                } // else, player is not inside a claim, do nothing
            } //else, flight is allowed
        }
        else {
            // Flight is not allowed in the region.
            if (plugin.isGriefPreventionEnabled()) {
                Claims claims = new Claims();

                StateFlag claimFlightFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpFlightFlagName());
                boolean flightAllowedInClaims = set.testState(localPlayer, claimFlightFlag);

                if (claims.isPlayerInClaim(player)) {
                    // Player is inside a claim
                    if (!flightAllowedInClaims) {
                        // Flight is not allowed in claims
                        disableFlight(languageManager.getLocalizedString("flightNotAllowedInClaims", player));
                    } //else, flight is allowed in claims, do nothing
                }
                else {
                    // Player is not inside a claim, disable flight.
                    if (flightAllowedInClaims) {
                        disableFlight(languageManager.getLocalizedString("flightAllowedInClaims", player));
                    }
                    else {
                        disableFlight(languageManager.getLocalizedString("flightNotAllowedInRegion", player));
                    }
                }
            }
            else {
                // gp is not enabled and flight is not allowed
                disableFlight(languageManager.getLocalizedString("flightNotAllowedInRegion", player));
            }
        }
    }

    private void disableFlight(String chatMessage) {

        AlbatrossLanguageManager languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);

        // Disable flight.
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setVelocity(new Vector(0, 0, 0));

        // Send chat message to player.
        player.sendMessage(chatMessage);
    }
}
