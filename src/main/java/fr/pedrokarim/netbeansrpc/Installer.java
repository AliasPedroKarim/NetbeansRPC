/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.pedrokarim.netbeansrpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import java.util.logging.Logger;
import org.openide.modules.ModuleInstall;
import org.openide.util.actions.SystemAction;
/**
 *
 * @author karim
 */

public class Installer extends ModuleInstall{
    public static final Logger log = Logger.getLogger("Installer");
    
    public Installer() {
        super();
        System.out.println("[PluginRPC] NetbeansRPC has loaded.");
        
        
        createPresence(new String[]{"621768079386345477"});
    }

    @Override
    public void close() {
        System.out.println("[PluginRPC] NetbeansRPC is closed.");
    }
    
    public static void createPresence(String[] args){
        
        if (args.length == 0) {
                System.err.println("You must specify an application ID in the arguments!");
                System.exit(-1);
        }
        DiscordRPC lib = DiscordRPC.INSTANCE;
        DiscordRichPresence presence = new DiscordRichPresence();
        String applicationId = args.length < 1 ? "" : args[0];
        String steamId       = args.length < 2 ? "" : args[1];

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");

        lib.Discord_Initialize(applicationId, handlers, true, steamId);

        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        // presence.endTimestamp   = presence.startTimestamp + 20;
        presence.details   = "Netbeans Test";
        presence.state     = "youupiii";
        presence.largeImageKey = "first";
        presence.largeImageText = "Netbeans EDI";
        presence.smallImageKey = "java";
        presence.smallImageText = "programming in java";
        // presence.partySize = 1;
        // presence.partyMax  = 4;
        lib.Discord_UpdatePresence(presence);

        final Thread t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                        lib.Discord_RunCallbacks();
                        try {
                                Thread.sleep(500);
                        } catch (InterruptedException e) {
                                lib.Discord_Shutdown();
                                break;
                        }
                }
        }, "RPC-Callback-Handler");
        t.start();
    }
    
}
