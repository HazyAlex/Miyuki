# Miyuki

Discord Bot using JDA.\
I made it for handling simple TODO lists and making polls.

# Commands

!help  - Sends you a message with a list of available commands.\
!hello - Introduces itself.\
!ping  - Replies 'Pong!' if it's online!

These are the commands related to TODO events:
```css
!todo new    "EVENT"
!todo add    "EVENT" Water the garden
!todo list   "EVENT"
!todo mark   "EVENT" [Number]
!todo remove "EVENT" [Number]
!todo clear  "EVENT"
```
You don't need the quotes around "EVENT" if your event is written with a word, e.g. AlexCampingTrip

These are the commands related to Poll events:
```css
!poll NAME { OPTION1;OPTION2 }
```
Example:
```css
!poll "Chocolate vs Strawberry?" { Chocolate obviously; Strawberry for sure! }
```
You can then add a reaction to one of the options and after a delay the results will be announced!

# Requirements

Java 11 or higher\
JDBC compliant Database

# Instructions

## Use:

Put your token in "settings/token.txt".

Change the "settings/db.properties" according to your Database.

Run "java -jar Miyuki-1.0-all.jar"\
(Make sure you are using Java 11+, run java -version to check it)
