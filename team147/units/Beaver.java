package team147.units;

import team147.BaseRobot;
import team147.util.statemachines.EconStateMachine;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Beaver extends BaseRobot {
	private EconStateMachine stateMachine;
	MapLocation lastBuildingBuilt;

	public Beaver(RobotController myRC) throws GameActionException {
		super(myRC);
		stateMachine = new EconStateMachine(this);
		defaultSpawnSetup();
		lastBuildingBuilt = hQLoc;

		while (true) {
			stateMachine.updateState();
			defaultTurnSetup();
			switch (stateMachine.currentState) {
			case ATTACK:
				defaultAttackAction();
				break;
			case DEFEND:
				defaultDefendAction();
				break;
			case ECON:
				defaultEconAction();
				break;
			case EXPLORE:
				defaultExploreAction();
				break;
			case PANIC:
				defaultPanicAction();
				break;
			default:
				break;
			}
			defaultTurnEndAction();
			stateMachine.sendStateMessages();
		}
	}

	@Override
	public void defaultPanicAction() throws GameActionException {
		moveToSafety();
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
	public void defaultEconAction() throws GameActionException {
		if (getMoveableDirections() <= 3) {
			moveTowardDestination(lastBuildingBuilt);
		}
		RobotType building = getNeededBuilding();
		boolean success = build(building);
		if (!success) {
			moveTowardDestination(lastBuildingBuilt);
		}
		if (rc.isBuildingSomething()) {
			lastBuildingBuilt = rc.getLocation().add(currentDirection);
		}
		transferSupply();
	}

	@Override
	public void defaultExploreAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void defaultTurnSetup() throws GameActionException {
		
	}

	@Override
	public void defaultSpawnSetup() throws GameActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void defaultTurnEndAction() throws GameActionException {
		rc.yield();
	}

	@Override
	public void defaultRallyAction() throws GameActionException {
		// TODO Auto-generated method stub

	}
}