/*******************************************************************************
 * Copyright (c) 2017
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
package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.position.ShortPoint2D;

/**
 *
 * @author Rudolf Polzer
 *
 */
public class DockGuiTask extends SimpleBuildingGuiTask {
    private int[] dockPosition;

    public DockGuiTask() {
    }

    /**
     *
     * @param guiAction
     * @param playerId
     * @param dockPosition
     * @param buildingPos
     */
    public DockGuiTask(EGuiAction guiAction, byte playerId, int[] dockPosition, ShortPoint2D buildingPos) {
        super(guiAction, playerId, buildingPos);
        this.dockPosition = dockPosition;
    }

    public int[] getPosition() {
        return dockPosition;
    }

    @Override
    protected void serializeTask(DataOutputStream dos) throws IOException {
        super.serializeTask(dos);
        for(int i = 0; i < 5; i++) {
            dos.writeInt(dockPosition[i]);
        }
    }

    @Override
    protected void deserializeTask(DataInputStream dis) throws IOException {
        super.deserializeTask(dis);
        for(int i = 0; i < 5; i++) {
            dockPosition[i] = dis.readInt();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((dockPosition == null) ? 0 : dockPosition.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DockGuiTask other = (DockGuiTask) obj;
        if (dockPosition == null) {
            if (other.dockPosition != null)
                return false;
        } else if (!dockPosition.equals(other.dockPosition))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DockGuiTask [dockPosition=" + dockPosition + ", getBuildingPos()=" + getBuildingPos() + ", getGuiAction()="
                + getGuiAction() + ", getPlayerId()=" + getPlayerId() + "]";
    }
}
