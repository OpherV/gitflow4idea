The plugin requires that you have a maintained fork of gitflow installed. We recommend the modern **[GitFlowNext (by Tower)](https://github.com/git-tower/git-flow)** or the legacy **[AVH edition](https://github.com/petervanderdoes/gitflow)**. This is because the [Vanilla Git Flow](https://github.com/nvie/gitflow) hasn't been maintained in years.

> **Note for Windows Users:** Git for Windows removed the built-in AVH edition starting from version 2.51.1. If you are getting a "command not found" error, you must install a modern alternative like GitFlowNext.

**How to check Git Flow version**

run `git flow version` 

If it says `0.4.1`, then you have the wrong (Vanilla) version. You will have to uninstall it and then install a supported fork. 
If the output contains `AVH` or `next` / `Tower`, you are good to go!

**How to install GitFlowNext (Recommended)**

* **Windows:** You can install it via winget: `winget install GitTower.GitFlowNext` *(Make sure to rename the downloaded executable to `git-flow.exe` and place it in your Git `mingw64\libexec\git-core` directory).*
* **Mac/Linux/Windows:** Refer to the official [GitFlowNext Installation Docs](https://github.com/git-tower/git-flow) for detailed instructions.

**How to uninstall wrong version on OSX**

If installed via `brew` then run `brew uninstall git-flow`                                      

**Mac/Linux users:**

If you're running into issues like getting
`Gitflow is not installed`
or
`git: 'flow' is not a git command. See 'git --help'.`

Please be sure to check out [this thread](https://github.com/OpherV/gitflow4idea/issues/7)