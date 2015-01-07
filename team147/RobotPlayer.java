package team147;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotPlayer {

	private static RobotController rc;
	private static Random rand;
	private static Direction currentDirection;
	private static MapLocation enemyHQLoc;

	public static void run(RobotController myRC) {

		rc = myRC;
		rand = new Random(rc.getID());
		currentDirection = randomDirection();
		try {
			switch (rc.getType()) {
			case AEROSPACELAB:
				aerospacelab();
				break;
			case BARRACKS:
				barracks();
				break;
			case BASHER:
				basher();
				break;
			case BEAVER:
				beaver();
				break;
			case COMMANDER:
				commander();
				break;
			case COMPUTER:
				computer();
				break;
			case DRONE:
				drone();
				break;
			case HANDWASHSTATION:
				handwashstation();
				break;
			case HELIPAD:
				helipad();
				break;
			case HQ:
				hq();
				break;
			case LAUNCHER:
				launcher();
				break;
			case MINER:
				miner();
				break;
			case MINERFACTORY:
				minerfactory();
				break;
			case MISSILE:
				missile();
				break;
			case SOLDIER:
				soldier();
				break;
			case SUPPLYDEPOT:
				supplydepot();
				break;
			case TANK:
				tank();
				break;
			case TANKFACTORY:
				tankfactory();
				break;
			case TECHNOLOGYINSTITUTE:
				technologyinstitute();
				break;
			case TOWER:
				tower();
				break;
			case TRAININGFIELD:
				trainingfield();
				break;
			default:
				break;
			}
		} catch (GameActionException ge) {
			ge.printStackTrace();
		}
	}

	private static Direction randomDirection() {
		return Direction.values()[rand.nextInt(8)];
	}

	private static void moveAround() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection)) {
				if (rand.nextInt(100) > 10) {
					rc.move(currentDirection);
				} else {
					currentDirection = currentDirection.rotateRight();
					if (rc.canMove(currentDirection)) {
						rc.move(currentDirection);
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
			}
		}
	}

	private static void attackEnemyZero() throws GameActionException {
		if (rc.isWeaponReady()) {
			RobotInfo[] enemies = rc.senseNearbyRobots(
					rc.getType().attackRadiusSquared, rc.getTeam().opponent());
			if (1 <= enemies.length) {
				rc.attackLocation(enemies[0].location);
			}
		}
	}

	private static void trainingfield() {
		while (true) {
			rc.yield();
		}

	}

	private static void technologyinstitute() {
		while (true) {
			rc.yield();
		}

	}

	private static void tower() throws GameActionException {
		while (true) {
			attackEnemyZero();
			rc.yield();
		}

	}

	private static void tankfactory() {
		while (true) {
			rc.yield();
		}

	}

	private static void tank() {
		while (true) {
			rc.yield();
		}

	}

	private static void supplydepot() {
		while (true) {
			rc.yield();
		}

	}

	private static void soldier() throws GameActionException {
		while (true) {
			attackEnemyZero();
			moveTowardsHQ();
			rc.yield();
		}

	}

	private static void missile() {
		while (true) {
			rc.yield();
		}

	}

	private static void minerfactory() throws GameActionException {
		while (true) {
			spawnRobot(RobotType.MINER);
		}

	}

	private static void miner() throws GameActionException {
		while (true) {
			mine();
			moveAround();
			attackEnemyZero();
			rc.yield();
		}
	}

	private static void launcher() {
		while (true) {
			rc.yield();
		}

	}

	private static void hq() throws GameActionException {
		while (true) {
			attackEnemyZero();
			spawnRobot(RobotType.BEAVER);
		}
	}

	private static void spawnRobot(RobotType type) throws GameActionException {
		if (rc.hasSpawnRequirements(type) && rc.isCoreReady()) {
			for (Direction d : Direction.values()) {
				if (rc.canSpawn(d, type)) {
					rc.spawn(d, type);
					break;
				}
			}
		}
	}

	private static void helipad() {
		while (true) {
			rc.yield();
		}

	}

	private static void handwashstation() {
		while (true) {
			rc.yield();
		}

	}

	private static void drone() {
		while (true) {
			rc.yield();
		}

	}

	private static void computer() {
		while (true) {
			rc.yield();
		}

	}

	private static void commander() {
		while (true) {
			rc.yield();
		}

	}

	private static void beaver() throws GameActionException {
		while (true) {
			mine();
			moveAround();
			build(Clock.getRoundNum() > 350 ? RobotType.BARRACKS
					: RobotType.MINERFACTORY);
		}
	}

	private static void build(RobotType building) throws GameActionException {
		if (rc.hasBuildRequirements(building) && rc.isCoreReady()) {
			for (int i = 0; i < 8; i++) {
				if (rc.canBuild(currentDirection, building)) {
					rc.build(currentDirection, building);
					break;
				} else
					currentDirection = currentDirection.rotateRight();
			}
		}
	}

	private static void mine() throws GameActionException {
		int mineMax = (rc.getType() == RobotType.MINER ? GameConstants.MINER_MINE_MAX
				: GameConstants.BEAVER_MINE_MAX);

		if (rc.isCoreReady() && rc.senseOre(rc.getLocation()) > mineMax) {
			rc.mine();
		}
	}

	private static void basher() throws GameActionException {
		while (true) {
			moveTowardsHQ();
			rc.yield();
		}

	}

	private static void moveTowardsHQ() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection)) {
				if (rand.nextInt(100) > 10) {
					rc.move(currentDirection);
				} else {
					if (enemyHQLoc == null)
						enemyHQLoc = rc.senseEnemyHQLocation();

					currentDirection = rc.getLocation().directionTo(enemyHQLoc);
					if (rc.canMove(currentDirection)) {
						rc.move(currentDirection);
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
			}
		}
	}

	private static void barracks() throws GameActionException {
		while (true) {
			spawnRobot(rand.nextDouble() > .5 ? RobotType.BASHER
					: RobotType.SOLDIER);
		}

	}

	private static void aerospacelab() {
		while (true) {
			rc.yield();
		}

	}

}