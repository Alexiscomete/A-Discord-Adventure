package io.github.alexiscomete.lapinousecond;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class Report extends CommandBot {

    public Report() {
        super("Permet de report aux modérateurs de A Discord Adventure un abus ou un bug", "report", "Permet de report aux modérateurs de A Discord Adventure un abus. A utiliser si un un joueur ou un serveur abuse de fait que Lapinou Second donne des invitations pour des serveurs.");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (args.length > 1) {
            Optional<Channel> chop = Main.api.getChannelById(862268273113825310L);
            if (chop.isPresent()) {
                Channel ch = chop.get();
                Optional<TextChannel> txtchop = ch.asTextChannel();
                if (txtchop.isPresent()) {
                    messageCreateEvent.getMessage().reply("Merci de votre aide, un signalement est toujours utile");
                    EmbedBuilder builder = new EmbedBuilder().setTitle("Report").setAuthor(messageCreateEvent.getMessageAuthor()).setDescription(content).setTimestampToNow();
                    txtchop.get().sendMessage(builder);
                }
            }
        } else {
            messageCreateEvent.getMessage().reply("Indiquez précisément votre problème, pour les bugs dévrivez les étapes pour les reproduires eet le résultat, pour les abus indiquez l'id du serveur / joueur et l'id du lieu où cela s'est déroulé");
        }
    }
}
