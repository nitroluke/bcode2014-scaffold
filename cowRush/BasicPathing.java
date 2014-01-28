package cowRush;

import java.util.ArrayList;

import battlecode.common.*;

public class BasicPathing {
static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();

public static boolean canMove(Direction dir, boolean selfAvoiding, RobotController rc){
	if(selfAvoiding){
		MapLocation resultingLocation = rc.getLocation().add(dir);
		for(int i = 0; i < snailTrail.size();i++){
			MapLocation m = snailTrail.get(i);
			if(!m.equals(rc.getLocation())){
				if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)){
					return false;
				}
			}
		}
	}// if you get through that entire part, then it is a good way to go.
	return rc.canMove(dir);
}

public static void tryToMove(Direction chosenDirection, boolean selfAvoiding, RobotController rc, int[] possibleDirections, Direction[] directions) throws GameActionException{
	while(snailTrail.size()<2)
		snailTrail.add(new MapLocation(-1,-1));
	if(rc.isActive()){
		snailTrail.remove(0);
		snailTrail.add(rc.getLocation());
		for(int directionalOffset:possibleDirections){
			int forwardInt = chosenDirection.ordinal();
			Direction trialDir = directions[(forwardInt+directionalOffset+8)%8];
			if(rc.canMove(trialDir)){
				rc.move(trialDir); //
				break;
			}
		}
	}
}
}
