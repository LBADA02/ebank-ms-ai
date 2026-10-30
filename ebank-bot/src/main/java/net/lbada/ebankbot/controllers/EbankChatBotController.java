package net.lbada.ebankbot.controllers;

import net.lbada.ebankbot.agents.EbankAiAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/chat")
public class EbankChatBotController {

    private EbankAiAgent ebankAiAgent;

    public EbankChatBotController(EbankAiAgent ebankAiAgent) {
        this.ebankAiAgent = ebankAiAgent;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam String query) {
       return ebankAiAgent.chat(query);
    }

}
