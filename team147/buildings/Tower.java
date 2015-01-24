package team147.buildings;

import team147.BaseRobot;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.world.signal.BroadcastSignal;

public class Tower extends BaseRobot {
	private int towerNumber;
	private int maxNumEnemiesSeen = 0;

	public Tower(RobotController myRC) throws GameActionException {
		super(myRC);
		towerNumber = getTowerNumberByMapLocation(rc.getLocation());
		int numEnemiesInSight = 0;

		while (true) {
			attackEnemyZero();
			numEnemiesInSight = rc.senseNearbyRobots(attackRadiusSquared,
					enemyTeam).length;
			if (numEnemiesInSight > maxNumEnemiesSeen) {
				messaging.setTowerPressure(numEnemiesInSight, towerNumber);
				maxNumEnemiesSeen = numEnemiesInSight;
			}

			rc.yield();
		}
	}

	private int getTowerNumberByMapLocation(MapLocation location) {
		MapLocation towers[] = rc.senseTowerLocations();
		MapLocation myLoc = rc.getLocation();

		for (int i = 0; i < towers.length; i++) {
			if (towers[i].equals(myLoc))
				return i;
		}

		return 0;
	}

	@Override
	public void defaultPanicAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void defaultAttackAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void defaultDefendAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void defaultEconAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defaultExploreAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defaultTurnSetup() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defaultSpawnSetup() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defaultTurnEndAction() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defaultRallyAction() throws GameActionException {
		// TODO Auto-generated method stub

	}
}