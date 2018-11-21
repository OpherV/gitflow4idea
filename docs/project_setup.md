# Setup Guide
This document shall assist you in setting up the development project in IntelliJ IDEA. There are two ways of setting up the project:
1. "Classic"
2. Gradle

# Classic Setup

## Project setup help

This is a quick overview how to setup the project for development.

### 1. Make sure the plugin development plugin is enabled  

Start IDEA, go to *Settings -> Plugins* and make sure that `Plugin DevKit` is installed and enabled.
If it's not, install it now.

### 2. Clone the project from GitHub

Typically you check out your fork of the project on GitHub here.

### 3. Import the project into IDEA 

Select the *Import Project* option (e.g. by pressing shift twice and entering "import project") 
and navigate to the cloned repository directory when prompted. 

#### Model

Chose "From existing sources" when prompted for a model.

#### SDK Setup 

If you dont have a plugin SDK yet, click `+` to add an SDK and select *IntelliJ Platform Plugin SDK*
    
1. Navigate to your IDEA installation and select the installation directory.
2. Afterwards select a JDK when prompted
    
Select your plugin SDK as the one to use.

#### Other

The remaining options can be left at default
    
### 4. Change the project type

Open the projects iml file (it should be named `gitflow4idea.iml` by default) and replace its contents with this:

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

### 5. Add git4idea dependency

1. Open the module settings and navigate to *Modules -> gitflow4idea (or your project name here)* and select the *Dependencies* tab. 
2. Click add -> "JARs or directories" and add `git4idea.jar`.
    This can be found in your IDEA installation directory under `plugins/git4idea/lib`.
3. Change the scope of the added JAR to **provided**.

### 6. Create a run configuration

Go to Run/Debug configurations and create a new configuration of the type `Plugin`. Under "Use classpath of module" select the project (`gitflow4idea` by default).
Click run. A new IDEA instance should start with the plugin running. 

And that's it. You can now make changes to the source and run them.

### Notes & hints

#### Language level

This project is written to target Java 6, so make sure to set the project language level appropriately
 to avoid accidentally using newer features. You can do so in the module settings under "modules -> gitflow4idea -> sources -> Language level".

# Gradle

## Prerequisites
- The environment variable `JAVA_HOME` is set to a valid Java environment.
- The *Gradle* plugin is enable in IntelliJ IDEA.

## Setup Steps
1. Fork the original repository and checkout the fork.
2. Import the project into IntelliJ IDEA.
3. Choose *"Import from external model" > Gradle*.
4. Set the following options in the import dialog:
    - *Use Auto Import*
    - *Create directories for empty content roots automatically*
    - *Use default gradle wrapper (recommended)*
 
![Import project dialog](./docs/img/import_project.png?raw=true "Import Project Dialog")  
![Import options dialog](./docs/img/import_options.png?raw=true "Import Options Dialog")  
![Run configuration dialog](./docs/img/run_configuration.png?raw=true "Run Configuration")

    
## Run Configuration
Add a new run configuration of type *Gradle* with the following settings:
- Set the current project as *Gradle project*
- In *Tasks* enter `runIde`.

This should give you a basic run configuration which will start a new IntelliJ instance with the plugin deployed.