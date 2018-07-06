package com.dyl.gw;

import com.dyl.gw.netty.server.PetServer;

/**
 * Description: Main
 * Author: DIYILIU
 * Update: 2018-07-06 09:44
 */
public class Main {

    public static void main(String[] args) {

        PetServer server = new PetServer();
        server.setPort(5006);
        server.init();
    }
}
