package com.mathandcs.kino.effectivejava.highavailability.leaderelection;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A combination of a leader address and leader id.
 */
@Data
@AllArgsConstructor
public class LeaderAddressAndId {

    private final String leaderAddress;
    private final UUID   leaderId;

    @Override
    public String toString() {
        return "LeaderAddressAndId (" + leaderAddress + " / " + leaderId + ')';
    }
}
