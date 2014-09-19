GroupSnap for Snapchat
======================
By Joe Engelman (joengelm)
With libraries provided by Sam Stern (hatboysam), Liam Cottle (liamcottle), James St. Pierre (jamesst20), and Joe Engelman (joengelm)

What is GroupSnap?
------------------

GroupSnap is a "bot" program that monitors a Snapchat account for incoming snaps, and automatically reposts those snaps to its account's story. Essentially, this program allows you to create groups on Snapchat by setting up a new "group" account and using GroupSnap to share all received snaps on its story. For more info, just try GroupSnap out for yourself.

Usage
-----

1. Create a Snapchat account for your group by using an official client
2. Ensure privacy settings and friendships are set as desired
3. Start the GroupSnap program
	a. Compile with "javac -cp "lib/*:." groupsnap/*.java"
	b. Run with "java -cp "lib/*:." groupsnap/GroupSnap"
4. Enter the name of your new group account
5. Enter the password for this group
6. Done! GroupSnap will now repost all incoming snaps

Bugs
----

- GroupSnap does not currently support video.
- Occasionally, GroupSnap will fail to reach Snapchat's servers (due to IP blocking, connection issues, etc). In these cases, snap re-posting will be delayed.

To do
-----

- Add video support
- Add better controls for group administrator