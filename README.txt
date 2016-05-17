user: 009445384, (Del Castillo, Kyle)
user: 010106096, (Vo, Huy)

NOTES FROM KYLE:

The file is a little bit big due to the music added.
Almost all required functionalities(open/save/remove/etc) can be accessed in either the Menu bar, or through the buttons.
I have added A LOT of extra functionalities. It doesn't affect the required functionalities.
I tried to make the applet as real as possible. I hope I don't get deducted for this.

Here's a list of extra functionalities:

- You can open a new window using File Menu -> Open New Window. This is for easier Server/Client testing.
- Implemented a Random N Shape Generator. Both with and without colors.
- Implemented "click" and "hover" sounds for buttons and menu items
- Implemented music player. This is just for my own practice.
- Implemented "clear" option, which clears the entire canvas and table.
- Implemented JPopupMenu for Canvas. Right clicking the canvas/shape will lead to a popup menu.
	+ The popup menu includes add border, copy shape, paste shape, and remove shape
	+ Add border, copy/paste shape only works for Rectangles and Ovals.
	+ It also only works for server/current side as of right now
	+ Copy/pasting shapes seems to add multiple shapes after clicking, still can't debug it right.

- All buttons are disabled when in Client mode except some Menu items(music, background change, save, about)

All of the requirements seems to have been met. I ran the code and the jar on both Windows and Mac and it seems to work fine. 
The Move Front/Back requirements just says to move the selected shape from one end to the other.
This means that the implementation moves it to the very top or to the very bottom.

Any hints or design approach advise will be highly appreciated!