***Curently under development***

#Yawn 1.0

Yawn is a very basic JDBC SQL client that preserves and organizes the history (and more) of SQL statements that pass through it.

## You Should Know...
If all you need is another database client, you need much more than this application. I recommend you get something more robust like Squirrel SQL instead. If you still want to know more about Yawn, read on.

## Features
### Summary
* one command window
* automatic logging of queries, results, and more
* support for multiple connections with click-and-fire capability
* XML-configurable connections and logging

### Details
* Yawn is intended to be command-window centric. You have one and only one command window. 
* Every SQL statement you run will be remembered in a *run subfolder* for the selected database. So you don't have to worry about having to remember to save any of your SQL for later reference. Every run subfolder has the following files written to it for each run.
  - run.sql containing the SQL statement that was run
  - output.csv containing the result set in an Excel-friendly format
  - run.log containing the result set metadata if the statement ran successfully, else the error message
* *Run subfolders* are created under a folder representing the database against which the statement was executed.
* *Run subfolders* are given timestamped names, making them sort-friendly.
* You manage your configuration for various connections in a `yawn-connections.xml` file. 
* To run your SQL statement on a specific database configured in `yawn-connections.xml`, click the radio button for that database before you click *Go*.
* Be careful if you find yourself yawning when you use Yawn. It is very easy to inadvertently run unintended SQL. On the bright side, the auto-logging becomes useful if you do enter SQL hell - that way you can figure out what command you ran, when you ran it, and what database you ran it against.

### Typical Use Case For Yawn
* Yawn might be useful when you want a simple SQL execution environment that lets you run the same SQL statement against multiple database environments (dev, test, stage, prod) in a fairly quick manner. Yawn is good for spot checking result set metadata.

## Usage
From the command line, run:
```
java -jar Yawn-1.0.jar c:\temp\yawn-connections.xml
```

### Example Configuration File
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
*Yawn!*
