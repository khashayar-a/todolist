todolist
========

To build the project run:

> ./gradlew build

---------------------------------

To Sign up

> ./gradlew :Client:run -Pargs="--mode signup"

---------------------------------

To Add a Entry 

> ./gradlew :Client:run -Pargs="--mode add --user your_username --pass your_password"

---------------------------------

To View your ToDo List

> ./gradlew :Client:run -Pargs="--mode view --user your_username --pass your_password"

