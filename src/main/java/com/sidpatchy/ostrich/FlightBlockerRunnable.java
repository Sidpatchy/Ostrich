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
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.IOException;

public class FlightBlockerRunnable implements Runnable {

    private final Ostrich plugin;
    private final Player player;
    private final AlbatrossLanguageManager languageManager;

    public FlightBlockerRunnable(Ostrich plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);
    }

    @Override
    public void run() {

        StateFlag regionFlightFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getFlightFlagName());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        if (player.hasPermission("ostrich.bypass.flight") || player.hasPermission("ostrich.bypass.*")) {
            return;
        }

        if (set.testState(localPlayer, regionFlightFlag)) {
            if (plugin.isGriefPreventionEnabled()) {
                try {
                    handlePlayerInGriefPreventionClaim(set, localPlayer);
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            if (plugin.isGriefPreventionEnabled()) {
                try {
                    handleNonFlightRegionWithGriefPrevention(set, localPlayer);
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    disableFlight(languageManager.getLocalizedString("flightNotAllowedInRegion", player));
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     *
     * @param set the region that the player is in
     * @param localPlayer a WorldGuard LocalPlayer
     */
    private void handlePlayerInGriefPreventionClaim(ApplicableRegionSet set, LocalPlayer localPlayer) throws IOException, InvalidConfigurationException {

        Claims claims = new Claims();

        if (!claims.isPlayerInClaim(player)) {
            return;
        }

        if (claims.getClaimFromPlayer(player).isAdminClaim() &&
                (player.hasPermission("ostrich.griefprevention.adminclaims.flight") || player.hasPermission("ostrich.griefprevention.adminclaims.*"))) {
            return;
        }

        StateFlag claimFlightFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpFlightFlagName());

        if (!set.testState(localPlayer, claimFlightFlag)) {
            disableFlight(languageManager.getLocalizedString("flightNotAllowedInClaims", player));
        }
        else {
            shouldPlayerReallyBeAllowToFlyInClaim(player, claims);
        }
    }

    /**
     *
     * @param set the region that the player is in
     * @param localPlayer a WorldGuard LocalPlayer
     */
    private void handleNonFlightRegionWithGriefPrevention(ApplicableRegionSet set, LocalPlayer localPlayer) throws IOException, InvalidConfigurationException {

        Claims claims = new Claims();
        StateFlag claimFlightFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpFlightFlagName());
        boolean flightAllowedInClaims = set.testState(localPlayer, claimFlightFlag);

        if (claims.isPlayerInClaim(player)) {
            handlePlayerInGriefPreventionClaim(set, localPlayer);
        }
        else {
            handlePlayerNotInGriefPreventionClaim(flightAllowedInClaims);
        }
    }


    /**
     * Determines what message should be sent if the player is not in a GriefPrevention claim.
     * @param flightAllowedInClaims
     */
    private void handlePlayerNotInGriefPreventionClaim(boolean flightAllowedInClaims) throws IOException, InvalidConfigurationException {

        if (flightAllowedInClaims) {
            disableFlight(languageManager.getLocalizedString("flightAllowedInClaims", player));
        } else {
            disableFlight(languageManager.getLocalizedString("flightNotAllowedInRegion", player));
        }
    }

    /**
     * Creative name, I know.
     *
     * Checks if player is required to be a member of a claim to fly in it.
     * @param player the player to check
     * @param claims an instance of Claims
     */
    private void shouldPlayerReallyBeAllowToFlyInClaim(Player player, Claims claims) throws IOException, InvalidConfigurationException {

        if (player.hasPermission("ostrich.griefprevention.requireClaimMembership.flight") || player.hasPermission("ostrich.griefprevention.requireClaimMembership.*")) {

            if (!claims.isPlayerMemberInClaim(player, claims.getClaimFromPlayer(player))) {
                disableFlight(languageManager.getLocalizedString("claimMembershipRequiredToFly", player));
            }
        }
    }

    /**
     * Disable flight on the user.
     *
     * @param chatMessage message to send to the player. Should already be translated by the language manager.
     */
    private void disableFlight(String chatMessage) {

        // Disable flight.
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setVelocity(new Vector(0, 0, 0));

        // Prevent fall damage if enabled.
        if (plugin.getFlightPreventFallDamage()) {
            int blockY = player.getWorld().getHighestBlockAt(player.getLocation()).getY();
            float distanceFromGround = (float) (player.getLocation().getY() - blockY);
            player.setFallDistance((distanceFromGround + 10) * -1);
        }

        // Send chat message to player.
        player.sendMessage(chatMessage);
    }
}