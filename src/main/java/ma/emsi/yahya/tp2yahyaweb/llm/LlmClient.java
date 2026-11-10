package ma.emsi.yahya.tp2yahyaweb.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;

import java.io.Serializable;

@Dependent
public class LlmClient implements Serializable {

    private static final long serialVersionUID = 1L;

    private String systemRole;

    private final ChatMemory chatMemory;
    private final Assistant assistant;

    public LlmClient() {
        String cle = System.getenv("GEMINI_KEY");
        if (cle == null || cle.isBlank()) {
            throw new IllegalStateException("La variable GEMINI_KEY n'est pas définie.");
        }

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        chatMemory = MessageWindowChatMemory.withMaxMessages(20);

        assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    public void setSystemRole(String roleSysteme) {
        this.systemRole = roleSysteme;
        chatMemory.clear();
        chatMemory.add(SystemMessage.from(this.systemRole));
    }

    public String chat(String prompt) {
        return assistant.chat(prompt);
    }
}