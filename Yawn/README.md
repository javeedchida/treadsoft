#Yawn 1.0

At the time of this README file's creation, this application is still in development. 

##You Should Know
If all you need is another database client, you need much more than this application. 
I recommend you get something more robust like Squirrel SQL instead. If you still 
feel the need to know more, read on...

Here are the no-so-ambitious goals for Yawn.
* The typical use case is when you want a simple SQL execution environment that 
lets you run the same SQL against multiple database environments (dev, test, 
stage, prod) in a fairly quick way.
* Yawn is intended to be command-window centric. You have one single command window. 
Any and every command you enter there will be stored (logged) with a timestamp 
and connection information. The idea is you don't have to worry about having to 
save your SQL. Log file names will be timestamped with a sort-friendly naming 
convention.
* Yawn supports selective command execution (highlight and click Go)
* You manage your configuration for various connections in a yawn-connections.xml file. To 
run your SQL on a specific database, just make sure it is configured, then click the radio 
button for your target database before you click Go.
* Be careful if you find yourself yawning when you use yawn. It is very easy to inadvertently 
run unintended SQL, but this is where the auto-logging becomes useful so you can always figure out what exactly you did.

##Requires
JDK 1.7.x

##Usage
From the command line, run:
```
java -jar Yawn-1.0.jar c:\temp\yawn-connections.xml
```

###Example yawn-connections.xml
```
<connections>
    <connection name="my-database">
	<driverJar>ojdbc6.jar</driverJar>
	<driverClass>oracle.jdbc.OracleDriver</driverClass>
	<jdbcConnectionString>jdbc:oracle:thin:@//localhost:1571/service</jdbcConnectionString>
	<username>username</username>
	<password>password</password>
    </connection>
</connections>
```
*Yawn!
