package team147;

import java.util.ArrayList;
import java.util.Random;

import team147.util.Messenger;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

public abstract class BaseRobot {
	public RobotController rc;
	public Messenger messaging;
	public Random rand;
	public Direction currentDirection;
	public MapLocation enemyHQLoc, hQLoc;
	public Team enemyTeam, myTeam;
	public int attackRadiusSquared, sensorRadiusSquared;

	public BaseRobot(RobotController rc) {
		this.rc = rc;
		rand = new Random(rc.getID());
		messaging = new Messenger(rc);
		currentDirection = randomDirection();
		enemyHQLoc = rc.senseEnemyHQLocation();
		hQLoc = rc.senseHQLocation();
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();

		attackRadiusSquared = rc.getType().attackRadiusSquared;
		sensorRadiusSquared = rc.getType().sensorRadiusSquared;
	}

	public void attackEnemyTowerZero() throws GameActionException {
		MapLocation enemyTowers[] = rc.senseEnemyTowerLocations();
		if (rc.getLocation().distanceSquaredTo(enemyTowers[0]) <= rc.getType().attackRadiusSquared) {
			rc.attackLocation(enemyTowers[0]);
		} else {
			moveTowardDestination(enemyTowers[0]);
		}
	}

	public void rallyToTower() throws GameActionException {
		int towerPressures[] = messaging.getTowerPressure();
		int maxPressure = 0;
		int towerToDefend = -1;

		for (int i = 0; i < towerPressures.length; i++) {
			if (towerPressures[i] > maxPressure) {
				maxPressure = towerPressures[i];
				towerToDefend = i;
			}
		}
		MapLocation dest = towerToDefend != -1 ? getTowerLocationByNumber(towerToDefend)
				: getTowerLocationByNumber(rc.getID() % towerPressures.length);

		if (dest != null) {
			moveTowardDestination(dest);
		} else
			moveTowardDestination(hQLoc);

	}

	public MapLocation getDefaultRallyPoint(MapLocation attackLocation)
			throws GameActionException {
		MapLocation rallyPoint = hQLoc.add(hQLoc.directionTo(attackLocation),
				(int) (Math.sqrt(hQLoc.distanceSquaredTo(attackLocation)) / 4));
		return rallyPoint;
	} // end of getRallyPoint method

	public Direction randomDirection() {
		return Direction.values()[rand.nextInt(8)];
	} // end of randomDirection method

