import java.time.LocalTime;

public class Clock implements Runnable{
    private LocalTime nodeTime;
    private long delay;
    private int idProcess;
    private long pidProcess;

    public Clock(String defaultTime, long delayTime, int idProcess){
        this.nodeTime = LocalTime.parse(defaultTime);
        this.delay = delayTime;
        this.idProcess = idProcess;
    }

    public Clock(String defaultTime, long delayTime, long idProcess){
        this.nodeTime = LocalTime.parse(defaultTime);
        this.delay = delayTime;
        this.pidProcess = idProcess;
    }

    public void run(){
        while(true){
            this.timer();
        }
    }

    private void timer(){
        try{
            this.nodeTime = this.nodeTime.plusMinutes(1);
            //System.out.println("Current time process id " + this.idProcess + ": " + nodeTime.toString());
            Thread.currentThread().sleep(this.delay);
        } catch(InterruptedException e){

        }
    }

    public int timeStamp(int daemonTime){
        int thisMinutes = this.toMinutes(this.nodeTime);

        return (thisMinutes - daemonTime);
    }

    public int timeStamp(LocalTime daemonTime){
        int daemonMinutes = this.toMinutes(daemonTime);
        int thisMinutes = this.toMinutes(this.nodeTime);

        return (thisMinutes - daemonMinutes);
    }

    public int getMinutes(){
        int minutes = this.nodeTime.getHour()*60 + this.nodeTime.getMinute();

        return minutes;
    }

    @Override
    public String toString(){
        return this.nodeTime.toString();
    }


    public void setCertainTime(int time){
        this.nodeTime = this.nodeTime.plusMinutes(time);
    }

    public LocalTime getNodeTime(){
        return this.nodeTime;
    }

    public int toMinutes(LocalTime timeConverter){
        return timeConverter.getHour()*60 + timeConverter.getMinute();
    }
}