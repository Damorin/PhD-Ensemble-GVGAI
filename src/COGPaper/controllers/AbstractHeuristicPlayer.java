package COGPaper.controllers;

import COGPaper.heuristics.StateHeuristic;
import core.game.Game;
import core.game.StateObservation;
import core.player.AbstractPlayer;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by kisenshi on 03/03/17.
 * Abstract player that allows using a certain heuristic that needs to be set up
 */
public abstract class AbstractHeuristicPlayer extends AbstractPlayer {

    private String heuristicName = "YouForgotToSetTheHeuristic";
    protected StateHeuristic heuristic;

    public AbstractHeuristicPlayer(StateObservation stateObs, String heuristicName) {
        setPlayerHeuristic(heuristicName);
        this.heuristic = createPlayerHeuristic(stateObs);
    }

    /**
     * When the game is finished it is needed to print some information in a file
     * to be able to
     */
    public void recordHeuristicData(Game played, String fileName, int randomSeed, int[] recordIds) {
        heuristic.recordDataOnFile(played, fileName, randomSeed, recordIds);
    }

    protected void setPlayerHeuristic(String heuristicName) {
        this.heuristicName = heuristicName;
    }

    protected StateHeuristic createPlayerHeuristic(StateObservation stateObs) {
        StateHeuristic heuristic = null;
        try {
            Class<? extends StateHeuristic> heuristicClass = Class.forName(heuristicName)
                    .asSubclass(StateHeuristic.class);

            // It is pass the stateObs as argument when instantiating the class
            Class[] heuristicArgsClass = new Class[]{StateObservation.class};
            Object[] constructorArgs = new Object[]{stateObs};

            Constructor heuristicArgsConstructor = heuristicClass.getConstructor(heuristicArgsClass);

            heuristic = (StateHeuristic) heuristicArgsConstructor.newInstance(constructorArgs);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("Constructor " + heuristicName + "() not found :");
            System.exit(1);

        } catch (ClassNotFoundException e) {
            System.err.println("Class " + heuristicName + " not found :");
            e.printStackTrace();
            System.exit(1);

        } catch (InstantiationException e) {
            System.err.println("Exception instantiating " + heuristicName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access exception when instantiating " + heuristicName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Exception calling the constructor " + heuristicName + "():");
            e.printStackTrace();
            System.exit(1);
        }

        return heuristic;
    }

    public String getHeuristicName() {
        return this.heuristicName;
    }

    /**
     * For some heuristics it would be helpful to print info in the screen
     */
    public void draw(Graphics2D g) {
        heuristic.drawInScreen(g);
    }

}
