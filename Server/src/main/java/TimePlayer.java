//
// Created by Khashayar Abdouli on 8/22/14.
//


import java.util.Calendar;

public class TimePlayer
{

    private Calendar initialDate;
    private Calendar createdDate;
    private double speed;

    /**
     * Constructor with certain initial date and speed
     * @param initialDate given date
     * @param speed given speed
     */
    public TimePlayer(Calendar initialDate, double speed) {
        this.initialDate = initialDate;
        this.speed = speed;
        this.createdDate = Calendar.getInstance();
        System.out.println("Start Date : " + this.initialDate.getTime());
    }

    /**
     * Constructor with certain speed
     * @param speed given speed
     */
    public TimePlayer(float speed)
    {
        this.speed = speed;
        this.initialDate = Calendar.getInstance();
        this.createdDate = Calendar.getInstance();
        System.out.println("Start Date : " + this.initialDate.getTime());
    }

    /**
     * Returns the specified speed
     */
    public double getSpeed()
    {
        return speed;
    }

    /**
     * Sets the speed to given value
     */
    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    /**
     * @return the date from the player calculated based on the speed of the player
     */
    public Calendar getCurrentDate()
    {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long initialTime = initialDate.getTimeInMillis();
        long createdTime = createdDate.getTimeInMillis();
        long timeDiff = currentTime - createdTime;

        timeDiff *= this.speed;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(initialTime + timeDiff);
        return calendar;

    }

    /**
     * It compares the current date of the player and the given date to calculate the delay needed
     * @param next the next date to compare with
     * @return the delay needed calculated in milliseconds
     */
    public long sleepTime(Calendar next)
    {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long initialTime = initialDate.getTimeInMillis();
        long createdTime = createdDate.getTimeInMillis();
        long timeDiff = currentTime - createdTime;

        timeDiff *= this.speed;

        long min = Math.min(540000, (next.getTimeInMillis() - (initialTime + timeDiff)));
        long sleep = (long) (min / speed);

        return sleep;
    }
}
