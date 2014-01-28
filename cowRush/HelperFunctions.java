package cowRush;

import battlecode.common.MapLocation;

public class HelperFunctions {
	
	public static MapLocation mapAdd(MapLocation m1, MapLocation m2){
		return new MapLocation(m1.x + m2.x, m1.y + m2.y);

	}

	public static MapLocation mapDivide(MapLocation bigM, int divisor){
		return new MapLocation(bigM.x/divisor,bigM.y/divisor);
	}

	public static int locToInt(MapLocation m) {
		return (m.x * 100 + m.y);
	}

	public static MapLocation intToLoc(int i){
		return new MapLocation (i/100, i%100);
	}

	public static MapLocation findClosest(MapLocation[] manyLocs, MapLocation point){
		int closestDist = 10000;
		int challengerDist = closestDist;
		MapLocation closestLoc = null;
		for(MapLocation m:manyLocs){
			challengerDist = point.distanceSquaredTo(m);
			if(challengerDist<closestDist){
				closestDist = challengerDist;
				closestLoc = m;
			}
		}
		return closestLoc;
	}

}
