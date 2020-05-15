Hello! Available commands are:

!help  - I will send you a message with a list of available commands.
!hello - I will introduce myself.
!ping  - I'll reply 'Pong!' if I am online!

These are the commands related to TODO events:
```css
!todo new    "EVENT"
!todo add	"EVENT" Water the garden
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

