# Project Setup Help
This is a quick overview how to setup the project for development.

## 1. Make Sure the Plugin Development Plugin Is Enabled  
Start IDEA, go to *Settings -> Plugins* and make sure that `Plugin DevKit` is installed and enabled. If it's not, install it now.

## 2. Clone Project from GitHub
Fork the original repository on GitHub, then clone your fork of the project.

## 3. Import Project into IDEA
Select the *Import Project* option (e.g. by pressing shift twice and entering "import project") and navigate to the cloned repository directory when prompted. 

### Model
Choose "Create from existing sources" when prompted for a model.

### SDK Setup 
If you dont have a plugin SDK yet, click `+` to add an SDK and select *IntelliJ Platform Plugin SDK*
    
1. Navigate to your IDEA installation and select the installation directory.
2. Afterwards select a JDK when prompted.
    
Select your plugin SDK as the one to use.

### Other
The remaining options can be left at default.
    
## 4. Change the Project Type
Open the project's .iml file (it should be named `gitflow4idea.iml` by default) and replace its contents with this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="PLUGIN_MODULE" version="4">
  <component name="DevKit.ModuleBuildProperties" url="file://$MODULE_DIR$/META-INF/plugin.xml" />
  <component name="NewModuleRootManager" LANGUAGE_LEVEL="JDK_1_6" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/src" isTestSource="false" />
    </content>
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
```

Then close and reopen the project to apply the changes.

## 5. Add Dependencies
1. Open the module settings and navigate to *Modules -> gitflow4idea (or your project name here)* and select the *Dependencies* tab. 
2. Click *Add -> "JARs or directories"* and select your IDEA installation directory.
3. Add the following JARs:
- `plugins/git4idea/lib/git4idea.lib`
- `plugins/tasks/lib/tasks*.jar`   
4. Change the scope of the added JARs to **provided**.

## 6. Create a Run Configuration
Go to *Run/Debug configurations* and create a new configuration of type `Plugin`. Under "Use classpath of module" select the project (`gitflow4idea` by default). Click run. A new IDEA instance should start with the plugin running. 

And that's it. You can now make changes to the source and run them.

## Notes & Hints

### Language Level
This project is written to target Java 6, so make sure to set the project language level appropriately to avoid accidentally using newer features. You can do so in the module settings under "modules -> gitflow4idea -> sources -> Language level".
