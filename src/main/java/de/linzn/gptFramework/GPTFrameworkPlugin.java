/*
 * Copyright (C) 2023. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 * this file. If not, please write to: niklas.linz@enigmar.de
 *
 */

package de.linzn.gptFramework;


import de.stem.stemSystem.modules.pluginModule.STEMPlugin;


public class GPTFrameworkPlugin extends STEMPlugin {

    public static GPTFrameworkPlugin gptFrameworkPlugin;
    private GPTManager gptManager;

    public GPTFrameworkPlugin() {
        gptFrameworkPlugin = this;
    }

    @Override
    public void onEnable() {
        saveConfig();
        this.gptManager = new GPTManager(this);
    }

    @Override
    public void onDisable() {
    }

    private void saveConfig(){
        getDefaultConfig().getString("openAI.token", "xxx");
        getDefaultConfig().getString("personality.model", "gpt-3.5-turbo");
        getDefaultConfig().getString("personality.user", "STEM-SYSTEM");
        getDefaultConfig().getString("personality.description", "Hello, you are Jarvis");
        getDefaultConfig().save();
    }
    public GPTManager getGptManager() {
        return gptManager;
    }
}
