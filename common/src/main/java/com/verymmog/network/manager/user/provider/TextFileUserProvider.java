package com.verymmog.network.manager.user.provider;

import com.verymmog.model.User;
import com.verymmog.network.manager.user.provider.InMemoryUserProvider;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextFileUserProvider extends InMemoryUserProvider {

    private Logger logger = Logger.getLogger(getClass());

    public TextFileUserProvider(File file) {
        try {
            Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(file)));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] spl = line.split(":");

                User u = new User(spl[0].trim(), spl[1].trim());

                addUser(u);
                logger.debug("Loading user " + u.getLogin());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
