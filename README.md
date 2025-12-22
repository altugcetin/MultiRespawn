# MultiRespawn

Cross-server respawn plugin for Folia 1.21.8

Transfer players to another server (e.g., lobby) when they die.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Folia](https://img.shields.io/badge/Folia-1.21.8-green.svg)](https://papermc.io/software/folia)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://adoptium.net/)

## Features

- **Folia Support** - Fully compatible with Folia's RegionScheduler API
- **BungeeCord/Velocity Support** - Works with both proxy systems
- **Modern Transfer (1.20.5+)** - New transfer packet support
- **World-based Configuration** - Different target servers for different worlds
- **PvP/PvE Filter** - Option to transfer only on PvP deaths
- **Permission System** - Bypass and transfer permissions
- **Action Bar Countdown** - Visual countdown before transfer
- **Fully Configurable** - All messages and settings are customizable

## Installation

1. Download the JAR file from [Releases](https://github.com/altugcetin/MultiRespawn/releases)
2. Place the JAR in your `plugins` folder
3. Start the server
4. Edit `plugins/MultiRespawn/config.yml` as needed
5. Reload with `/multirespawn reload`

## Configuration

### config.yml

```yaml
# Target server settings
transfer:
  target-server: "lobby"  # Server to transfer to on death
  use-modern-transfer: false  # 1.20.5+ modern transfer

# World-specific settings
worlds:
  default: "lobby"
  overrides:
    world: "lobby"
    world_nether: "lobby"
    world_the_end: "lobby"

# Conditions
conditions:
  only-pvp-deaths: false      # Only transfer on PvP deaths
  require-permission: false   # Permission requirement
  enabled-worlds: []          # Enabled worlds (empty = all)
  disabled-worlds: []         # Disabled worlds
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
