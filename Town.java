/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */


public class Town {
    public static String[] treasureList = {"crown", "trophy", "gem", "dust"};


    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    private boolean hasTreasure;
    private double breakChance;
    private boolean dug;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();


        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;


        printMessage = "";


        breakChance = 0;


        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }
    //second constructor to prevent errors
    public Town(Shop shop, double toughness, String treasure) {
        this.shop = shop;
        this.terrain = getNewTerrain();


        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;


        printMessage = "";


        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        this.treasure = treasure;
        hasTreasure = true;
        dug = false;
    }


    public String getLatestNews() {
        return printMessage;
    }


    public void setBreakChance() {
        breakChance = 1;
    }


    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";


        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }


    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            if (hunter.hasItemInKit("sword") && terrain.getTerrainName() == "Jungle") {
                item = "sword";
            }
            printMessage = "You used your " + Colors.PURPLE + item + Colors.RESET + " to cross the " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET +  ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + Colors.PURPLE + item + Colors.RESET;
            }


            return true;
        }


        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + Colors.PURPLE + terrain.getNeededItem() + Colors.RESET + ".";
        return false;
    }


    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        printMessage = "You left the shop";
    }


    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }


        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance || hunter.hasItemInKit("sword")) {
                if (hunter.hasItemInKit("sword")) {
                    printMessage += Colors.BLUE + "Due to censorship, the sword used magic instead to instantly win the brawl." + Colors.RESET;
                } else {
                    printMessage += Colors.BLUE + "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                }
                printMessage += "\nYou won the brawl and receive "+ Colors.YELLOW + goldDiff + " gold."+ Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                printMessage += Colors.RED + "\nYou lost the brawl and pay " + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(-goldDiff);
            }
        }
    }


    public boolean lookForTreasure(Hunter h) {
        if (hasTreasure) {
            if (!h.hasTreasure(treasure)) {
                h.addTreasure(treasure);
                hasTreasure = false;
                return true;
            }
        } return false;
    }


    public String getTreasure() {
        return treasure;
    }


    public boolean hasTreasure() {
        return hasTreasure;
    }


    public int dig() {
        printMessage = "";
        if (!dug) {
            if (hunter.hasShovel()) {
                dug = true;
                if (Math.random() >= 0.5) {
                    int g = (int) (Math.random() * 20) + 1;
                    hunter.changeGold(g);
                    return g;
                } return -1;
            } return -2;
        } return 0;
    }


    public boolean isDug() {
        return dug;
    }


    public String toString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }


    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random() * 1.2;
        if (rnd < .2) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .4) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .6) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .8) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 1) {
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }


    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (breakChance != 1){
            double rand = Math.random();
            return (rand < 0.5);
        } else {
            return true;
        }
    }
}
