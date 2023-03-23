# Ostrich
The next generation of flight-management plugins designed to allow server administrators to place sensible limits on players' ability to fly.

## Features
* Flight and Elytra blocking.
* WorldGuard integration
  * Disable flight on a per-world or per-region basis.
    * Flight/Elytra blocking is customized using region flags and permissions, allowing admins to have fine-grained control over where players fly.
* GriefPrevention integration
  * Allow players to fly when inside claims they are members of.
  * Allow players to fly when inside admin claims.

## What Ostrich isn't
* Ostrich is not an anti-cheat. It does not attempt to detect nor stop fly "hacks".
  * Ostrich shouldn't interfere with vanilla flight detection nor anti-cheat plugins. If you run into this, please open an issue.

## Planned Features
* Daily flight limits.

# Why does this exist?
This plugin is being created due to an issue I've faced on my Minecraft servers when allowing players to use /fly. The issue we faced was partially helped by using plugins like AntiWorldFly (which I now maintain, more on that later) to prevent flight in specific worlds. But this solution didn't allow for the fine-grained control that we desired.

The biggest issue we faced is that being able to fly at any point in time makes the game too trivial for a PvE server. We want to ensure that players can fly when it has a utility that couldn't otherwise be served by something like an elytra.

### Why develop this *and* AntiWorldFly?
Turning AntiWorldFly into something like this was not practical. I tried but quickly came to the conclusion that in order to expand features without subtracting from ease-of-use a full rewrite would be required.

The architecture of AntiWorldFly is incompatible with my vision for an ideal flight management plugin. Control using a config file and some permissions is SIMPLE and makes the user experience great for server admins who just want to have a plugin that is easy to set up and use. Whereas this plugin focuses on having *much more* customization. The outcome of more customization is more complex configuration. A different approach was needed.

My biggest issue with further expanding on AntiWorldFly is breaking simplicity. I think AWF is great as is, it doesn't need all of these additional features. The addition of the WorldGuard integration in AWF lead to duplicated features which lead to less intuitive configuration. Furthermore, intertwining these duplicate features is not practical and even if pulled off successfully, it would likely be rather confusing for users.

Ostrich aims to fix these issues by taking a different approach from the ground up.