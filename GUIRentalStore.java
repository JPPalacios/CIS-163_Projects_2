package project2GIVE_TO_STUDENTS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;

public class GUIRentalStore extends JFrame implements ActionListener {
    private JMenuBar menus;

    private JMenu fileMenu;
    private JMenu actionMenu;

    private JMenuItem openSerItem;
    private JMenuItem exitItem;
    private JMenuItem saveSerItem;
    private JMenuItem openTextItem;
    private JMenuItem saveTextItem;
    private JMenuItem rentConsoleItem;
    private JMenuItem rentGameItem;
    private JMenuItem rentControllerItem;
    private JMenuItem returnItem;

    private JMenuItem currentRentedItemScn;
    private JMenuItem currentReturnedItemScn;
    private JMenuItem withIn7ItemScn;
    private JMenuItem sort30DaysItemScn;
    private JMenuItem sortGameItemScn;
    private JMenuItem everything;

    private JPanel panel;

    private ListModel dList;

    private JTable jTable;

    private JScrollPane scrollList;

    public GUIRentalStore(){
        menus = new JMenuBar();
        fileMenu = new JMenu("File");
        actionMenu = new JMenu("Action");
        openSerItem = new JMenuItem("Open File");
        exitItem = new JMenuItem("Exit");
        saveSerItem = new JMenuItem("Save File");
        openTextItem = new JMenuItem("Open Text");
        saveTextItem = new JMenuItem("Save Text");
        rentConsoleItem = new JMenuItem("Rent a Console");
        rentGameItem = new JMenuItem("Rent a Game");
        rentControllerItem = new JMenuItem("Rent a Controller");
        returnItem = new JMenuItem("Return of Game, Console or Controller");

        currentRentedItemScn = new JMenuItem("Current Rental Screen");
        currentReturnedItemScn = new JMenuItem("Returned screen");
        withIn7ItemScn = new JMenuItem("Within 7 Days Screen");
        sortGameItemScn = new JMenuItem("Within 7 Days Games First Screen");
        sort30DaysItemScn = new JMenuItem("Cap all Rentals 14 days late Screen");
        everything = new JMenuItem("EveryThing Screen");

        fileMenu.add(openSerItem);
        fileMenu.add(saveSerItem);
        fileMenu.addSeparator();
        fileMenu.add(openTextItem);
        fileMenu.add(saveTextItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        fileMenu.addSeparator();
        fileMenu.add(currentRentedItemScn);
        fileMenu.add(currentReturnedItemScn);
        fileMenu.add(withIn7ItemScn);
        fileMenu.add (sortGameItemScn);
        fileMenu.add(sort30DaysItemScn);
        fileMenu.addSeparator();
        fileMenu.add(everything);

        actionMenu.add(rentConsoleItem);
        actionMenu.add(rentGameItem);
        actionMenu.add(rentControllerItem);
        actionMenu.addSeparator();
        actionMenu.add(returnItem);

        menus.add(fileMenu);
        menus.add(actionMenu);

        openSerItem.addActionListener(this);
        saveSerItem.addActionListener(this);
        openTextItem.addActionListener(this);
        saveTextItem.addActionListener(this);
        exitItem.addActionListener(this);
        rentConsoleItem.addActionListener(this);
        rentGameItem.addActionListener(this);
        rentControllerItem.addActionListener(this);
        returnItem.addActionListener(this);

        currentRentedItemScn.addActionListener(this);
        currentReturnedItemScn.addActionListener(this);
        withIn7ItemScn.addActionListener(this);
        sortGameItemScn.addActionListener(this);
        sort30DaysItemScn.addActionListener(this);
        everything.addActionListener(this);

        setJMenuBar(menus);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        dList = new ListModel();
        jTable = new JTable(dList);
        scrollList = new JScrollPane(jTable);
        panel.add(scrollList);
        add(panel);
        scrollList.setPreferredSize(new Dimension(1000,800));

        setVisible(true);
        setSize(1070,950);
    }

    public void actionPerformed(ActionEvent e) {
        Object comp = e.getSource();

        if (currentRentedItemScn == comp)
            dList.setDisplay(ScreenDisplay.CurrentRentalStatus);

        if (currentReturnedItemScn == comp)
            dList.setDisplay(ScreenDisplay.ReturnedItems);

        if (withIn7ItemScn == comp) {
            GregorianCalendar dat = new GregorianCalendar();
            dList.setDisplay(ScreenDisplay.DueWithInWeek);
        }

        if (everything == comp)
            dList.setDisplay(ScreenDisplay.EveryThing);


        if (sortGameItemScn == comp)
            dList.setDisplay(ScreenDisplay.DueWithinWeekGamesFirst);

        if (sort30DaysItemScn == comp)
            dList.setDisplay(ScreenDisplay.Cap14DaysOverdue);

        if (openSerItem == comp || openTextItem == comp) {
            JFileChooser chooser = new JFileChooser();
            int status = chooser.showOpenDialog(null);
            if (status == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                if (openSerItem == comp)
                    dList.loadDatabase(filename);
                else if (openTextItem == comp)
                    dList.loadFromText((filename));
            }
        }

        if (saveSerItem == comp || saveTextItem == comp) {
            JFileChooser chooser = new JFileChooser();
            int status = chooser.showSaveDialog(null);
            if (status == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                if (saveSerItem == e.getSource())
                    dList.saveDatabase(filename);
                else if (saveTextItem == comp)
                    dList.saveAsText(filename);
            }
        }

        if(e.getSource() == exitItem){
            System.exit(1);
        }
        if(e.getSource() == rentConsoleItem){
            Console Console = new Console();
            RentConsoleDialog dialog = new RentConsoleDialog(this, Console);
            if(dialog.getCloseStatus() == RentConsoleDialog.OK){
                dList.add(Console);
            }
        }
        if(e.getSource() == rentGameItem){
            Game gameOnly = new Game();
            RentGameDialog dialog = new RentGameDialog(this, gameOnly);
            if(dialog.getCloseStatus() == RentGameDialog.OK){
                dList.add(gameOnly);
            }
        }
        if(e.getSource() == rentControllerItem){
            controler controller = new controler();
            RentcontrolerDialog dialog = new RentcontrolerDialog(this, controller);
            if(dialog.getCloseStatus() == RentGameDialog.OK){
                dList.add(controller);
            }
        }


        if (returnItem == e.getSource()) {
            int index = jTable.getSelectedRow();
            if (index != -1) {
                GregorianCalendar dat = new GregorianCalendar();

                Rental unit = dList.get(index);
                ReturnedOnDialog dialog = new ReturnedOnDialog(this, unit);

                JOptionPane.showMessageDialog(null,
                        "  Be sure to thank " + unit.nameOfRenter +
                                "\n for renting with us. The price is:  " +
                                unit.getCost(unit.actualDateReturned) +
                                " dollars");
            if(dialog.getCloseStatus() == ReturnedOnDialog.OK){     

                dList.get(index).setActualDateReturned(dat);
                dList.update(index, unit);
            }
            }
        }

    }

    public static void main(String[] args) {
        new GUIRentalStore();
    }
}