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
        String password = new String(console.readPassword("Group password (typing will be hidden): "));

        System.out.println(getCurrentTime() + "\t" + username + "\tInitializing...");

        while (true) {
            try {
                System.out.println(getCurrentTime() + "\t" + username + "\tLogging in...");
                while (true) {
                    snapchat = Snapchat.login(username, password, true);
                    if (snapchat != null) {
                        System.out.println(getCurrentTime() + "\t" + username + "\tLogged in.");
                    } else {
                        System.out.println(getCurrentTime() + "\t" + username + "\tFailed to log in.");
                        continue;
                    }
                    snapchat.setProxied(false);
                    System.out.println(getCurrentTime() + "\t" + username + "\tFetching snaps...");
                    fetchSnaps(username);
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
                            boolean result = setStory(username, storyFileName, timeToDisplay);
                            if (result) {
                                System.out.println(getCurrentTime() + "\t" + username + "\tPosted a snap by '" + fetchedSnap.getName().split("-")[1] + "'.");
                                fetchedSnap.delete();
                            } else {
                                System.out.println(getCurrentTime() + "\t" + username + "\tFailed to post a snap by '" + fetchedSnap.getName().split("-")[1] + "'.");
                            }
                        }
                        snapchat.setProxied(true);
                    }
                    try {
                        System.out.println(getCurrentTime() + "\t" + username + "\tSleeping...");
                        Thread.sleep(300000);   // 1000 milliseconds is one second.
                        System.out.println(getCurrentTime() + "\t" + username + "\tLogging in...");
                    } catch (InterruptedException ex) {
                        System.out.println(getCurrentTime() + "\t" + username + "\tThread could not sleep.");
                        Thread.currentThread().interrupt();
                        System.out.println(getCurrentTime() + "\t" + username + "\tLogging in...");
                    }
                }
            }
            catch (Exception e) {
                System.out.println(getCurrentTime() + "\t" + username + "\tRestarting loop due to: " + e);
                e.printStackTrace();
            }
        }
    }

    public static void fetchSnaps(String username) throws IOException {
        // Try fetching all snaps
        Snap[] snapObjs = snapchat.getSnaps();
        Snap[] downloadable = Snap.filterDownloadable(snapObjs);
        for (Snap s : downloadable) {
            if (s.isImage()) {
                System.out.println(getCurrentTime() + "\t" + username + "\tDownloading a snap (picture) from '" + s.getSender() + "'.");
                byte[] snapBytes = snapchat.getSnap(s);
                File snapFile = new File(username + "/" + s.getTime() + "-" + s.getSender() + "-" + s.getId() + ".jpg");
                FileOutputStream snapOs = new FileOutputStream(snapFile);
                snapOs.write(snapBytes);
                snapOs.close();
                snapchat.setSnapFlags(s, true, false, false);
            } /*else if (s.isVideo()) {
                System.out.println(getCurrentTime() + "\t" + username + "\tDownloading a snap (video) from '" + s.getSender() + "'.");
                byte[] snapBytes = snapchat.getSnap(s);
                File snapFile = new File(username + "/" + s.getTime() + "-" + s.getSender() + "-" + s.getId() + ".mp4");
                FileOutputStream snapOs = new FileOutputStream(snapFile);
                snapOs.write(snapBytes);
                snapOs.close();
                snapchat.setSnapFlags(s, true, false, false);
            }*/
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
