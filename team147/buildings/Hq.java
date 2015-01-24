package team147.buildings;

import team147.BaseRobot;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Hq extends BaseRobot {
	int numBeaversAlive;

	public Hq(RobotController myRC) throws GameActionException {
		super(myRC);
		defaultSpawnSetup();
		while (true) {
			defaultTurnSetup();
			messaging.determineAllUnitCounts();
			attackLeastHealthyEnemy();
			messaging.broadCastHQPressure();
			transferSupply();
			defaultTurnEndAction();
		}
	} // end of HQ constructor

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

	}

	@Override
	public void defaultExploreAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void defaultTurnSetup() throws GameActionException {
		numBeaversAlive = messaging.getNumBeavers();
		if (numBeaversAlive < 2)
			spawnRobot(RobotType.BEAVER);
	}

	@Override
	public void defaultSpawnSetup() throws GameActionException {
		messaging.setRallyPoint(getDefaultRallyPoint(enemyHQLoc));
	}

	@Override
	public void defaultTurnEndAction() throws GameActionException {
		// TODO Auto-generated method stub
		rc.yield();
	}

	@Override
	public void defaultRallyAction() throws GameActionException {
		// TODO Auto-generated method stub

	}
	

} // end of HQ class