/*******************************************************************************
 * Copyright (c) 2015, 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.buildings.workers;

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.MaterialProductionSettings;
import jsettlers.logic.buildings.WorkAreaBuilding;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.Player;

/**
 * This class is a building with a worker that can fulfill it's job.
 * 
 * @author Andreas Eberle
 * 
 */
public class WorkerBuilding extends WorkAreaBuilding implements IWorkerRequestBuilding {
	private static final long serialVersionUID = 7050284039312172046L;

	private IManageableWorker worker;
	private EMaterialType[] order = null;
	private int orderPointer = 0;
	private Movable ship = null;
	private int[] dockPosition = null; // x, y, dx, dy

	/**
	 * Points where we need to clean up pigs or donkeys.
	 */
	private List<Tuple<ShortPoint2D, EMapObjectType>> cleanupPositions = null;

	public WorkerBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
	}

	@Override
	public final EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_ROOF;
	}

	@Override
	protected final int constructionFinishedEvent() {
		requestWorker();
		return -1; // no scheduling required
	}

	private void requestWorker() {
		super.grid.requestBuildingWorker(super.getBuildingType().getWorkerType(), this);
	}

	@Override
	protected final int subTimerEvent() {
		assert false : "This should never be called, as this building should not be scheduled.";
		return -1;
	}

	protected final boolean popMaterialFromStack(EMaterialType material) {
		for (IRequestStack stack : super.getStacks()) {
			if (stack.getMaterialType() == material) {
				return stack.pop();
			}
		}
		return false;
	}

	@Override
	public final void occupyBuilding(IManageableWorker worker) {
		if (super.isNotDestroyed()) {
			this.worker = worker;
			super.showFlag(true);
			super.initWorkStacks();
		}
	}

	@Override
	public MaterialProductionSettings getMaterialProduction() {
		return grid.getMaterialProductionAt(pos.x, pos.y);
	}

	@Override
	public final void leaveBuilding(IManageableWorker worker) {
		if (worker == this.worker) {
			this.worker = null;
			super.showFlag(false);
			super.releaseRequestStacks();
			requestWorker();
		} else {
			System.err.println("ERROR: A worker not registered at the building wanted to leave it!");
		}
	}

	@Override
	protected final void killedEvent() {
		if (worker != null) {
			this.worker.buildingDestroyed();
			this.worker = null;
		}

		if (cleanupPositions != null) {
			for (Tuple<ShortPoint2D, EMapObjectType> cleanup : cleanupPositions) {
				grid.getMapObjectsManager().removeMapObjectType(cleanup.e1.x, cleanup.e1.y, cleanup.e2);
			}
		}
		super.killedEvent();
	}

	@Override
	public final boolean isOccupied() {
		return worker != null;
	}

	@Override
	public boolean tryTakingResource() {
		return false;
	}

	@Override
	public boolean tryTakingFoood(EMaterialType[] foodOrder) {
		return false;
	}

	@Override
	public void addMapObjectCleanupPosition(ShortPoint2D pos, EMapObjectType objectType) {
		if (cleanupPositions == null) {
			cleanupPositions = new ArrayList<>();
		}

		for (Tuple<ShortPoint2D, EMapObjectType> cleanup : cleanupPositions) {
			if (cleanup.e1.equals(pos) && cleanup.e2 == objectType) {
				return;
			}
		}
		cleanupPositions.add(new Tuple<>(pos, objectType));
	}

	@Override
	public EMaterialType getOrderedMaterial() {
		if (this.order != null && this.orderPointer < this.order.length) {
			return this.order[this.orderPointer];
		}
		return null;
	}

	@Override
	public void setOrder(EMaterialType[] list) {
		this.order = list;
		this.orderPointer = 0;
	}

	@Override
	public void reduceOrder() {
		this.orderPointer++;
	}

	@Override
	public void buildShipAction() {
		if (this.ship == null) {
			ShortPoint2D position = new ShortPoint2D
					((short) (this.dockPosition[0] + 5 * this.dockPosition[2]),
					(short) (this.dockPosition[1] + 5 * this.dockPosition[3]));
			this.ship = new Movable(super.grid.getMovableGrid(), EMovableType.FERRY,
					position, super.getPlayer());
			EDirection direction = EDirection.getDirection
					(this.dockPosition[2], this.dockPosition[3]).rotateRight(1);
			this.ship.setDirection(direction);
		} else {
			this.ship.increaseStateProgress((float) (1./24.));
			if (this.ship.getStateProgress() >= .99) {
				this.ship = null;
			}
		}
	}

	public void setDock(int[] position) {
		if (this.type != EBuildingType.DOCKYARD) {
			return;
		}
		if (this.dockPosition != null) { // replace dock, kill ship
			this.grid.setDock(this.dockPosition, false, this.getPlayerId());
			if (this.ship != null) {
				this.ship.kill();
			}
		}
		this.dockPosition = position;
		this.grid.setDock(position, true, this.getPlayerId());
	}

	public int[] getDock() {
		return this.dockPosition;
	}

	public ShortPoint2D whereIsMaterialAvailable(EMaterialType material) {
		for (IRequestStack stack : getStacks()) {
			if (stack.getMaterialType() == material) {
				if (stack.hasMaterial()) {
					return stack.getPos();
				}
			}
		}
		return null;
	}
}
