Allows you to store all your recipes in a easyish to read way.

Each recipe is a data class and has its own ingredients and details: name, time to cook, description of product, ingredients and their amounts, instructions, total carbs or nutritional information, and estimated cost.
There is a GUI that allows the user to see all the recipes with an image, and a button to add a new recipe or ingredient.
The adding page has an area to fill out the recipe details plus an area to add ingredients with its own small dialog window.
There is a details page for each recipe that shows all its details and has the option to go back to the recipes page, edit the recipe, or increment how many times it has been made.

Each ingredient is a data class and has details: name, where they are stored, the cost of each ingredient by its entire weight or volume, and the carbs or other nutritional information.
There is a GUI that allows the user to see all the ingredients in a table view with the ability to edit or delete any of them.
The adding page for ingredients has an area to fill out the ingredient details.

Users are able to edit and delete existing recipes and ingredients.
The application persists data using two local files on the computer to store recipes and ingredients, so it's available across sessions.

This was a personal project I began to make because I had needed a way to remember how to make all the different meals I like to eat without having to create a document and print out each one.
This is my second real project by myself and I started out this time by making an actual outline for the program and detailing what features I wanted and how it would be done.
However this was also my first time using Kotlin as I had never touched it before this project, so that meant that I was still used to writing the Java way and so I had to change how a few things were done along the way.
I also removed a couple of features that I realized while making the program were not going to either be very useful or would be too complicated for not much actual use.
Overall It has gone much better than my first project, and the use of JavaFX and FXML has really helped the GUI design part.