	public void moveAround() throws GameActionException {
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
				moveAround();
			}
		}
	} // end of moveAround method

	public void followEconUnit() throws GameActionException {
		RobotInfo allies[] = rc.senseNearbyRobots(
				rc.getType().attackRadiusSquared, rc.getTeam());
		RobotInfo allyToFollow = null;

		for (RobotInfo ally : allies) {
			if (ally.type == RobotType.MINER || ally.type == RobotType.BEAVER) {
				allyToFollow = ally;
				break;
			}
		}

		if (allyToFollow != null)
			safeMoveTowardDestination(allyToFollow.location.add(
					allyToFollow.location.directionTo(enemyHQLoc),
					(int) ((double) rc.getType().attackRadiusSquared / 2 * rand
							.nextDouble())));
	}
	
	public void followSoldierOrTankOrDroneUnit() throws GameActionException {
		RobotInfo allies[] = rc.senseNearbyRobots(
				rc.getType().attackRadiusSquared, rc.getTeam());
		RobotInfo allyToFollow = null;

		for (RobotInfo ally : allies) {
			if (ally.type == RobotType.SOLDIER || ally.type == RobotType.TANK || ally.type == RobotType.DRONE) {
				allyToFollow = ally;
				break;
			}
		}

		if (allyToFollow != null)
			safeMoveTowardDestination(allyToFollow.location.add(
					allyToFollow.location.directionTo(enemyHQLoc),
					(int) ((double) rc.getType().attackRadiusSquared / 2 * rand
							.nextDouble())));
	}

	public void safeMoveAround(int numTurns) throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection) && directionSafeFromTowers()) {
				if (rand.nextInt(100) > 10) {
					rc.move(currentDirection);
				} else {
					currentDirection = currentDirection.rotateRight();
					if (rc.canMove(currentDirection)
							&& directionSafeFromTowers()) {
						rc.move(currentDirection);
					} else {
						currentDirection = currentDirection.rotateRight();
						if (numTurns < 8)
							safeMoveAround(numTurns++);
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
				if (numTurns < 8)
					safeMoveAround(numTurns++);
			}
		}
	}

	public void safeMoveAround() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection) && directionSafeFromTowers()) {
				if (rand.nextInt(100) > 10) {
					rc.move(currentDirection);
				} else {
					currentDirection = currentDirection.rotateRight();
					if (rc.canMove(currentDirection)
							&& directionSafeFromTowers()) {
						rc.move(currentDirection);
					} else {
						currentDirection = currentDirection.rotateRight();
						safeMoveAround(1);
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
				safeMoveAround(1);
			}
		}
	}
	
	public void safeMoveTowardOre() throws GameActionException {
		if (rc.isCoreReady()) {
			double maxOre = 0.0;
			Direction toMaxOre = Direction.NONE;

			Direction toTry[] = getDirectionsClosestTo(rc.getLocation()
					.directionTo(enemyHQLoc).opposite());

			for (Direction d : toTry) {
				if (d.equals(Direction.OMNI))
					continue;
				else if (rc.canMove(d)
						&& rc.senseOre(rc.getLocation().add(d)) > maxOre
						&& directionSafeFromTowers(d) && directionSafeFromHQ(d)) {
					maxOre = rc.senseOre(rc.getLocation().add(d));
					toMaxOre = d;
				}
			}
			if (maxOre <= getMineMax())
				safeMoveAround();
			else if (!toMaxOre.equals(Direction.NONE))
				rc.move(toMaxOre);
		}
	}

	public Direction[] getDirectionsClosestTo(Direction towards) {
		boolean prioritzeLeft = rand.nextInt(2) == 0;

		int towardsNum = directionNum(towards);

		Direction closestTo[] = new Direction[8];
		if (prioritzeLeft) {
			closestTo[0] = towards;
			for (int i = 1; i < 4; i++) {
				closestTo[2 * i] = numDirection((towardsNum + i) % 7);
				closestTo[2 * i - 1] = numDirection((towardsNum - i) % 7);
			}
			closestTo[7] = towards.opposite();
		}

		else {
			closestTo[0] = towards;
			for (int i = 1; i < 4; i++) {
				closestTo[2 * i - 1] = numDirection((towardsNum + i) % 7);
				closestTo[2 * i] = numDirection((towardsNum - i) % 7);
			}
			closestTo[7] = towards.opposite();
		}

		return closestTo;
	}

	public MapLocation getTowerLocationByNumber(int towerNumber) {
		MapLocation towers[] = rc.senseTowerLocations();
		return towers[towerNumber % towers.length];
	}

	public RobotType getNeededBuilding() throws GameActionException {
		if (messaging.getNumMinerFactories() < 1)
			return RobotType.MINERFACTORY;
		else if (messaging.getNumBarracks() < 2)
			return RobotType.BARRACKS;
		else if (messaging.getNumTankFactories() < 2)
			return RobotType.TANKFACTORY;
		// else if (messaging.getNumHelipads() < 3)
		// return RobotType.HELIPAD;
		// else if (messaging.getNumAerospacelabs() < 1)
		// return RobotType.AEROSPACELAB;
		else if (messaging.getNumSupplyDepots() < Clock.getRoundNum() / 250)
			return RobotType.SUPPLYDEPOT;

			return null;
	}

	public void harrass() throws GameActionException {
		safeMoveTowardsHQ();
	}

	public void moveToSafety() throws GameActionException {
		if (rc.isCoreReady()) {
			ArrayList<Direction> badDirections = getDangerDirs();
			for (Direction d : badDirections) {
				if (rc.canMove(d.opposite())
						&& !badDirections.contains(d.opposite())) {
					rc.move(d.opposite());
					return;
				}
			}
		}
	}

	private ArrayList<Direction> getDangerDirs() {
		RobotInfo enemies[] = rc.senseNearbyRobots(sensorRadiusSquared);
		MapLocation currentLoc = rc.getLocation();
		ArrayList<Direction> badDirs = new ArrayList<Direction>();

		for (RobotInfo enemy : enemies) {
			int enemyAttackRadiusSquared = enemy.type.attackRadiusSquared;

			Direction toEnemy = currentLoc.directionTo(enemy.location);
			if (!badDirs.contains(toEnemy)
					&& enemyAttackRadiusSquared <= currentLoc
							.distanceSquaredTo(enemy.location)) {
				badDirs.add(currentLoc.directionTo(enemy.location));
			}
		}

		return badDirs;
	}

	public int getMoveableDirections() {
		int numMoveable = 0;
		for (Direction d : Direction.values()) {
			if (rc.canMove(d))
				numMoveable++;
		}
		return numMoveable;
	}

	public boolean directionSafeFromTowers() {
		MapLocation target = rc.getLocation().add(currentDirection);
		MapLocation towerLocs[] = rc.senseEnemyTowerLocations();

		for (MapLocation towerLoc : towerLocs) {
			if (target.distanceSquaredTo(towerLoc) <= RobotType.TOWER.attackRadiusSquared)
				return false;
		}
		return true;
	}

	public boolean directionSafeFromTowers(Direction facing) {
		MapLocation target = rc.getLocation().add(facing);
		MapLocation towerLocs[] = rc.senseEnemyTowerLocations();

		for (MapLocation towerLoc : towerLocs) {
			if (target.distanceSquaredTo(towerLoc) <= RobotType.TOWER.attackRadiusSquared)
				return false;
		}
		return true;
	}

	public boolean directionSafeFromHQ() {
		MapLocation target = rc.getLocation().add(currentDirection);
		if (target.distanceSquaredTo(enemyHQLoc) <= RobotType.HQ.attackRadiusSquared)
			return false;
		return true;
	}

	public boolean directionSafeFromHQ(Direction d) {
		MapLocation target = rc.getLocation().add(d);
		if (target.distanceSquaredTo(enemyHQLoc) <= RobotType.HQ.attackRadiusSquared)
			return false;
		return true;
	}

	public void moveTowardsHQ() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection)) {
				if (rand.nextInt(100) > 10) {
					rc.move(currentDirection);
				} else {
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

	public void safeMoveTowardsHQ() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection)) {
				if (rand.nextInt(100) > 10 && directionSafeFromTowers()
						&& directionSafeFromHQ()) {
					rc.move(currentDirection);
				} else {
					currentDirection = rc.getLocation().directionTo(enemyHQLoc);
					if (rc.canMove(currentDirection)
							&& directionSafeFromTowers()) {
						rc.move(currentDirection);
					} else {
						currentDirection = currentDirection.rotateRight();
						// safeMoveTowardsHQ();
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
				// safeMoveTowardsHQ();
			}
		}
	} // end of moveTowardsHQ method

	// we probably shouldn't use this method anymore since it makes more sense
	// to attack the least healthy enemy
	public void attackEnemyZero() throws GameActionException {
		if (rc.isWeaponReady()) {
			RobotInfo[] enemies = rc.senseNearbyRobots(
					rc.getType().attackRadiusSquared, rc.getTeam().opponent());
			if (1 <= enemies.length) {
				rc.attackLocation(enemies[0].location);
			}
		}
	} // end of attackEnemyZero method

	public void attackLeastHealthyEnemy() throws GameActionException {
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),
				rc.getType().attackRadiusSquared, rc.getTeam().opponent());
		double leastHealth = 999999;
		MapLocation loc = rc.getLocation().add(1, 1);
		for (RobotInfo robot : nearbyEnemies) {
			if (robot.health < leastHealth) {
				leastHealth = robot.health;
				loc = robot.location;
			}
		}
		if (nearbyEnemies.length >= 1) {
			if (rc.isCoreReady() && rc.isWeaponReady()) {
				rc.attackLocation(loc);
			}
		}
	} // end of attackLeastHealthyEnemy method

	public MapLocation getClosestTowerLocation() {
		MapLocation enemyTowers[] = rc.senseEnemyTowerLocations();

		int closestDistance = Integer.MAX_VALUE;
		MapLocation closestTower = null;
		for (MapLocation tower : enemyTowers) {
			int distanceSquaredTo = tower.distanceSquaredTo(rc
					.senseHQLocation());
			if (distanceSquaredTo < closestDistance) {
				closestTower = tower;
				closestDistance = distanceSquaredTo;
			}
		}

		if (closestTower != null)
			return closestTower;
		else
			return enemyHQLoc;
	}

	public void mine() throws GameActionException {
		double mineMax = getMineMax();

		if (rc.isCoreReady() && rc.senseOre(rc.getLocation()) > mineMax) {
			rc.mine();
		}
	} // end of mine method

	public double getMineMax() {
		double mineMax = (rc.getType() == RobotType.MINER ? GameConstants.MINER_MINE_MAX
				: GameConstants.BEAVER_MINE_MAX);
		return mineMax;
	}

	public void spawnRobot(RobotType type) throws GameActionException {
		if (rc.hasSpawnRequirements(type) && rc.isCoreReady()) {
			for (Direction d : Direction.values()) {
				if (rc.canSpawn(d, type)) {
					rc.spawn(d, type);
					break;
				}
			}
		}
	} // end of spawnRobot method

	public boolean build(RobotType building) throws GameActionException {
		if (building == null)
			return false;
		if (rc.hasBuildRequirements(building) && rc.isCoreReady()) {
			Direction bestBuildDir = getDirectionToBuild();
			if (rc.canBuild(bestBuildDir, building)) {
				rc.build(bestBuildDir, building);
				return true;
			}
		}
		return false;
	} // end of build method

	private Direction getDirectionToBuild() throws GameActionException {
		for (Direction d : Direction.values()) {
			MapLocation testSite = rc.getLocation().add(d);
			if (rc.senseTerrainTile(testSite) == TerrainTile.NORMAL
					&& rc.senseRobotAtLocation(rc.getLocation().add(d)) == null) {
				if (isABuilding(rc.senseRobotAtLocation(testSite
						.add(Direction.NORTH_EAST)))
						|| isABuilding(rc.senseRobotAtLocation(testSite
								.add(Direction.NORTH_WEST)))
						|| isABuilding(rc.senseRobotAtLocation(testSite
								.add(Direction.SOUTH_EAST)))
						|| isABuilding(rc.senseRobotAtLocation(testSite
								.add(Direction.SOUTH_WEST))))
					return d;
			}

		}
		return Direction.NONE;
	}

	public boolean isABuilding(RobotInfo robot) {
		if (robot == null)
			return false;
		switch (robot.type) {
		case AEROSPACELAB:
		case BARRACKS:
		case HANDWASHSTATION:
		case HELIPAD:
		case HQ:
		case MINERFACTORY:
		case SUPPLYDEPOT:
		case TANKFACTORY:
		case TECHNOLOGYINSTITUTE:
		case TOWER:
		case TRAININGFIELD:
			return true;
		default:
			return false;

		}
	}

	public void buildSupplyDepotNearHQ() throws GameActionException {
		MapLocation currentLoc = rc.getLocation();
		int distanceFromHQ = currentLoc.distanceSquaredTo(rc.senseHQLocation());
		if (Clock.getRoundNum() < 1500) {
			if (rand.nextInt(100) < 10) {
				if (distanceFromHQ < 60 && distanceFromHQ > 10) {
					build(RobotType.SUPPLYDEPOT);
				}
			}
		}
	} // end of buildSupplyDepotNearHQ method

	// checks to see how many nearby allies have zero supply
	public int checkSupplyLevels() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
				rc.getType().sensorRadiusSquared, rc.getTeam());
		int zeroSupplyCounter = 0;
		for (RobotInfo robot : nearbyAllies) {
			if (robot.supplyLevel == 0) {
				zeroSupplyCounter++;
			}
		}
		return zeroSupplyCounter;
	} // end of checkSupplyLevels method

	// transfer supply to other robots that have less supply
	public void transferSupply() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
				GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation transferDestination = null;
		if (nearbyAllies.length > 0) {
			for (RobotInfo robot : nearbyAllies) {
				if (robot.supplyLevel < lowestSupply) {
					lowestSupply = robot.supplyLevel;
					transferAmount = ((rc.getSupplyLevel() - robot.supplyLevel) / 2);
					transferDestination = robot.location;
				}
			}
		}
		if (transferDestination != null) {
			int transferDistance = transferDestination.distanceSquaredTo(rc
					.getLocation());
			if (transferDistance <= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
				rc.transferSupplies((int) transferAmount, transferDestination);
			}
		}
	} // end of transferSupply method

	public void moveTowardDestination(MapLocation dest)
			throws GameActionException {
		Direction toDest = rc.getLocation().directionTo(dest);
		Direction[] directions = { toDest, toDest.rotateLeft(),
				toDest.rotateLeft().rotateLeft(), toDest.rotateRight(),
				toDest.rotateRight().rotateRight() };
		for (Direction d : directions) {
			if (rc.canMove(d) && rc.isCoreReady()) {
				rc.move(d);
				break;
			}
		}
	} // end of moveTowardDestination method

	public void safeMoveTowardDestination(MapLocation dest)
			throws GameActionException {
		if (rc.isCoreReady()) {
			Direction toDest = rc.getLocation().directionTo(dest);
			Direction[] directions = { toDest, toDest.rotateLeft(),
					toDest.rotateLeft().rotateLeft(), toDest.rotateRight(),
					toDest.rotateRight().rotateRight() };
			for (Direction d : directions) {
				if (rc.canMove(d) && directionSafeFromTowers(d)
						&& directionSafeFromHQ(d)) {
					rc.move(d);
					break;
				}
			}
		}
	} // end of moveTowardDestination method

	public abstract void defaultPanicAction() throws GameActionException;

	public abstract void defaultAttackAction() throws GameActionException;

	public abstract void defaultDefendAction() throws GameActionException;

	public abstract void defaultEconAction() throws GameActionException;

	public abstract void defaultExploreAction() throws GameActionException;

	public abstract void defaultTurnSetup() throws GameActionException;

	public abstract void defaultSpawnSetup() throws GameActionException;

	public abstract void defaultTurnEndAction() throws GameActionException;

	// this method isn't being used, but could be used for efficient direction
	// changing
	public int directionNum(Direction d) {
		switch (d) {
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
		case OMNI:
			return 8;
		default:
			return -1;
		}
	} // end of directionNum method

	public Direction numDirection(int directionVal) {
		if (directionVal < 0)
			directionVal = 8 + directionVal;
		switch (directionVal) {
		case 0:
			return Direction.NORTH;
		case 1:
			return Direction.NORTH_WEST;
		case 2:
			return Direction.WEST;
		case 3:
			return Direction.SOUTH_WEST;
		case 4:
			return Direction.SOUTH;
		case 5:
			return Direction.SOUTH_EAST;
		case 6:
			return Direction.EAST;
		case 7:
			return Direction.NORTH_EAST;
		case 8:
			return Direction.OMNI;
		default:
			return Direction.NONE;
		}
	} // end of directionNum method
}
