# Project setup help

This is a quick overview how to setup the project for development.

## 1. Make sure the plugin development plugin is enabled  

Start IDEA, go to Settings->Plugins and make sure that `Plugin DevKit` is installed and enabled.
If it's not, install it now.

## 2. Clone the project

## 3. Import the project into IDEA 

Select "Import Project" and navigate to the cloned repository when prompted. 
	* Chose "From existing sources" when prompted for a model
	* At the SDK step (if you dont have a plugin SDK yet): Click `+` to add an SDK and select "IntelliJ Platform Plugin SDK"
		- Navigate to your IDEA installation and select the installation directory.
		- Afterwards select a JDK when prompted
	* At the SDK step select your Plugin SDK (if you followed the previous step select the newly created SDK)
    * The remaining options can be left at default
    
## 4. Change the project type

Open the projects iml file (it should be named `gitflow4idea.iml` by default) and replace its contents with this:

```
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

## 5. Add git4idea dependency

Open the module settings and navigate to modules -> gitflow4idea (or your project name here) -> dependencies. 
Click add -> "JARs or directories" and add `git4idea.jar`.
This can be found in your IDEA installation directory under `plugins/git4idea/lib`.
Change the scope of the added JAR to **provided**.

## 6. Create a run configuration

Go to Run/Debug configurations and create a new configuration of the type `Plugin`. Under "Use classpath of module" select the project (`gitflow4idea` by default).
Run it. A new IDEA instance should start with the plugin running. 

And that's it. You can now make changes to the source and run them.


