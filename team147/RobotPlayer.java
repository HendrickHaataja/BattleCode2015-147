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
		// initialize random number generator
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
	} // end of run method

	private static Direction randomDirection() {
		return Direction.values()[rand.nextInt(8)];
	} // end of randomDirection method

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
	} // end of moveAround method

	private static void attackEnemyZero() throws GameActionException {
		if (rc.isWeaponReady()) {
			RobotInfo[] enemies = rc.senseNearbyRobots(
					rc.getType().attackRadiusSquared, rc.getTeam().opponent());
			if (1 <= enemies.length) {
				if(rc.canAttackLocation(enemies[0].location)){
					rc.attackLocation(enemies[0].location);
				}
			}
		}
	} // end of attackEnemyZero method

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
			transferSupply();
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
			//transferSupply();
			rc.yield();
		}
	} // end of miner method

	private static void launcher() {
		while (true) {
			rc.yield();
		}

	}

	private static void hq() throws GameActionException {
		while (true) {
			attackEnemyZero();
			spawnRobot(RobotType.BEAVER);
			transferSupply();
			rc.yield();
		}
	} // end of hq method

	private static void spawnRobot(RobotType type) throws GameActionException {
		if (rc.hasSpawnRequirements(type) && rc.isCoreReady()) {
			for (Direction d : Direction.values()) {
				if (rc.canSpawn(d, type)) {
					rc.spawn(d, type);
					break;
				}
			}
		}
	} // end of spawnRobot method

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
			if(rand.nextInt(100) > 50) {
			build(Clock.getRoundNum() > 350 ? RobotType.BARRACKS
					: RobotType.MINERFACTORY);
			} else {
				buildSupplyDepot();
			}
			transferSupply();
			rc.yield();
		}
	} // end of beaver method

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
	} // end of build method

	private static void mine() throws GameActionException {
		int mineMax = (rc.getType() == RobotType.MINER ? GameConstants.MINER_MINE_MAX
				: GameConstants.BEAVER_MINE_MAX);

		if (rc.isCoreReady() && rc.senseOre(rc.getLocation()) > mineMax) {
			rc.mine();
		}
	} // end of mine method

	private static void basher() throws GameActionException {
		while (true) {
			moveTowardsHQ();
			//transferSupply();
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
	} // end of moveTowardsHQ method

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
	
	private static void buildSupplyDepot() throws GameActionException {
		MapLocation currentLoc = rc.getLocation();
		int distanceFromHQ = currentLoc.distanceSquaredTo(rc.senseHQLocation());
		if (Clock.getRoundNum() < 500) {
			if (rand.nextInt(100) < 10) {
				if (distanceFromHQ < 40 && distanceFromHQ > 20) {
					build(RobotType.SUPPLYDEPOT);
				}
			}
		}
	} // end of buildSupplyDepot method
	
	// transfer supply to other robots that have less supply
	private static void transferSupply() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(), GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation transferDestination = null;
		if (nearbyAllies.length > 0) {
		for (RobotInfo robot : nearbyAllies) {
			if (robot.supplyLevel < lowestSupply) {
				lowestSupply = robot.supplyLevel;
				transferAmount = ((rc.getSupplyLevel() - robot.supplyLevel)/2);
				transferDestination = robot.location;
			}
		  }
		}
		if (transferDestination != null) {
			int transferDistance = transferDestination.distanceSquaredTo(rc.getLocation());
			if (transferDistance <= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
				rc.transferSupplies((int)transferAmount, transferDestination);
		}
	  }
	} // end of transferSupply method
	
	// this method isn't being used, but could be used for efficient direction changing
	private static int directionNum(Direction d) {
		switch(d) {
		case NORTH:
			return 0;
		case NORTH_WEST: 
			return 1;
		case WEST:
			return 2;
		case SOUTH_WEST:
			return 3;
		case SOUTH:
			return 4;
		case SOUTH_EAST:
			return 5;
		case EAST:
			return 6;
		case NORTH_EAST:
			return 7;
		default:
			return -1;
		}
	} // end of directionNum method
} // end of RobotPlayer class
