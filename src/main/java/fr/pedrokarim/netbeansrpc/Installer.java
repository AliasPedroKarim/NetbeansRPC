/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.pedrokarim.netbeansrpc;

import java.util.Date;
import java.util.Timer;
import java.util.logging.Logger;
import org.openide.modules.ModuleInstall;
import org.openide.windows.OnShowing;


/**
 *
 * @author karim
 */

@OnShowing
public class Installer extends ModuleInstall implements Runnable {

    public static final Logger log = Logger.getLogger("Installer");
    private static Timer timer;

    public Installer() {
        super();
        
        timer = new Timer();
        
        RCPSchedule timetask = new RCPSchedule();
        
        timer.scheduleAtFixedRate(timetask, new Date(), 12000l); // 10s timeout
        
        System.out.println("[PluginRPC] NetbeansRPC has loaded.");
    }

    @Override
    public void run() {
        
    }

    @Override
    public void close() {
        System.out.println("[PluginRPC] NetbeansRPC is closed.");
    }
}
