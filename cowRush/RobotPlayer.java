package cowRush;

import java.util.Random;

import battlecode.common.*;
public class RobotPlayer {

	static Random rand  = new Random();
	public static RobotController rc;
	static Direction directions[] = Direction.values();
	static int possibleDirections[] = new int[]{0,1,-1,2,-2,3,-3};
	
	public static void run(RobotController myRc) {

		rc = myRc;
        rand.setSeed(rc.getRobot().getID());
		while(true) {
			try{
				if (rc.getType() == RobotType.HQ) {
					runHeadQuarters();
				}

				if (rc.getType() == RobotType.SOLDIER) {
					runSoldier();
				}

				rc.yield();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private static void runHeadQuarters() throws GameActionException {  //Headquarters Control
		Direction spawnDir = Direction.NORTH;
		// spawn a soldier			
		if(rc.isActive() && rc.canMove(spawnDir) && rc.senseRobotCount() < GameConstants.MAX_ROBOTS){ 
			rc.spawn(spawnDir);
		}
		int editingChannel =  (Clock.getRoundNum()%2);
//		int usingChannel =  (Clock.getRoundNum()+1%2);
		rc.broadcast(editingChannel, 0);
		rc.broadcast(editingChannel+2, 0);
	}

	private static void runSoldier() throws GameActionException {// Soldier Control
		//this bit of code is used to assess how many byte codes are being used in each place
//		int currentBytecode = Clock.getBytecodeNum();
		tryToShoot();
//		rc.setIndicatorString(0, "used " + (Clock.getBytecodeNum() - currentBytecode) + " ByteCode");
		//TODO Communication
		//rc.setIndicatorString(0, "read ID: " + rc.readBroadcast(0));
		int editingChannel =  (Clock.getRoundNum()%2);
		int usingChannel =  ((Clock.getRoundNum()+1)%2);
		int runningTotal = rc.readBroadcast(editingChannel);
		rc.broadcast(editingChannel, runningTotal+1);
		
		
		MapLocation runningVectorTotal = HelperFunctions.intToLoc(rc.readBroadcast(editingChannel+2));
		rc.broadcast(editingChannel+2, HelperFunctions.locToInt(HelperFunctions.mapAdd(runningVectorTotal, rc.getLocation())));
		MapLocation averagePositionOfSwarm = HelperFunctions.mapDivide(HelperFunctions.intToLoc(rc.readBroadcast(usingChannel+2)),rc.readBroadcast(usingChannel));
		
		rc.setIndicatorString(0, ""+HelperFunctions.locToInt(averagePositionOfSwarm));
		//TODO Movement
		swarmMove(averagePositionOfSwarm);
	}


	private static void swarmMove(MapLocation averagePositionOfSwarm) throws GameActionException {
//		Direction randDir = directions[rand.nextInt(7)];
		Direction chosenDirection = rc.getLocation().directionTo(averagePositionOfSwarm);//needed for bad swarm movement
//		MapLocation closestEnemyPASTR;
//		for(int i = 0; i < rc.sensePastrLocations(rc.getTeam().opponent()).length; i++){
//			closesntEnemyPASTR = 
//		}
		if(rc.isActive()){
			if(rand.nextDouble()<0.5){ //go to swarm
				for(int directionalOffset:possibleDirections){
					int forwardInt = chosenDirection.ordinal();//needed for bad swarm movement
					Direction trialDir = directions[(forwardInt+directionalOffset+8)%8];//needed for bad swarm movement
//					Direction towardEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
					if(rc.canMove(trialDir)){
						rc.move(trialDir); //
//						BasicPathing.tryToMove(towardEnemy, true, rc, possibleDirections, directions);
						break;
					}
				}
			}
		}else{//move randomly
			Direction randDir = directions[rand.nextInt(7)];
			if(rc.isActive() && rc.canMove(randDir)){
				rc.move(randDir); //
			}
		}
		
	}

	private static void tryToShoot() throws GameActionException {

		//TODO Attacking
				Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,1000,rc.getTeam().opponent());
				if(enemyRobots.length > 0){
//					Robot enemy = enemyRobots[0];
					MapLocation[] robotLocations = new MapLocation[enemyRobots.length];
					for(int i=0; i<enemyRobots.length;i++){
						Robot enemy = enemyRobots[i];
						RobotInfo enemyInfo = rc.senseRobotInfo(enemy);
						
						robotLocations[i] = enemyInfo.location;
					}
					MapLocation closestEnemyLoc = HelperFunctions.findClosest(robotLocations, rc.getLocation());
 // use enemyInfo.location.getAllMapLocationsWithinRadiusSq(arg0, arg1)
					if(closestEnemyLoc.distanceSquaredTo(rc.getLocation()) < rc.getType().attackRadiusMaxSquared){
						if(rc.isActive()){
							rc.attackSquare(closestEnemyLoc);
						}
					}else{
						Direction towardClosest = rc.getLocation().directionTo(closestEnemyLoc);
						BasicPathing.tryToMove(towardClosest, true, rc, possibleDirections, directions);
						
					}
				}else{// there are no nearby enemies so build a PASTR
					if((rc.senseCowsAtLocation(rc.getLocation()) > 100) && (rand.nextDouble() < .001) && (rc.sensePastrLocations(rc.getTeam()).length < 2)){ //
						if(rc.isActive()){
							rc.construct(RobotType.PASTR);
						}
					}
				}

	}


}


