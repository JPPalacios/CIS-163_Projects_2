package project2GIVE_TO_STUDENTS;

import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ListModel extends AbstractTableModel {

    /**
     * holds all the rentals
     */
    private ArrayList<Rental> listOfRentals;

    /**
     * holds only the rentals that are to be displayed
     */
    private ArrayList<Rental> filteredListRentals;

    /**
     * holds rentals that are overdue
     */
    private ArrayList<Rental> overDue;

    /**
     * current screen being displayed
     */
    private ScreenDisplay display = ScreenDisplay.CurrentRentalStatus;

    private String[] columnNamesCurrentRentals = {"Renter\'s Name", "Est. Cost",
            "Rented On", "Due Date ", "Console", "Name of the Game"};
    private String[] columnNamesReturned = {"Renter\'s Name", "Rented On Date",
            "Due Date", "Actual date returned ", "Est. Cost", " Real Cost"};
            // use in within7daysgamesfirst -> modify not done
    // private String[] columnWithin7Days = {"Renter\'s Name", "Rented On Date",
    // "Due Date", "Actual date returned ", "Est. Cost", " Real Cost"};


    private DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    public ListModel() {
        display = ScreenDisplay.CurrentRentalStatus;
        listOfRentals = new ArrayList<>();
        filteredListRentals = new ArrayList<>();
        updateScreen();
        createList();
    }

    public void setDisplay(ScreenDisplay selected) {
        display = selected;
        updateScreen();
    }

    private void updateScreen() {
        switch (display) {
            case CurrentRentalStatus:
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                        .filter(n -> n.actualDateReturned == null)
                        .collect(Collectors.toList());

                // Note: This uses Lambda function
                Collections.sort(filteredListRentals, (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));
                break;

            case ReturnedItems:
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                       .filter(n -> n.actualDateReturned != null)
                       .collect(Collectors.toList());

                // Note: This uses an anonymous class.
                Collections.sort(filteredListRentals, new Comparator<Rental>() {
                    @Override
                    public int compare(Rental n1, Rental n2) {
                        return n1.nameOfRenter.compareTo(n2.nameOfRenter);
                    }
                });

                break;

            case EveryThing:
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                    .collect(Collectors.toList());

                break;

            case DueWithInWeek:
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                    .filter(n -> n.dueBack.compareTo(n.rentedOn)  <= 7)  // not returned rentals,  dueDate - dateRented <= 7
                    .collect(Collectors.toList()); 

                break;

            case DueWithinWeekGamesFirst:
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                    .filter(n -> n.dueBack.compareTo(n.rentedOn) <=7)
                    .filter(n -> n.getActualDateReturned() == null )
                    .collect(Collectors.toList());

                Collections.sort(filteredListRentals, (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));
                break;

            case Cap14DaysOverdue:
                // Your code goes here AND OTHER PLACES TOO
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                    .filter(n ->  n.dueBack.compareTo(n.rentedOn)< -7)
                    .filter(n ->  n.dueBack.compareTo(n.rentedOn)  <= -14)                 
                    // .map(n -> n.getNameOfRenter().toUpperCase())
                    // .map(n -> n.getRentedOn())
                    // .map(n -> n.getDueBack()) 
                    // .map(n -> n.getActualDateReturned())
                    //.map(n -> n == null ? defaultValue : x ) use for reference
                    .collect(Collectors.toList()); 

                Collections.sort(filteredListRentals, (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));
                              
                break;

            default:
                throw new RuntimeException("upDate is in undefined state: " + display);
        }
        fireTableStructureChanged();
    }
    
    /**
     * Private helper method to count the number of days between two
     * GregorianCalendar dates
     * Note that this is the proper way to do this; trying to use other
     * classes/methods likely won't properly account for leap days
     * @param startDate - the beginning/starting day
     * @param endDate - the last/ending day
     * @return int for the number of days between startDate and endDate
     */
    private int daysBetween(GregorianCalendar startDate, GregorianCalendar endDate) {
		// Determine how many days the Game was rented out
		GregorianCalendar gTemp = new GregorianCalendar();
		gTemp = (GregorianCalendar) endDate.clone(); //  gTemp = dueBack;  does not work!!
		int daysBetween = 0;
		while (gTemp.compareTo(startDate) > 0) {
			gTemp.add(Calendar.DATE, -1);                // this subtracts one day from gTemp
			daysBetween++;
		}

		return daysBetween;
	}

       @Override
    public String getColumnName(int col) {
        switch (display) {
            case CurrentRentalStatus:
                return columnNamesCurrentRentals[col];
            case ReturnedItems:
                return columnNamesReturned[col];
            case DueWithInWeek:
                return columnNamesCurrentRentals[col];
            case Cap14DaysOverdue:
                return columnNamesCurrentRentals[col];
            case DueWithinWeekGamesFirst:
                return columnNamesCurrentRentals[col];
            case EveryThing:
                return columnNamesCurrentRentals[col];

        }
        throw new RuntimeException("Undefined state for Col Names: " + display);
    }

    @Override
    public int getColumnCount() {
        switch (display) {
            case CurrentRentalStatus:
                return columnNamesCurrentRentals.length;
            case ReturnedItems:
                return columnNamesReturned.length;
            case DueWithInWeek:
                return columnNamesCurrentRentals.length;
            case Cap14DaysOverdue:
                return columnNamesCurrentRentals.length;
            case DueWithinWeekGamesFirst:
                return columnNamesCurrentRentals.length;
            case EveryThing:
                return columnNamesCurrentRentals.length;

        }
        throw new IllegalArgumentException();
    }

    @Override
    public int getRowCount() {
        return filteredListRentals.size();     // returns number of items in the arraylist
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (display) {
            case CurrentRentalStatus:
                return currentRentScreen(row, col);
            case ReturnedItems:
                return rentedOutScreen(row, col);
            case DueWithInWeek:
                return currentRentScreen(row, col);
            case Cap14DaysOverdue:
                return currentRentScreen(row, col);
            case DueWithinWeekGamesFirst:
                return currentRentScreen(row, col);
            case EveryThing:
                return currentRentScreen(row, col);


        }
        throw new IllegalArgumentException();
    }

    private Object currentRentScreen(int row, int col) {
        switch (col) {
            case 0:
                return (filteredListRentals.get(row).nameOfRenter);

            case 1:
                return (filteredListRentals.get(row).getCost(filteredListRentals.
                        get(row).dueBack));

            case 2:
                return (formatter.format(filteredListRentals.get(row).rentedOn.getTime()));

            case 3:
                if (filteredListRentals.get(row).dueBack == null)
                    return "-";

                return (formatter.format(filteredListRentals.get(row).dueBack.getTime()));

            case 4:
                if (filteredListRentals.get(row) instanceof Console)
                    return (((Console) filteredListRentals.get(row)).getConsoleType());
                else {
                    if (filteredListRentals.get(row) instanceof Game)
                        if (((Game) filteredListRentals.get(row)).getConsole() != null)
                            return ((Game) filteredListRentals.get(row)).getConsole();
                        else
                            return "";
                }

            case 5:
                if (filteredListRentals.get(row) instanceof Game)
                    return (((Game) filteredListRentals.get(row)).getNameGame());
                else
                    return "";
            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }


    private Object rentedOutScreen(int row, int col) {
        switch (col) {
            case 0:
                return (filteredListRentals.get(row).nameOfRenter);

            case 1:
                return (formatter.format(filteredListRentals.get(row).rentedOn.
                        getTime()));
            case 2:
                return (formatter.format(filteredListRentals.get(row).dueBack.
                        getTime()));
            case 3:
                return (formatter.format(filteredListRentals.get(row).
                        actualDateReturned.getTime()));

            case 4:
                return (filteredListRentals.
                        get(row).getCost(filteredListRentals.get(row).dueBack));

            case 5:
                return (filteredListRentals.
                        get(row).getCost(filteredListRentals.get(row).
                        actualDateReturned
                ));

            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

   // public Object 

    public void add(Rental a) {
        listOfRentals.add(a);
        updateScreen();
    }

    public Rental get(int i) {
        return filteredListRentals.get(i);
    }

    public void update(int index, Rental unit) {
        updateScreen();
    }

    public void saveDatabase(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            System.out.println(listOfRentals.toString());
            os.writeObject(listOfRentals);
            os.close();
        } catch (IOException ex) {
            throw new RuntimeException("Saving problem! " + display);
        }
    }

    public void loadDatabase(String filename) {
        listOfRentals.clear();

        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream is = new ObjectInputStream(fis);

            listOfRentals = (ArrayList<Rental>) is.readObject();
            updateScreen();
            is.close();
        } catch (Exception ex) {
            throw new RuntimeException("Loading problem: " + display);

        }
    }

    public boolean saveAsText(String filename) {
        if (filename.equals("")) {
            throw new IllegalArgumentException();
        }

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(filename)));
            out.println(listOfRentals.size());
            for (int i = 0; i < listOfRentals.size(); i++) {
                Rental unit = listOfRentals.get(i);
                out.println(unit.getClass().getName());
                out.println("Name is " + unit.getNameOfRenter());
                out.println("Rented on " + formatter.format(unit.rentedOn.getTime()));
                out.println("DueDate " + formatter.format(unit.dueBack.getTime()));

                if (unit.getActualDateReturned() == null)
                    out.println("Not returned!");
                else
                    out.println(formatter.format(unit.actualDateReturned.getTime()));

                if (unit instanceof Game) {
                    out.println(((Game) unit).getNameGame());
                    if (((Game) unit).getConsole() != null)
                        out.println(((Game) unit).getConsole());
                    else
                        out.println("No Console");
                }

                if (unit instanceof Console)
                    out.println(((Console) unit).getConsoleType());
            }
            out.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public void loadFromText(String filename) {
        listOfRentals.clear();

        if (filename == null)
			throw new IllegalArgumentException();

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
            
            int i = 0;
            Game rent = new Game();
            Console rent2 = new Console();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            GregorianCalendar date = new GregorianCalendar();
            Date d1;

            scanner.nextLine();

            while(scanner.hasNextLine()){
                //checks if game or console
            if(scanner.nextLine().contains("project2GIVE_TO_STUDENTS.Game")){
               //gets renters name
                scanner.next();
                scanner.next();
                rent.nameOfRenter = scanner.next();
                //gets dates for rented on and due date. 
                try{
                //rented    
                scanner.next();
                scanner.next(); 
                d1 = df.parse(scanner.next());
                date.setTime(d1);
                rent.setRentedOn(date); 
                //due
                scanner.next();
                d1 = df.parse(scanner.next());
                date.setTime(d1);
                rent.setDueBack(date); 
                //acutal date returned 
                String checkReturn = scanner.nextLine();
                if(checkReturn.contains("Not returned")){


                }
                else{
                    d1 = df.parse(checkReturn);
                    date.setTime(d1);
                    rent.setActualDateReturned(date);
                }
                
                }catch(java.text.ParseException e){
                }
                rent.setNameGame(scanner.nextLine());
                rent.setConsole(ConsoleTypes.valueOf(scanner.nextLine()));
            
                listOfRentals.add(i,rent);
            }else{
                 //gets renters name
                 scanner.next();
                 scanner.next();
                 rent2.nameOfRenter = scanner.next();
                 //gets dates for rented on and due date. 
                 try{
                 //rented    
                 scanner.next();
                 scanner.next(); 
                 d1 = df.parse(scanner.next());
                 date.setTime(d1);
                 rent2.setRentedOn(date); 
                 //due
                 scanner.next();
                 d1 = df.parse(scanner.next());
                 date.setTime(d1);
                 rent2.setDueBack(date); 
                 //acutal date returned 
                 String checkReturn = scanner.nextLine();
                 if(checkReturn.contains("Not returned")){
 
 
                 }
                 else{
                     d1 = df.parse(checkReturn);
                     date.setTime(d1);
                     rent2.setActualDateReturned(date);
                 }
                 
                 }catch(java.text.ParseException e){
                 }
                 rent2.setConsoleType(ConsoleTypes.valueOf(scanner.nextLine()));

                listOfRentals.add(i,rent2);
                
            }
            i++;
        }
			
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException();
		}

        scanner.close();
        updateScreen();
    }

    /**********************************************************************
     *
     *  DO NOT MODIFY THIS METHOD!!!!!!
     */
    public void createList() {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        GregorianCalendar g1 = new GregorianCalendar();
        GregorianCalendar g2 = new GregorianCalendar();
        GregorianCalendar g3 = new GregorianCalendar();
        GregorianCalendar g4 = new GregorianCalendar();
        GregorianCalendar g5 = new GregorianCalendar();
        GregorianCalendar g6 = new GregorianCalendar();
        GregorianCalendar g7 = new GregorianCalendar();
        GregorianCalendar g8 = new GregorianCalendar();

        try {
            Date d1 = df.parse("1/20/2020");
            g1.setTime(d1);
            Date d2 = df.parse("12/22/2020");
            g2.setTime(d2);
            Date d3 = df.parse("12/20/2019");
            g3.setTime(d3);
            Date d4 = df.parse("7/02/2020");
            g4.setTime(d4);
            Date d5 = df.parse("1/20/2010");
            g5.setTime(d5);
            Date d6 = df.parse("9/29/2020");
            g6.setTime(d6);
            Date d7 = df.parse("7/25/2020");
            g7.setTime(d7);
            Date d8 = df.parse("7/29/2020");
            g8.setTime(d8);

            Console console1 = new Console("Person1", g4, g6, null, ConsoleTypes.PlayStation4);
            Console console2 = new Console("Person2", g5, g3, null, ConsoleTypes.PlayStation4);
            Console console3 = new Console("Person5", g4, g8, null, ConsoleTypes.SegaGenesisMini);
            Console console4 = new Console("Person6", g4, g7, null, ConsoleTypes.SegaGenesisMini);
            Console console5 = new Console("Person1", g5, g4, g3, ConsoleTypes.XBoxOneS);

            Game game1 = new Game("Person1", g3, g2, null, "title1", ConsoleTypes.PlayStation4);
            Game game2 = new Game("Person1", g3, g1, null, "title2", ConsoleTypes.PlayStation4);
            Game game3 = new Game("Person1", g5, g3, null, "title2", ConsoleTypes.SegaGenesisMini);
            Game game4 = new Game("Person7", g4, g8, null, "title2", null);
            Game game5 = new Game("Person3", g3, g1, g1, "title2", ConsoleTypes.XBoxOneS);
            Game game6 = new Game("Person6", g4, g7, null, "title1", ConsoleTypes.NintendoSwich);
            Game game7 = new Game("Person5", g4, g8, null, "title1", ConsoleTypes.NintendoSwich);

            controler controler2 = new controler("Person2", g5, g3, null, ControlerTypes.PlayStation4);
            controler controler3 = new controler("Person5", g4, g8, null, ControlerTypes.SegaGenesisMini);
            controler controler4 = new controler("Person6", g4, g7, null, ControlerTypes.SegaGenesisMini);
            controler controler5 = new controler("Person1", g5, g4, g3, ControlerTypes.XBoxOneS);

            add(game1);
            add(game4);
            add(game5);
            add(game2);
            add(game3);
            add(game6);
            add(game7);

            add(console1);
            add(console2);
            add(console5);
            add(console3);
            add(console4);

            add(controler2);
            add(controler3);
            add(controler4);
            add(controler5);

            // create a bunch of them.
            int count = 0;
            Random rand = new Random(13);
            String guest = null;

            while (count < 30) {  // change this number to 300 for a complete test of your code
                Date date = df.parse("7/" + (rand.nextInt(10) + 2) + "/2020");
                GregorianCalendar g = new GregorianCalendar();
                g.setTime(date);
                if (rand.nextBoolean()) {
                    guest = "Game" + rand.nextInt(5);
                    Game game;
                    if (count % 2 == 0)
                        game = new Game(guest, g4, g, null, "title2", ConsoleTypes.NintendoSwich);
                    else
                        game = new Game(guest, g4, g, null, "title2", null);
                    add(game);


                } else {
                    guest = "Console" + rand.nextInt(5);
                    date = df.parse("7/" + (rand.nextInt(20) + 2) + "/2020");
                    g.setTime(date);
                    Console console = new Console(guest, g4, g, null, getOneRandom(rand));
                    add(console);
                }

                count++;
            }
        } catch (ParseException e) {
            throw new RuntimeException("Error in testing, creation of list");
        }
    }

    public ConsoleTypes getOneRandom(Random rand) {

        int number = rand.nextInt(ConsoleTypes.values().length - 1);
        switch (number) {
            case 0:
                return ConsoleTypes.PlayStation4;
            case 1:
                return ConsoleTypes.XBoxOneS;
            case 2:
                return ConsoleTypes.PlayStation4Pro;
            case 3:
                return ConsoleTypes.NintendoSwich;
            default:
                return ConsoleTypes.SegaGenesisMini;
        }
    }
}