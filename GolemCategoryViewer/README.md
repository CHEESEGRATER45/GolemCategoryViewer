# Golem Category Viewer (Fabric, Minecraft 26.2, client-side)

Adds a colour-coded line to every item tooltip showing the category the SmartCopperGolem plugin
puts it in:

    ■ Category: Wood  (220 items)        <- coloured; same category = same colour
    □ No category  (golems match it only to itself)

Hover any item in your inventory, a chest, creative tabs, anywhere a tooltip shows. It only reads
categories; it changes nothing in the game and doesn't need to be on the server.

## Build and install
Needs JDK 25 and Gradle 9.x (no wrapper included; run `gradle wrapper` once if you want one).

    gradle build

Put `build/libs/golem-category-viewer-1.0.0.jar` and Fabric API in your `mods/` folder.
Versions are in `gradle.properties`; if Gradle can't resolve one, take the current numbers
from https://fabricmc.net/develop.

## Keeping it in sync with the plugin
The mod ships with the same 40 categories as the plugin. After you edit the plugin's `categories.yml`:

    python3 tools/yaml_to_json.py categories.yml golem-categories.json

and put the result in your Minecraft folder as `config/golem-categories.json`. It is used instead of the
bundled copy the next time the game starts. No rebuild needed.

## License
MIT, see `LICENSE`.
