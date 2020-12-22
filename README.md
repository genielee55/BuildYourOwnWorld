# BuildYourOwnWorld

BuildYourOwnWorld is a 2D tile-based world exploration engine with an overhead perspective, in which the player uses WASD to maneuver around obstacles and collect flowers to unlock a door and win the game. Each world is pseudorandomly generated (using the Random library), and saving/loading/replaying are possible with the Q (quit), L (load), and R (replay) keys.
 

Feature | Description
------- | -------
[MapGenerator](https://github.com/genielee55/BearMaps/blob/main/bearmaps/proj2d/server/handler/impl/RasterAPIHandler.java) | Pseudorandomly generates map with connected, distinct rooms and hallways using the StdDraw library.
[Engine](https://github.com/genielee55/BearMaps/blob/main/bearmaps/proj2d/AugmentedStreetMapGraph.java) | Implements interactive component to game by gathering input string from user to move avatar accordingly and storing avatar's movements in txt file (for saving/loading features).
[TileSet](https://github.com/genielee55/BearMaps/blob/main/bearmaps/proj2ab/KDTree.java) | Contains tile objects for front-end features of the game.
