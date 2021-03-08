package project2GIVE_TO_STUDENTS;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class controler extends Rental {
 
    /** Represents the type of Console, see enum type. */
    private ControlerTypes controlerTypes;

    public controler(){
    }

    public controler(String nameOfRenter,
                   GregorianCalendar rentedOn,
                   GregorianCalendar dueBack,
                   GregorianCalendar actualDateReturned,
                   ControlerTypes controlerTypes) {
        super(nameOfRenter, rentedOn, dueBack, actualDateReturned);
        this.controlerTypes = controlerTypes;
    }

    public ControlerTypes getControlerTypes() {
        return controlerTypes;
    }

    public void setCotrollerType(ControlerTypes controlerTypes) {
        this.controlerTypes = controlerTypes;
    }

    @Override
    public double getCost(GregorianCalendar dueBack) {
        GregorianCalendar gTemp = new GregorianCalendar();
        double cost = 2;

        gTemp = (GregorianCalendar) dueBack.clone();     //  gTemp = dueBack;  does not work!!

        for (int days = 0; days < 7; days++)  // or             gTemp.add(Calendar.DATE, -7);
            gTemp.add(Calendar.DATE, -1);

        while (gTemp.after(rentedOn)) {
            while (gTemp.after(rentedOn)) {
                if ((this.controlerTypes == ControlerTypes.NintendoSwich) ||
                        (this.controlerTypes == ControlerTypes.PlayStation4Pro) ||
                        (this.controlerTypes == ControlerTypes.SegaGenesisMini))
                    cost += 1.5;

                if ((this.controlerTypes == ControlerTypes.PlayStation4) ||
                        (this.controlerTypes == ControlerTypes.XBoxOneS))
                    cost += 1;
                gTemp.add(Calendar.DATE, -1);

            }
        }
        return cost;
    }

    @Override
    public String toString() {
        return "controler{" +
                " controlerTypes=" + controlerTypes + " " + super.toString() +
                '}';
    }
}




