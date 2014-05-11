package com.verymmog.network.leader;

import com.verymmog.network.DataPlayer;
import com.verymmog.network.manager.DataPlayerComparatorInterface;

public class DistanceComparator implements DataPlayerComparatorInterface {
    private long origX, origY;

    public DistanceComparator(long origX, long origY) {
        this.origX = origX;
        this.origY = origY;
    }

    @Override
    public int compare(DataPlayer o1, DataPlayer o2) {
        long d1 = distance(o1);
        long d2 = distance(o2);

        if (d1 == d2) {
            return 0;
        } else if (d1 < d2) {
            return -1;
        } else {
            return 1;
        }
    }

    private long distance(DataPlayer player) {
        long varx = origX - player.x;
        long vary = origY - player.y;

        return (long) Math.sqrt(varx * varx + vary * vary);
    }
}
