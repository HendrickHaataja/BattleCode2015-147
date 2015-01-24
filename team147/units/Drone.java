package team147.units;

import team147.BaseRobot;
import team147.util.statemachines.AttackStateMachine;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Drone extends BaseRobot {
	private AttackStateMachine stateMachine;

	public Drone(RobotController myRC) throws GameActionException {
		super(myRC);
		stateMachine = new AttackStateMachine(this);

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
			case EXPLORE:
				defaultExploreAction();
				break;
			case PANIC:
				defaultPanicAction();
				break;
			case RALLY:
				defaultRallyAction();
				break;
			default:
				break;
          }

			defaultTurnEndAction();
			stateMachine.sendStateMessages();
			rc.yield();
		}
	}

	@Override
	public void defaultPanicAction() throws GameActionException {
		moveToSafety();
	}

	@Override
	public void defaultRallyAction() throws GameActionException {
		attackEnemyZero();
		moveTowardDestination(messaging.getRallyPoint());
	}

	@Override
	public void defaultAttackAction() throws GameActionException {
		attackLeastHealthyEnemy();
		if (rc.getRoundLimit() - Clock.getRoundNum() > 360)
			safeMoveTowardDestination(getClosestTowerLocation());
		else
			moveTowardDestination(getClosestTowerLocation());
	}

	@Override
	public void defaultDefendAction() throws GameActionException {
		attackLeastHealthyEnemy();
		rallyToTower();
	}

	@Override
	public void defaultEconAction() throws GameActionException {
	}

	@Override
	public void defaultExploreAction() throws GameActionException {
		safeMoveAround();
	}

	@Override
	public void defaultTurnSetup() throws GameActionException {
	}

	@Override
	public void defaultSpawnSetup() throws GameActionException {

	}

	@Override
	public void defaultTurnEndAction() throws GameActionException {
		transferSupply();
		rc.yield();
	}

}