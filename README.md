# MultiRespawn

Cross-server respawn plugin for Folia 1.21.8

Transfer players to another server (e.g., lobby) when they die.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Folia](https://img.shields.io/badge/Folia-1.21.8-green.svg)](https://papermc.io/software/folia)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://adoptium.net/)

## Features

- **Folia Support** - Fully compatible with Folia's RegionScheduler API
- **Instant Spawn on Death** - Skip respawn screen, /back returns to death location
- **Console Command Execution** - Bypasses cooldowns and restrictions
- **Movement Freeze** - Players frozen during teleportation
- **PvP/PvE Filter** - Option to run only on PvP deaths
- **Permission System** - Bypass and permission controls
- **Configurable Messages** - Enable/disable all messages
- **World Filter** - Enable/disable for specific worlds

## Installation

1. Download the JAR file from [Releases](https://github.com/altugcetin/MultiRespawn/releases)
2. Place the JAR in your `plugins` folder
3. Start the server
4. Edit `plugins/MultiRespawn/config.yml` as needed
5. Reload with `/multirespawn reload`

## Configuration

### config.yml

```yaml
# Plugin Settings
settings:
  enabled: true
  debug: false
  respawn-delay: 60              # Only used if skip-respawn-screen is false
  skip-respawn-screen: true      # Skip respawn screen for instant spawn
  spawn-teleport-duration: 100   # Freeze duration after spawn command

# Respawn Command Settings
respawn:
  command: "spawn"
  run-as-console: true           # Bypasses cooldowns
  console-command: "spawn %player%"

# Message Settings
messages:
  show-death-message: false
  show-actionbar: false
  show-bypass-message: false

# Conditions
conditions:
  only-pvp-deaths: false
  require-permission: false
  permission-node: "multirespawn.use"
  enabled-worlds: []
  disabled-worlds: []

# Movement Freeze Settings
freeze:
  enabled: true
  freeze-rotation: false
```

### messages.yml

All messages are fully customizable in a separate `messages.yml` file.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/multirespawn reload` | Reload configuration | `multirespawn.admin` |
| `/multirespawn info` | Show plugin information | `multirespawn.admin` |
| `/multirespawn help` | Show help menu | `multirespawn.admin` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `multirespawn.admin` | Access to admin commands | OP |
| `multirespawn.bypass` | Bypass cross-server respawn | false |
| `multirespawn.transfer` | Transfer permission (optional) | true |

## Proxy Configuration

### BungeeCord / Waterfall

In `config.yml`:
```yaml
servers:
  survival:
    address: localhost:25566
  lobby:
    address: localhost:25567
```

### Velocity

In `velocity.toml`:
```toml
[servers]
survival = "localhost:25566"
lobby = "localhost:25567"
```

## Requirements

- Folia 1.21.4+ or Paper 1.21+
- Java 21+
- BungeeCord/Velocity proxy system

## Building from Source

```bash
git clone https://github.com/altugcetin/MultiRespawn.git
cd MultiRespawn
./gradlew build
```

The JAR will be in `build/libs/`

## Author

**AstroAlchemist**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.
