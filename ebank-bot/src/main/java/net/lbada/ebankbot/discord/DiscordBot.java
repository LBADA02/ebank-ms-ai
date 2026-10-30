package net.lbada.ebankbot.discord;


import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.lbada.ebankbot.agents.EbankAiAgent;

@DiscordController
public class DiscordBot {
        private EbankAiAgent ebankAiAgent;

    public DiscordBot(EbankAiAgent ebankAiAgent) {
        this.ebankAiAgent = ebankAiAgent;
    }



    @DiscordMapping
    private void perform (MessageReceivedEvent event){
        System.out.println(event);
        if (event.getAuthor().isBot()) return;
        String query = event.getMessage().getContentRaw();
        String response = ebankAiAgent.chat(query);
        event.getChannel().sendMessage(response).queue();
    }

}
