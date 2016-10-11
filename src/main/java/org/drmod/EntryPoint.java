package org.drmod;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class EntryPoint {

    public static void main(String[] args) {
        ApplicationContext applicationContext;
        if (args.length != 0) {
            applicationContext = new FileSystemXmlApplicationContext(args[0]);
        } else {
            applicationContext = new ClassPathXmlApplicationContext("spring-config.xml");
        }

        Run run = (Run) applicationContext.getBean("run");
        run.start();
    }

}
