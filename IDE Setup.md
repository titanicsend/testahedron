IDE Setup for LX Studio IDE
==

LX Studio IDE currently uses the temurin-11 SDK (Java 11).


## IntelliJ 
If you'd like to try IntelliJ, downloa IntelliJ IDEA **Community Edition** [here](https://www.jetbrains.com/idea/download/).

### Java SDK

Open the project in IntelliJ using `LXStudio-IDE/` as the root.

![Open Project](assets/IDE Setup/Open Project.png)

You will need to download the right Java SDK. Most people should see IntelliJ automatically prompt them to do it for them - accept these prompts.

![Auto install Java SDK](assets/IDE Setup/Auto install Java SDK.png)


### Manual config of Java SDK (shouldn't be needed)

You may need to select the Java SDK in the Project Structure:

![Project Structure](assets/IDE Setup/SDK in Project Structure.png)

As well as in the App Run Config:

![App config menu](assets/IDE Setup/App configuration menu.png)

![Run Configurations](assets/IDE Setup/Run Configurations.png)

### Recognize LX Studio JSON file extensions

If can be handy to edit LX Studio's JSON config files in the IDE. Add the .lfp and .lxp extension to be recognized as JSON.

Open preferences (⌘-, on Mac) and go to Editor → File Types → JSON.

![JSON File Types](assets/IDE Setup/JSON File Types.png)

### Optional Plugins

Jeff's enjoying the folllowing (he comes from Sublime and vim)

* CodeGlance
* Rainbow Brackets
* IdeaVim
* CSV
* KeyPromoter X
* Python Community Edition

## Eclipse

If Eclipse is like a warm snuggie to you, we'd appreciate you adding any SDK and evirnoment config tips here.
