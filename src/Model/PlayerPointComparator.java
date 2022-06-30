package Model;

import java.util.Comparator;

public class PlayerPointComparator implements Comparator<Player> {

    @Override
    public int compare(Player o1, Player o2) {
        if (o1.getPoint() > o2.getPoint())
            return 1;
        else if(o1.getPoint() < o2.getPoint())
            return -1;
        else
            return 0;
    }
}
