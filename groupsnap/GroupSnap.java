package groupsnap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.*;

public class GroupSnap {
    private static Snapchat snapchat;
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n\n--- GroupSnap for Snapchat (v1.0) by Joe Engelman ---");
        System.out.println("Tip: Have you created your \"<username>/\" directory as specified in the readme?\n");
        System.out.println("Group username: ");
        String username = scanner.nextLine();
        Console console = System.console();
        String password = new String(console.readPassword("Group password (typing will be hidden): \n"));

        File logfile = new File("gs.log");
        Logger l = new Logger(logfile, username);

        l.log("Initializing...");

        while (true) {
            try {
                l.log("Logging in...");
                while (true) {
                    snapchat = Snapchat.login(username, password, true);
                    if (snapchat != null) {
                        l.log("Logged in.");
                    } else {
                        l.log("Failed to log in.");
                        continue;
                    }
                    snapchat.setProxied(false);
                    l.log("Fetching snaps...");
                    fetchSnaps(username, l);
                    File dir = new File(username + "/");
                    File[] directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        snapchat.setProxied(false);
                        for (File fetchedSnap : directoryListing) {
                            String storyFileName = username + "/" + fetchedSnap.getName();
                            if (fetchedSnap.getName().substring(0,1).equals(".")) {
                                continue;
                            }
                            int timeToDisplay = Integer.parseInt(fetchedSnap.getName().split("-")[0]);
                            String userWhoSentSnap = fetchedSnap.getName().split("-")[1];
                            boolean result = setStory(username, storyFileName, timeToDisplay);
                            if (result) {
                                l.log("Posted a snap by '" + userWhoSentSnap + "'.");
                                fetchedSnap.delete();
                            } else {
                                l.log("Failed to post a snap by '" + userWhoSentSnap + "'.");
                            }
                        }
                        snapchat.setProxied(true);
                    }
                    try {
                        int sleepSeconds = 300;
                        l.log("Sleeping for " + sleepSeconds + " seconds...");
                        Thread.sleep(sleepSeconds*1000);   // 1000 milliseconds is one second.
                        l.log("Logging in...");
                    } catch (InterruptedException ex) {
                        l.log("Thread could not sleep.");
                        Thread.currentThread().interrupt();
                        l.log("Logging in after sleep interruption...");
                    }
                }
            }
            catch (Exception e) {
                l.log("Restarting loop due to: " + e);
            }
        }
    }

    public static void fetchSnaps(String username, Logger l) throws IOException {
        // Try fetching all snaps
        Snap[] snapObjs = snapchat.getSnaps();
        Snap[] downloadable = Snap.filterDownloadable(snapObjs);
        for (Snap s : downloadable) {
            if (s.isImage()) {
                l.log("Downloading a snap (picture) from '" + s.getSender() + "'.");
                byte[] snapBytes = snapchat.getSnap(s);
                File snapFile = new File(username + "/" + s.getTime() + "-" + s.getSender() + "-" + s.getId() + ".jpg");
                FileOutputStream snapOs = new FileOutputStream(snapFile);
                snapOs.write(snapBytes);
                snapOs.close();
                snapchat.setSnapFlags(s, true, false, false);
            } /* else if (s.isVideo()) {
                l.log("Downloading a snap (video) from '" + s.getSender() + "'.");
                byte[] snapBytes = snapchat.getSnap(s);
                File snapFile = new File(username + "/" + s.getTime() + "-" + s.getSender() + "-" + s.getId() + ".mp4");
                FileOutputStream snapOs = new FileOutputStream(snapFile);
                snapOs.write(snapBytes);
                snapOs.close();
                snapchat.setSnapFlags(s, true, false, false);
            } */
        }
    }
    
    public static boolean setStory(String username, String filename, int time)
            throws FileNotFoundException {
        boolean video = false;
        if (filename.substring(filename.length() - 4).equals("mp4")) {
            video = true;
        }
        File file = new File(filename);
        boolean result = snapchat.sendStory(file, video, time, "My Story");
        return result;
    }

    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}
