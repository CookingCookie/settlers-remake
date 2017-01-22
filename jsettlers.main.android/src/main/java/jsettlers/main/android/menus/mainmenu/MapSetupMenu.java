package jsettlers.main.android.menus.mainmenu;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartScreen;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.providers.GameStarter;

/**
 * Created by tompr on 21/01/2017.
 */

public abstract class MapSetupMenu {
    private final GameStarter gameStarter;
    private final IMapDefinition mapDefinition;

    public MapSetupMenu(GameStarter gameStarter) {
        this.gameStarter = gameStarter;
        this.mapDefinition = gameStarter.getMapDefinition();
    }

    public void dispose() {

    }

    public String getMapName() {
        return mapDefinition.getMapName();
    }

    public short[] getMapImage() {
        return mapDefinition.getImage();
    }

    //TODO return wrapper object with suitable toString()
    public Integer[] getAllowedPlayerCounts() {
        int maxPlayers = mapDefinition.getMaxPlayers();
        int minPlayers = mapDefinition.getMinPlayers();
        int numberOfOptions = maxPlayers - minPlayers + 1;

        Integer[] allowedPlayerCounts = new Integer[numberOfOptions];

        for (int i = 0; i < numberOfOptions; i++) {
            allowedPlayerCounts[i] = minPlayers + i;
        }

        return allowedPlayerCounts;
    }

    //TODO return wrapper object with suitable toString()
    public EMapStartResources[] getStartResourcesOptions() {
        return EMapStartResources.values();
    }

    //TODO return wrapper object with suitable toString()
    public String[] getPeaceTimeOptions() {
        return new String[] { "Without" };
    }

    public abstract void startGame();




    /**
     * protected getters
     */
    protected GameStarter getGameStarter() {
        return gameStarter;
    }

    protected IMapDefinition getMapDefinition() {
        return mapDefinition;
    }
}
