package acm.ucf.simplereminders.generate.id;

import java.util.Calendar;

/**
 * Created by Ryan on 11/16/2014.
 */
public class IdGenerator {
    public static int generateID(){
        Calendar cal = Calendar.getInstance();
        long ID = cal.getTimeInMillis();
        ID = ID % 1000000000;

        return (int)ID;
    }
}
