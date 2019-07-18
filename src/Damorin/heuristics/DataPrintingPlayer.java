package Damorin.heuristics;

import core.game.Game;
import core.game.StateObservation;
import core.vgdl.VGDLRegistry;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Cristina on 31/03/2017.
 */
public class DataPrintingPlayer extends StateHeuristic {
    int block_size;
    int grid_width;
    int grid_height;

    String[] games_experiment_sprites[] = new String[][]{
        //aliens
        new String[]{
            "background",
            "base",
            "avatar",
            "missile",
            "sam",
            "bomb",
            "alien",
            "alienGreen",
            "alienBlue",
            "portal",
            "portalSlow",
            "portalFast",
        },
        // "bait",
        new String[]{
            "floor",
            "hole",
            "avatar",
            "nokey",
            "withkey",
            "mushroom",
            "key",
            "goal",
            "box",
            "wall",
        },
        // "butterflies",
        new String[]{
            "floor",
            "cocoon",
            "animal",
            "avatar",
            "butterfly",
            "wall",
        },
        // "camelRace",
        new String[]{
            "floor",
            "camel",
            "randomCamel",
            "straightCamelFast",
            "fastR",
            "fastL",
            "straightCamelMedium",
            "mediumR",
            "mediumL",
            "straightCamelSlow",
            "slowR",
            "slowL",
            "structure",
            "goal",
            "winnerNPC",
            "winnerPlayer",
            "avatar",
            "left",
            "right",
            "wall",
        },
        // "chase",
        new String[]{
            "floor",
            "carcass",
            "goat",
            "angry",
            "scared",
            "avatar",
            "wall",
        },
        // "chopper",
        new String[]{
            "layers",
            "stratosphere",
            "thermosphere",
            "troposphere",
            "satellite",
            "avatar",
            "missile",
            "sam",
            "bomb",
            "cloud",
            "leftCloud",
            "fastLeftCloud",
            "rightCloud",
            "fastRightCloud",
            "tank",
            "portal",
            "portalBase",
            "portalAmmo",
            "supply",
            "bullet",
        },
        // "crossfire",
        new String[]{
            "floor",
            "turret",
            "bomb",
            "structure",
            "goal",
            "avatar",
            "wall",
        },
        // "digdug",
        new String[]{
            "floor",
            "goodies",
            "gold",
            "gem",
            "shovel",
            "weapon",
            "resting",
            "boulder",
            "moving",
            "avatar",
            "monster",
            "falling",
            "entrance",
            "wall",
        },
        // "escape",
        new String[]{
            "floor",
            "avatar",
            "box",
            "exit",
            "hole",
            "wall",
        },
        // "hungrybirds",
        new String[]{
            "floor",
            "avatar",
            "foodbank",
            "wall",
            "goal",
            "food",
        },
        // "infection",
        new String[]{
            "floor",
            "sword",
            "entrance",
            "virus",
            "moving",
            "avatar",
            "normal",
            "carrier",
            "npc",
            "host",
            "infected",
            "doctor",
            "wall",
        },
        // "intersection",
        new String[]{
            "floor",
            "floorV",
            "crossing",
            "goal",
            "deadPortal",
            "car",
            "rightcar",
            "fastRcar",
            "slowRcar",
            "downcar",
            "fastDcar",
            "slowDcar",
            "start",
            "wall",
            "tree",
            "input",
            "avatar",
        },
        // "lemmings",
        new String[]{
            "floor",
            "hole",
            "shovel",
            "entrance",
            "exit",
            "moving",
            "avatar",
            "lemming",
            "wall",
        },
        // "missilecommand",
        new String[]{
            "floor",
            "city",
            "explosion",
            "movable",
            "avatar",
            "incoming",
            "incoming_slow",
            "incoming_fast",
            "wall",
        },
        // "modality",
        new String[]{
            "black",
            "white",
            "grey",
            "target",
            "winTarget",
            "crate",
            "avatar",
            "inBlack",
            "inWhite",
            "inGrey",
            "wall",
        },
        // "plaqueattack",
        new String[]{
            "floor",
            "fullMolar",
            "fullMolarInf",
            "fullMolarSup",
            "deadMolar",
            "deadMolarInf",
            "deadMolarSup",
            "movable",
            "avatar",
            "food",
            "hotdog",
            "burger",
            "holes",
            "hotdoghole",
            "burgerhole",
            "fluor",
            "wall",
        },
        // "roguelike",
        new String[]{
            "floor",
            "exit",
            "sword",
            "weapon",
            "health",
            "lock",
            "gold",
            "key",
            "market",
            "transaction",
            "moving",
            "avatar",
            "spider",
            "phantom",
            "wall",
        },
        // "seaquest",
        new String[]{
            "sky",
            "water",
            "saved",
            "holes",
            "sharkhole",
            "whalehole",
            "diverhole",
            "normaldiverhole",
            "oftendiverhole",
            "moving",
            "avatar",
            "torpedo",
            "fish",
            "shark",
            "whale",
            "pirana",
            "diver",
            "crew",
        },
        // "survivezombies",
        new String[]{
            "floor",
            "flower",
            "hell",
            "fastHell",
            "slowHell",
            "honey",
            "moving",
            "avatar",
            "bee",
            "zombie",
            "wall",
        },
        // "waitforbreakfast
        new String[]{
            "ground",
            "floor",
            "exit",
            "wall",
            "othertable",
            "table",
            "emptytable",
            "tablewb",
            "endtable",
            "kitchen",
            "chair",
            "front",
            "back",
            "left",
            "right",
            "singlechair",
            "singlefront",
            "singleback",
            "singleleft",
            "singleright",
            "avatar",
            "standingavatar",
            "leavingavatar",
            "satavatar",
            "satfront",
            "satback",
            "satleft",
            "satright",
            "waiter",
            "waiterwb",
            "waiternb",
        }
    };

    public DataPrintingPlayer(StateObservation stateObs){
        block_size = stateObs.getBlockSize();
        Dimension grid_dimension = stateObs.getWorldDimension();

        grid_width = grid_dimension.width / block_size;
        grid_height = grid_dimension.height / block_size;

        initHeuristicAccumulation();

    }

    /* ***********************************************************************************************
     * PRINTING INFORMATION
     * ***************************************************************************************************/

    private void printGameSpritesInformation(String[] game_sprites, BufferedWriter writer) throws IOException{
        for (String stype: game_sprites){
            writer.write(VGDLRegistry.GetInstance().getRegisteredSpriteValue(stype) + " <- " + stype  + "\n");
        }
    }

    /* ***********************************************************************************************
    * HEURISTIC INFORMATION
    * ****************************************************************************************************/

    @Override
    public double evaluateState(StateObservation stateObs) {
        return 0;
    }

    @Override
    public void updateHeuristicBasedOnCurrentState(StateObservation stateObs) {

    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        int gameId = recordIds[0];
        String[] game_sprites = games_experiment_sprites[gameId];
        try {
            if(fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), false));

                printGameSpritesInformation(game_sprites, writer);

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void drawInScreen(Graphics2D g) {

    }
}
