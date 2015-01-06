#Yawn 1.0

Yawn is a very basic JDBC SQL Client. 

## Currently under development

## You Should Know...
If all you need is another database client, you need much more than this application. I recommend you get something more robust like Squirrel SQL instead. If you still feel the need to know more, read the section below titled *Typical Use Case For Yawn*.

## Features
### Summary
* one command window
* automatic logging of queries and resultset metadata annotated with target connection string and timestamp
* support for multiple connections with click-and-fire capability
* XML-configurable connections and logging

## Details
* Yawn is intended to be command-window centric. You have one and only one command window. 
Any and every command you enter there will be stored (logged) with a timestamp and its target connection string. So you don't have to worry about having to save your SQL to a file for later reference. Log file names are timestamped with a sort-friendly naming convention.
* You manage your configuration for various connections in a yawn-connections.xml file. To run your SQL on a specific database, just make sure it is configured, then click the radio button for your target database before you click Go.
* Be careful if you find yourself yawning when you use yawn. It is very easy to inadvertently run unintended SQL. On the bright side, the auto-logging becomes useful if you do enter SQL hell - that way you can figure out what command you ran, when you ran it, and what database you ran it against.

## Typical Use Case For Yawn
* Yawn might be useful when you want a simple SQL execution environment that lets you run the same SQL statement against multiple database environments (dev, test, stage, prod) in a fairly quick manner. Yawn is good for spot checking result set metadata.

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
*Yawn!*
