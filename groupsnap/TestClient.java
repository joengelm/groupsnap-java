package groupsnap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class TestClient {
    private static Snapchat snapchat;
    
    public static void main(String[] args) throws Exception {
        // Get username and password
        Scanner scanner = new Scanner(System.in);
        System.out.println("Snapchat username: ");
        String username = scanner.nextLine();
        System.out.println("Snapchat password: ");
        String password = scanner.nextLine();

        // Test logging in
        System.out.println("Logging in...");
        snapchat = Snapchat.login(username, password, false);
        if (snapchat != null) {
            System.out.println("Logged in.");
        } else {
            System.out.println("Failed to log in.");
            return;
        }

        snapchat.setProxied(false);
        // Ask the user what they want to do
        System.out.println();
        System.out.println("Choose an option:");
        System.out.println("\t1) Download un-viewed snaps");
        System.out.println("\t2) Send a snap");
        System.out.println("\t3) Set a Story");
        System.out.println("\t4) Download Stories");
        System.out.println();

        int option = scanner.nextInt();
        scanner.nextLine();
        switch (option) {
            case 1:
                fetchSnaps();
            break;
            case 2:
                System.out.println("Enter path to image file:");
                String snapFileName = scanner.nextLine();
                System.out.println("Enter recipient Snapchat username:");
                String recipient = scanner.nextLine();
                sendSnap(username, recipient, snapFileName);
            break;
            case 3:
                System.out.println("Enter path to image file:");
                String storyFileName = scanner.nextLine();
                setStory(username, storyFileName);
            break;
            case 4:
                Story[] storyObjs = snapchat.getStories();
                Story[] downloadable = Story.filterDownloadable(storyObjs);
                for (Story s : downloadable) {
                  String extension = ".jpg";
                  if(!s.isImage()){
                    extension = ".mp4";
                  }
                  System.out.println("Downloading story from " + s.getSender());
                  byte[] storyBytes = Snapchat.getStory(s);
                  File storyFile = new File(s.getSender() + "-" + s.getId() + extension);
                  FileOutputStream storyOs = new FileOutputStream(storyFile);
                  storyOs.write(storyBytes);
                  storyOs.close();
                }
                System.out.println("Done.");
            break;
            default:
                System.out.println("Invalid option.");
            break;
        }

    }

    public static void fetchSnaps() throws IOException {
        // Try fetching all snaps
        System.out.println("Fetching snaps...");
        Snap[] snapObjs = snapchat.getSnaps();
        Snap[] downloadable = Snap.filterDownloadable(snapObjs);
        for (Snap s : downloadable) {
            // TODO(samstern): Support video
            if (s.isImage()) {
                System.out.println("Downloading snap from " + s.getSender());
                byte[] snapBytes = snapchat.getSnap(s);
                File snapFile = new File(s.getTime() + "-" + s.getSender() + "-" + s.getId() + ".jpg");
                FileOutputStream snapOs = new FileOutputStream(snapFile);
                snapOs.write(snapBytes);
                snapOs.close();
            } else {
                System.out.println("Downloading snap video from " + s.getSender());
                byte[] snapBytes = snapchat.getSnap(s);
                File snapFile = new File(s.getTime() + "-" + s.getSender() + "-" + s.getId() + ".mp4");
                FileOutputStream snapOs = new FileOutputStream(snapFile);
                snapOs.write(snapBytes);
                snapOs.close();
            }
        }
        System.out.println("Done.");
    }

    public static void sendSnap(String username, String recipient, String filename)
            throws FileNotFoundException {

        // Get file
        File file = new File(filename);

        // Try sending it
        List<String> recipients = new ArrayList<String>();
        recipients.add(recipient);

        // Send and print
        System.out.println("Sending...");
        boolean postStory = false; //set as true to make this your story as well...

        // TODO(samstern): User-specified time, not automatically 10 seconds
        boolean result = snapchat.sendSnap(file, recipients, false, postStory, 10);
        if (result) {
            System.out.println("Sent.");
        } else {
            System.out.println("Could not send.");
        }
    }
    
    public static void setStory(String username, String filename)
            throws FileNotFoundException {
        boolean video = false;
        if (filename.substring(filename.length() - 4).equals("mp4")) {
            video = true;
        }
        int time = 10;
        try {
            time = Integer.parseInt(filename.split("-")[0]);
        } catch (Exception e) {}

        File file = new File(filename);
        boolean result = snapchat.sendStory(file, video, time, "My Story");
        System.out.println(result);
    }
}
