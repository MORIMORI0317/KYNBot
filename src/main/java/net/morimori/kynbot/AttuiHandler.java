package net.morimori.kynbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.morimori.kynbot.audio.PlayerManager;

import java.util.HashMap;
import java.util.Map;

public class AttuiHandler {
    private static final String attui = "https://cdn.discordapp.com/attachments/523502209988821033/884013238323404801/d7004ab52bb45ee0.wav";

    public static void update(long myid, float temp) {
        JDA jda = Main.jda;

        float vt = Math.max(temp - Main.attuiTemp, 0);
        int vol = (int) (vt * (300 / 20));
        jda.getGuilds().forEach(n -> {
            PlayerManager.getInstance().loadAndPlay(n.getIdLong(), attui, vol);

            AudioManager audioManager = n.getAudioManager();
            if (temp >= Main.attuiTemp) {
                VoiceChannel moto = n.getMemberById(myid).getVoiceState().getChannel();
                if (n.getVoiceStates().stream().filter(a -> a.getMember().getIdLong() != myid).noneMatch(a -> a.getChannel() == moto)) {
                    audioManager.closeAudioConnection();
                }
            } else {
                audioManager.closeAudioConnection();
            }
        });

        if (temp >= Main.attuiTemp)
            conectvc(myid);
    }

    public static void conectvc(long myid) {
        JDA jda = Main.jda;

        jda.getGuilds().forEach(n -> {

            Map<VoiceChannel, Integer> conects = new HashMap<>();
            n.getVoiceStates().stream().filter(s -> s.getMember().getIdLong() != myid).forEach(m -> {
                VoiceChannel channel = m.getChannel();
                if (channel == null) return;
                if (!conects.containsKey(channel))
                    conects.put(channel, 0);
                conects.put(channel, conects.get(channel) + 1);
            });
            VoiceChannel channel = null;
            int max = 0;
            for (Map.Entry<VoiceChannel, Integer> voiceChannelIntegerEntry : conects.entrySet()) {
                if (max < voiceChannelIntegerEntry.getValue())
                    channel = voiceChannelIntegerEntry.getKey();
            }
            AudioManager audioManager = n.getAudioManager();
            if (channel != null) {
                audioManager.openAudioConnection(channel);
            }
        });

    }

}
