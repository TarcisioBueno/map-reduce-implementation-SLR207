package rs;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.log4j.PropertyConfigurator;

public class MyFTPServer {

    public void setUpServer() {
    PropertyConfigurator.configure(MyFTPServer.class.getResource("/log4J.properties"));
    FtpServerFactory serverFactory = new FtpServerFactory();
    int port = 3459; // Replace 3456 with the desired port number

    ListenerFactory listenerFactory = new ListenerFactory();
    listenerFactory.setPort(port);

    ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
    connectionConfigFactory.setMaxLogins(30);
    connectionConfigFactory.setMaxThreads(30);

    serverFactory.addListener("default", listenerFactory.createListener());

    // Create a UserManager instance
    PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
    File userFile = new File("users.properties");
    if (!userFile.exists()) {
        try {
            if (userFile.createNewFile()) {
                System.out.println("File created: " + userFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    userManagerFactory.setFile(userFile); // Specify the file to store user details
    userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor()); // Store plain text passwords
    UserManager userManager = userManagerFactory.createUserManager();
    // Create a user
    BaseUser user = new BaseUser();
    user.setName("toto"); // Replace "username" with the desired username
    user.setPassword("tata"); // Replace "password" with the desired password
    //String username = user.getName();
    String homeDirectory = "/dev/shm/bueno-23";
    System.out.println(homeDirectory); 
    File directory = new File(homeDirectory); // Convert the string to a File object
    if (!directory.exists()) { // Check if the directory exists
        if (directory.mkdirs()) {
            System.out.println("Directory created: " + directory.getAbsolutePath());
        } else {
            System.out.println("Failed to create directory.");
        }
    }
    user.setHomeDirectory(homeDirectory);
    // Set write permissions for the user
    List<Authority> authorities = new ArrayList<>();
    authorities.add(new WritePermission());
    user.setAuthorities(authorities);
   

    // Add the user to the user manager
    try {
        userManager.save(user);
    } catch (FtpException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    // Set the user manager on the server context
    serverFactory.setUserManager(userManager);

    FtpServer server = serverFactory.createServer();

    // start the server
    try {
        server.start();
        System.out.println("FTP Server started on port " + port);
        
    } catch (FtpException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    }
}