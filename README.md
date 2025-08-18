# Delphi Menu plugin
A plugin for making menus with XML, styling them with CSS and adding functionality with either Java or JavaScript.

## Links
- Documentation: https://dev.juliewoolie.com/menus
- Dev journal: https://juliewoolie.com/delphi/devjournal/
- Hangar: https://hangar.papermc.io/JulieWoolie/delphi
- Modrinth: https://modrinth.com/plugin/delphi

## Features
* Ability to create HTML-like menus with XML and CSS (With limited native SCSS integration)
* Full support for CSS selectors (Including pseudo elements like `:hover` and `:active`)
* Fully documented Java API and JavaScript integration for easy scripting
* Contuing development and new features being added all the time.
* Extensive documentation (at [dev.juliewoolie.com](https://dev.juliewoolie.com/menus))
* Devtools to help you create menus and fix issues.
* All Delphi commands can be used in the `/execute` command and by datapack functions!

## Building
After cloning this repo, run `gradlew :plugin:shadowJar` to build the plugin which you'll find 
in `plugin/build/libs`.

You can build each module separately with `graldew :<module name>:build` and run tests with `gradlew test`

## Modules
- `api`: Outlines the API developers can use to interact with the plugin.
- `chimera`: SCSS implementation and style system.
- `dom` (`delphidom`): The Document Object Model implementation. (Uses `chimera`)
- `nlayout` (`nlayout`): The layout engine.
- `render` (`delphirender`): The rendering engine.
- `plugin` (`delphiplugin`): The Paper MC plugin.
- `hephaestus`: JavaScript integration

## Contributing
If you want to contribute to this repository, fork this repository, and submit any 
changes you want via pull request.
