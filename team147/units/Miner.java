package team147.units;

import team147.BaseRobot;
import team147.util.statemachines.EconStateMachine;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Miner extends BaseRobot {
	EconStateMachine stateMachine;

	public Miner(RobotController myRC) throws GameActionException {
		super(myRC);
		messaging.incrementNumMinersSpawned();
		stateMachine = new EconStateMachine(this);

		defaultSpawnSetup();
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
			rc.yield();
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
		mine();
		safeMoveTowardOre();
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
		transferSupply();
		rc.yield();
	}

	@Override
	public void defaultRallyAction() throws GameActionException {
		// TODO Auto-generated method stub

	}
}