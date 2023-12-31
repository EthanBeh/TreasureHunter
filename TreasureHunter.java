import java.util.Scanner;


/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */


public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);
    public static boolean gameOver = false;
    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private boolean easyMode;
    private boolean samuraiMode;


    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        easyMode = false;
        samuraiMode = false;
    }


    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }




    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.println("Welcome to TREASURE HUNTER!");
        System.out.println("Going hunting for the big treasure, eh?");
        System.out.print("What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();


        // set hunter instance variable
        hunter = new Hunter(name, 10);


        System.out.print("Difficulty? (e/n/h) ");
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("h")) {
            hardMode = true;
        } if (hard.equals("e")) {
            easyMode = true;
            hunter.changeGold(10);
        } else if (hard.equals("test")) {
            hunter.changeGold(90);
            String[] available = {"water", "rope", "machete", "horse", "boat", "boots", "shovel"};
            for (String item: available) {
                hunter.buyItem(item, 0);
            }
        } if (hard.equals("s")) {
            samuraiMode = true;
            hunter.hasSword();
        }
    }


    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;


            // and the town is "tougher"
            toughness = 0.75;
        } if (easyMode) {
            markdown = 1;
            toughness = 0;
        }
        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);
        if (samuraiMode) {
            shop.samuraiMode();
        }


        String treasure = Town.treasureList[(int) (Math.random() * 4)];
        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness, treasure);
        if (easyMode) {
            currentTown.setBreakChance();
        }




        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }


    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";


        while (!choice.equals("x") && !gameOver) {
            System.out.println();
            System.out.println(currentTown.getLatestNews());
            System.out.println("***");
            System.out.println(hunter);
            System.out.print("Treasures found: ");
            String found = "";
            for (String treasure: hunter.getTreasures()) {
                if (treasure != null) {
                    found += "a " + treasure + ", ";
                }
            } if (found == "") {
                System.out.println("none");
            } else {
                System.out.println(found.substring(0, found.length()-2));
            }
            System.out.println(currentTown);
            System.out.println("(B)uy something at the shop.");
            System.out.println("(S)ell something at the shop.");
            System.out.println("(M)ove on to a different town.");
            System.out.println("(L)ook for trouble!");
            System.out.println("(H)unt for treasure!");
            System.out.println("(D)ig for gold!");
            System.out.println("Give up the hunt and e(X)it.");
            System.out.println();
            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
    }


    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("x")) {
            System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
        } else if (choice.equals("h")) {
            if (currentTown.lookForTreasure(hunter) == true) {
                if (hunter.hasAllTreasures()) {
                    gameOver = true;
                    System.out.println("You found a " + currentTown.getTreasure() + "!\nCongratulations, you have found the last of the three treasures! You win!");
                } else {
                    System.out.println("You found a " + currentTown.getTreasure() + "!");
                }
            } else {
                if (!currentTown.hasTreasure()) {
                    System.out.println("You have already searched this town");
                } else {
                    System.out.println("You found a " + currentTown.getTreasure() + "\nBut... you already have one!");
                }
            }
        } else if (choice.equals("d")) {
            int earned = currentTown.dig();
            if (earned != 0 && earned != -1 && earned != -2) {
                System.out.println("You found " + earned + " gold");
            } else {
                if (hunter.hasShovel()) {
                    if (earned == -1) {
                        System.out.println("You dug but only found dirt");
                    } else {
                        if (currentTown.isDug()) {
                            System.out.println("You've already dug in this town");
                        }
                    }
                } else {
                    if (earned == -2) {
                        System.out.println("You need a shovel to dig!");
                    }
                }
            }
        } else {
            System.out.println("Yikes! That's an invalid option! Try again.");
        }
        if (gameOver && !hunter.hasAllTreasures()) {
            System.out.println(currentTown.getLatestNews());
            System.out.println("Game over!\nFare thee well, " + hunter.getHunterName() + "!");
        }
    }
}
