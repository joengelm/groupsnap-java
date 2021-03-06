GroupSnap for Snapchat
======================

- By Joe Engelman (joengelm)
- With libraries provided by Sam Stern (hatboysam), Liam Cottle (liamcottle), James St. Pierre (jamesst20), and Joe Engelman (joengelm)

What is GroupSnap?
------------------

GroupSnap is a "bot" program that monitors a Snapchat account for incoming snaps, and automatically reposts those snaps to its account's story. Essentially, this program allows you to create groups on Snapchat by setting up a new "group" account and using GroupSnap to share all received snaps on its story. For more info, just try GroupSnap out for yourself.

Usage
-----

1. Create a Snapchat account for your group by using an official client
2. Ensure privacy settings and friendships are set as desired
3. Create a directory "<username>" at the same level as this readme file
  - NOTE: You _must_ name your directory the username of each GroupSnap account you plan on running
  - NOTE: You _must_ place this directory at the same level as this readme file
4. Start the GroupSnap program
    + Unix:
        - Compile with ```javac -cp "lib/*:." groupsnap/*.java```
        - Run with ```java -cp "lib/*:." groupsnap/GroupSnap```
    + Windows:
        - Compile with ```javac -cp "lib\*" groupsnap/*.java```
        - Run with ```java -cp "lib/*;." groupsnap/GroupSnap```
5. Enter the name of your new group account
6. Enter the password for this group
7. Done! GroupSnap will now repost all incoming snaps

Bugs
----

- GroupSnap does not currently support video.
- Occasionally, GroupSnap will fail to reach Snapchat's servers (due to IP blocking, connection issues, etc). In these cases, snap re-posting will be delayed.

To do
-----

- Add video support
- Add better controls for group administrator