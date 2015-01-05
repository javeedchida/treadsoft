At the time of this README file creation, this application is still in development. 

NOTE: You don't need this program. Use something more robust like Squirrel SQL instead. If you still feel the need to know more, read on...


These are the no-so-ambitious goals for Yawn.
* The typical use case for Yawn is when you want a simple SQL execution environment that lets you run the same SQL against multiple database environments (dev, test, stage, prod) in a fairly quick way.
* Yawn is intended to be command-window centric. You have one single command window. Any and every command you enter there will be stored (logged) with a timestamp and connection information so you never forget what you ran.
You can clear the command window without impacting the log.
* You manage your various connections in a simple yawn-connections.xml file. The user interface lets you pick your target database using a radio-button metaphor.
* Can be dangerous if you don't know what you're doing, but that is where the auto-logging becomes useful so you can always figure out what exactly you did.

Yawn!