todolist
========

To build the project run:

> ./gradlew build


Client
==================

To Sign up

> ./gradlew :Client:run -Pargs="--mode signup"

---------------------------------

To Add a Entry 

> ./gradlew :Client:run -Pargs="--mode add --user your_username --pass your_password"

---------------------------------

To View your ToDo List

> ./gradlew :Client:run -Pargs="--mode view --user your_username --pass your_password"



Server
=====================

* The speed is used for testing so you can speed the time up so you can reach your destination fast. 

* Don't use spee if you want a normal speed, and at the same time do not use too much speed beacuse it might end in jumping your time

* The server will notify the users via mail 1 hour before the time

> ./gradlew :Server:run -Pargs="--speed 150 --user your@email.adrress --pass your_password"
