package de.linzn.gptFramework.memory;

import com.theokanning.openai.completion.chat.ChatMessage;
import de.linzn.gptFramework.completions.AIChatCompletion;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.databaseModule.DatabaseModule;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;

public class DatabaseMemory {
    private final AIChatCompletion aiChatCompletion;
    private LinkedList<ChatMessage> dataMemory;

    public DatabaseMemory(AIChatCompletion aiChatCompletion) {
        this.aiChatCompletion = aiChatCompletion;
        this.dataMemory = new LinkedList<>();
        this.loadMemoryFromSQL();
    }


    public void memorize(ChatMessage chatMessage) {
        this.dataMemory.addLast(chatMessage);
        this.saveToSQL(chatMessage);
    }

    public LinkedList<ChatMessage> getMemory() {
        LinkedList<ChatMessage> trimmedMemory = new LinkedList<>(this.dataMemory);
        if (this.dataMemory.size() > 20) {
            trimmedMemory = new LinkedList<>(trimmedMemory.subList(trimmedMemory.size() - 20, trimmedMemory.size()));
        }
        return trimmedMemory;
    }

    private void saveToSQL(ChatMessage chatMessage) {
        STEMPlugin identityPlugin = this.aiChatCompletion.getIdentityPlugin();
        String identity = this.aiChatCompletion.getIdentity();
        java.sql.Date date = new java.sql.Date(new Date().getTime());

        DatabaseModule databaseModule = STEMSystemApp.getInstance().getDatabaseModule();

        try {
            Connection conn = databaseModule.getConnection();

            String query = " INSERT INTO plugin_gpt_memory_data (identityPlugin, identity, role, content, date)"
                    + " VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, identityPlugin.getPluginName());
            preparedStmt.setString(2, identity);
            preparedStmt.setString(3, chatMessage.getRole());
            preparedStmt.setString(4, chatMessage.getContent());
            preparedStmt.setDate(5, date);
            preparedStmt.execute();

            databaseModule.releaseConnection(conn);
        } catch (SQLException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }

    private void loadMemoryFromSQL() {
        STEMPlugin identityPlugin = this.aiChatCompletion.getIdentityPlugin();
        String identity = this.aiChatCompletion.getIdentity();

        DatabaseModule databaseModule = STEMSystemApp.getInstance().getDatabaseModule();

        try {
            Connection conn = databaseModule.getConnection();

            String query = "SELECT * FROM plugin_gpt_memory_data WHERE identityPlugin = '" + identityPlugin.getPluginName() + "' AND identity = '" + identity + "' ORDER BY id DESC LIMIT 50";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                STEMSystemApp.LOGGER.DEBUG("Loading gptID " + rs.getInt("id") + " from database");
                String role = rs.getString("role");
                String content = rs.getString("content");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setRole(role);
                chatMessage.setContent(content);
                this.dataMemory.addFirst(chatMessage);
            }

            databaseModule.releaseConnection(conn);
        } catch (SQLException e) {
            STEMSystemApp.LOGGER.ERROR(e);
        }
    }

}
