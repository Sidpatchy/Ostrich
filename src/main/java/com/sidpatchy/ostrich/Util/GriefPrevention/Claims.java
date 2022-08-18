package com.sidpatchy.ostrich.Util.GriefPrevention;

import com.sidpatchy.ostrich.Ostrich;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Claims {
    /**
     * Get a claim object from a player.
     *
     * @param player the player to check
     * @return the claim the player is currently in, regardless of height limits.
     * if the player is not in a claim, this will return null.
     */
    public Claim getClaimFromPlayer(Player player) {
        DataStore dataStore = Ostrich.getDataStore();
        Location location = player.getLocation();
        PlayerData playerData = dataStore.getPlayerData(player.getUniqueId());

        return dataStore.getClaimAt(location, true, null);
    }

    /**
     * Check if a player is in a claim.
     *
     * @param player the player to check
     * @return true if the player is in a claim.
     */
    public boolean isPlayerInClaim(Player player) {
        return getClaimFromPlayer(player) != null;
    }

    /**
     * Check if the player is a member of a claim.
     *
     * @param player player to check
     * @param claim  claim to test against
     * @return true if player is allowed to access claim.
     */
    public boolean isPlayerMemberInClaim(Player player, Claim claim) {
        return claim.checkPermission(player.getUniqueId(), ClaimPermission.Access, null) == null;
    }
}
