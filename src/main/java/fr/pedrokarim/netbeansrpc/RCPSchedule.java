/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.pedrokarim.netbeansrpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import java.util.TimerTask;
import org.openide.util.Exceptions;

/**
 *
 * @author karim
 */
public class RCPSchedule extends TimerTask {

    private static final int MAX_TITLE_LENGTH = 1024;
    public static String currentWindows = "";
    
    private DiscordRichPresence presence;
    private DiscordRPC lib;

    public RCPSchedule() {
        try {
            this.currentWindows = windowsTitleCurrency();
            
            createPresence(new String[]{"621768079386345477"});
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    
    
    @Override
    public void run() {
        try {
            this.currentWindows = windowsTitleCurrency();
            updateRCP(false);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public String windowsTitleCurrency() throws Exception {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, rect);
        System.out.println("rect = " + rect);
        
        return Native.toString(buffer);
    }

    
    public void createPresence(String[] args) {

        if (args.length == 0) {
            System.err.println("You must specify an application ID in the arguments!");
            System.exit(-1);
        }
        lib = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
        String applicationId = args.length < 1 ? "" : args[0];
        String steamId = args.length < 2 ? "" : args[1];

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        
        lib.Discord_Initialize(applicationId, handlers, true, steamId);

        updateRCP(true);
    }
    
    private void updateRCP(Boolean timestamp){
        
        DiscordRichPresence p = this.presence;

        if(timestamp != null && timestamp){
            p.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        }
        // presence.endTimestamp   = presence.startTimestamp + 20;
        
        String[] title = currentWindows.split("-");

        if(title.length == 2 && title[1].contains("NetBeans")){
            p.details = title[1] != null ? title[1].trim() : "Netbeans en cous...";
            p.state = title[0] != null ? title[0].trim() : "none";
        }else{
            p.details = currentWindows.contains("NetBeans") ? currentWindows.trim() : "Netbeans en cous...";
            p.state = "none";
        }
        
        p.largeImageKey = "first";
        p.largeImageText = "Netbeans EDI";
        p.smallImageKey = "java";
        p.smallImageText = "programming in java";
        // presence.partySize = 1;
        // presence.partyMax  = 4;
        lib.Discord_UpdatePresence(p);

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
