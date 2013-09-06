LGE-Game-Engine
===============
A 2D Game Engine created using the LibGDX framework. Still a work in progress.


Requirements
===============
Requires Eclipse with ADT plugin for use.


Overview
===============
The game engine works based on 3 structures which should be extended to specify necessary game logic. These
structures are:

GameSprite - Controls drawing textures, animations and collisions between objects (collision mask usage)

GameObject - Controls logic based around a sprite. Makes it easy to switch animations for a given sprite based
             on input or events
             
Level      - The level objects contain multiple GameSprites and GameObjects. The main game loop is controlled
             through here along with the drawing of all objects.
             
            
Logic Overview
===============
creation

    Each GameObject, GameSprite and Level have an onCreate function where initialization logic should be placed, e.g,
    location, initial speed, direction etc.
draw

    Each GameSprite, GameObject and Level have a draw function which can be viewed to see how the drawing of them
    is handled. Level draw will call the draw function for each GameObject and each GameSprite not attached to a
    GameObject. Each GameObject then calls the draw function for each GameSprite attached to it. Each GameSprite
    will then draw the current frame of its texture at its current location. The draw functionality is accessed
    through an overridable onDraw function.
update

    The GameObject logic works in exactly the same way as the draw logic but is accessed through a number of
    overridable methods. These methods are preStep, onStep and postStep. They are called in the following order 
    with other built in logic (Called from the Level object holding these objects):
    
    
        preStep
        
        Kinematics are then updated for the GameObject
        
        onStep
        
        Changes in position, velocity, rotation and scale are passed down to the current GameSprite that is being used
          and the GameSprite update method is called.
          
        postStep
        
    For the GameSprite the update logic is not accessible/overiddable.
    
    All GameObject updates are called before GameSprites that are not contained in a GameObject.
    

Usage
==============
The engine needs to be used by starting a new libgdx project as outlined on their own website and then importing the
Engine structures you need. An example project can be seen here: https://github.com/kbristow/PlatformSoldier


Spine
==============
The engine has some rudimentary support for spine animations. See the game example for more information.
    
