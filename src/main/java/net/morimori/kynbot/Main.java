package net.morimori.kynbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static String TOKEN;
    public static JDA jda;
    public static float attuiTemp;

    public static void main(String[] args) throws Exception {

        if (args.length <= 0)
            throw new IllegalStateException("No Token");

        if (args.length <= 1)
            attuiTemp = 45;
        else
            attuiTemp = Float.parseFloat(args[1]);

        TOKEN = args[0];
        jda = JDABuilder.createDefault(TOKEN).build();
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("test"));

        long myid = jda.getSelfUser().getIdLong();

        Timer timer = new Timer();
        TimerTask updatesystemdata = new TimerTask() {
            public void run() {
                SystemInfo si = new SystemInfo();
                HardwareAbstractionLayer ha = si.getHardware();
                Sensors s = ha.getSensors();
                float temp = (float) s.getCpuTemperature();

                JDA jda = Main.jda;

                jda.getPresence().setActivity(Activity.playing(temp + "度"));
            }
        };
        timer.scheduleAtFixedRate(updatesystemdata, 0, 3 * 1000);

        while (true) {
            try {
                Thread.sleep(1000);
                SystemInfo si = new SystemInfo();
                HardwareAbstractionLayer ha = si.getHardware();
                Sensors s = ha.getSensors();
                float temp = (float) s.getCpuTemperature();
                AttuiHandler.update(myid, temp);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
