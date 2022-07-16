import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static java.time.ZoneId.systemDefault;

public class TesTime {
    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static void main(String[] args) {
        //long delay =
 ;
//        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
//        long timestampnow = instant.toEpochMilli();
//        System.out.println(timestampnow)
//        LocalDateTime now = LocalDateTime.now();
//        Time timenow = Time.valueOf(dtf.format(now));
//        long timenow_ = timenow.getTime();
//        System.out.println(dtf.format(now));
//        System.out.println(timenow_);
        String datenow_ = "10:00:50";
        Time datenow__ = Time.valueOf(datenow_);
        long datenow = datenow__.getTime();
        System.out.println("Waktu Sekarang : " + datenow);

        String datemessage_ = "10:00:00";
        Time datemessage__ = Time.valueOf(datemessage_);
        long datemessage = datemessage__.getTime();
        System.out.println("Waktu Pesan : " + datemessage);

        long eppoch = 1657874421690L;
        Instant eppoch_ = Instant.ofEpochMilli(eppoch);

        long now = Instant.now().toEpochMilli();
        System.out.println(now);
        System.out.println("Pengurangan : " + (now - eppoch));


        //LocalDateTime eppoch__ = LocalDateTime.ofInstant(eppoch_), ZoneId.systemDefault();
        System.out.println("Waktu Eppoch : " + eppoch_);
//
//        long kurang = datenow - datemessage;
//        System.out.println("kurang : " + kurang);
//
//        DelayTime();
//
//    }
//
//    public static void DelayTime(){
//        String delayMinute = "10:01:00";
//        Time delay = Time.valueOf(delayMinute);
//        long delay_ = delay.getTime();
//        System.out.println(delay);
//        System.out.println(delay_);
//    }

           int i = 0;
           int b =18;
           i += b;
           System.out.println(i);
//

}
}
