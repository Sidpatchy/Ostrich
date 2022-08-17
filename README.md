# Ostrich
A Minecraft plugin to allow server administrators to place sensible limits on players' ability to fly.

# Why does this exist?
This plugin is being created due to an issue I've faced on my Minecraft servers when allowing players to use /fly.
The issue we faced was partially helped using plugins like AntiWorldFly (which I now maintain, more on that later) to
prevent flight in specific worlds. But this solution didn't allow for the fine-grained control that we desired.

The biggest issue we faced is that being able to fly at any point in time makes the game too trivial for a PvE server.
We want to ensure that players can fly when it has a utility that couldn't otherwise be served by something like an 
elytra.

### Why develop this *and* AntiWorldFly?
Turning AntiWorldFly into something like this was not practical. I tried but quickly came to the conclusion that in 
order to expand features without subtracting from ease-of-use a full rewrite would be required. 

The architecture of AntiWorldFly is incompatible with my vision for an ideal flight management plugin. Control using a 
config file and some permissions is SIMPLE and makes the user experience great for server admins who just want to have a 
plugin that is easy to set up and use. Whereas this plugin focuses on having *much more* customization. The outcome of 
more customization is more complex configuration. A different approach was needed.

My biggest issue with further expanding on AntiWorldFly is breaking simplicity. I think AWF is great as is, it doesn't 
need all of these additional features. The addition of the WorldGuard integration in AWF lead to duplicated features 
which lead to less intuitive configuration. Furthermore, intertwining these duplicate features is not practical and even 
if pulled off successfully, it would likely be rather confusing for users.

Ostrich aims to fix these issues by starting from the ground up.