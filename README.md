# Git Flow Integration for Intellij


An intelliJ plugin providing a UI layer for git-flow, which in itself is a collection of Git extensions to provide high-level repository operations for Vincent [Driessen's branching model](http://nvie.com/git-model).

![screenshot](http://opherv.github.io/gitflow4idea/images/gitflow.jpg)

## Getting started

For the best introduction to get started with `git flow`, please read Jeff Kreeftmeijer's blog post:

[http://jeffkreeftmeijer.com/2010/why-arent-you-using-git-flow/](http://jeffkreeftmeijer.com/2010/why-arent-you-using-git-flow/)

Or have a look at this [cheat sheet](http://danielkummer.github.io/git-flow-cheatsheet/) by Daniel Kummer:

## Who and why

This plugin was created by [Opher Vishnia](http://www.opherv.com), after I couldn't find any similar implementation.
I saw this [suggestion page](http://youtrack.jetbrains.com/issue/IDEA-65491) on the JetBrains site has more than 220 likes and 60 comments, and decided to take up the gauntlet :)

Huge shoutout [to Kirill Likhodedov](https://github.com/klikh), who wrote much of the original git4idea plugin, without which this plugin could not exist

## Installation

The plugin is available via the IntelliJ plugin manager. Just search for "Git Flow Integration" to get the latest version!

(The plugin requires that you have gitflow installed. I *highly* recommend using the [AVH edition](https://github.com/petervanderdoes/gitflow), rather than [Vanilla Git Flow](https://github.com/nvie/gitflow) since the original isn't being maintained anymore)

**Mac/Linux users:**

If you're running into issues like getting
`Gitflow is not installed`
or
`git: 'flow' is not a git command. See 'git --help'.`

Please be sure to check out [this thread](https://github.com/OpherV/gitflow4idea/issues/7)


## Caveats

While the plugin is operational and contains all basic functions (init/feature/release/hotfix), it may contains bugs. With your help I'll be able to find and zap them all.

## Helping out

This project is under active development.
If you encounter any bug or an issue, I encourage you to add the them to the [Issues list](https://github.com/OpherV/gitflow4idea/issues) on Github.
Feedback and suggestions are also very welcome.

I have worked hard on this plugin on my spare time. If you feel it is useful to you, please consider buying me a beer. I code better drunk.

[![PayPal][2]][1]

[1]: https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=DH67M9XFKAJHA&lc=IL&item_name=gitflow4idea%20development&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest
[2]: https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif

## License

This plugin is under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
Copyright 2013-2014, Opher Vishnia.


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/OpherV/gitflow4idea/trend.png)](https://bitdeli.com/free "Bitdeli Badge") 
