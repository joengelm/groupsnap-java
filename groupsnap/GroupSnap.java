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
        System.out.println("Tip: Have you created your \"fetched-snaps\" directory as specified in the readme?");
        System.out.println("Group username: ");
        String username = scanner.nextLine();
        Console console = System.console();
        String password = new String(console.readPassword("Group password (typing will be hidden): "));

        System.out.println(getCurrentTime() + "\tInitializing...");

        while (true) {
            try {
                System.out.println(getCurrentTime() + "\tLogging in...");
                while (true) {
                    snapchat = Snapchat.login(username, password, true);
                    if (snapchat != null) {
                        System.out.println(getCurrentTime() + "\tLogged in.");
                    } else {
                        System.out.println(getCurrentTime() + "\tFailed to log in.");
                        continue;
                    }
                    snapchat.setProxied(false);
                    System.out.println(getCurrentTime() + "\tFetching snaps...");
                    fetchSnaps();
                    File dir = new File("fetched-snaps/");
                    File[] directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        snapchat.setProxied(false);
                        for (File fetchedSnap : directoryListing) {
                            String storyFileName = "fetched-snaps/" + fetchedSnap.getName();
                            int timeToDisplay = Integer.parseInt(fetchedSnap.getName().split("-")[0]);
                            boolean result = setStory(username, storyFileName, timeToDisplay);
                            if (result) {
                                System.out.println(getCurrentTime() + "\tPosted a snap by '" + fetchedSnap.getName().split("-")[1] + "'.");
                                fetchedSnap.delete();
                            } else {
                                System.out.println(getCurrentTime() + "\tFailed to post a snap by '" + fetchedSnap.getName().split("-")[1] + "'.");
                            }
                        }
                        snapchat.setProxied(true);
                    }
                    try {
                        System.out.println(getCurrentTime() + "\tSleeping...");
                        Thread.sleep(300000);   // 1000 milliseconds is one second.
                        System.out.println(getCurrentTime() + "\tLogging in...");
                    } catch (InterruptedException ex) {
                        System.out.println(getCurrentTime() + "\tThread could not sleep.");
                        Thread.currentThread().interrupt();
                        System.out.println(getCurrentTime() + "\tLogging in...");
                    }
                }
            }
            catch (Exception e) {
                System.out.println(getCurrentTime() + "\tRestarting loop due to: " + e);
            }
        }
    }

    public static void fetchSnaps() throws IOException {
        // Try fetching all snaps
        Snap[] snapObjs = snapchat.getSnaps();
        Snap[] downloadable = Snap.filterDownloadable(snapObjs);
        for (Snap s : downloadable) {
            // TODO Support video
            if (s.isImage()) {
                System.out.println(getCurrentTime() + "\tDownloading a snap from '" + s.getSender() + "'.");
                byte[] snapBytes = snapchat.getSnap(s);
                File snapFile = new File("fetched-snaps/" + s.getTime() + "-" + s.getSender() + "-" + s.getId() + ".jpg");
                FileOutputStream snapOs = new FileOutputStream(snapFile);
                snapOs.write(snapBytes);
                snapOs.close();
                snapchat.setSnapFlags(s, true, false, false);
            }
        }
    }
    
    public static boolean setStory(String username, String filename, int time)
            throws FileNotFoundException {
        boolean video = false; //TODO upload video snaps from command line.
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
