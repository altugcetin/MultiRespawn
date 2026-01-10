# MultiRespawn

Cross-server respawn plugin with `/back` command for Folia/Paper

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Folia](https://img.shields.io/badge/Folia-1.21.8-green.svg)](https://papermc.io/software/folia)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://adoptium.net/)

## Features

- **Folia Support** - RegionScheduler API compatible
- **Cross-Server /back** - Return to death location across servers
- **MySQL + Redis** - Database sync for multi-server setup
- **HuskHomes Integration** - Uses HuskHomes for cross-server teleport
- **Instant Spawn** - Skip respawn screen on death
- **PvP Filter** - Option for PvP-only deaths

## Installation

1. Download JAR from [Releases](https://github.com/altugcetin/MultiRespawn/releases)
2. Place in `plugins` folder
3. Configure MySQL and Redis in `config.yml`
4. Set `server-name` for each server
5. Reload: `/multirespawn reload`

## Configuration

```yaml
settings:
  enabled: true
  server-name: "lobby"  # Different for each server
  skip-respawn-screen: true

respawn:
  run-as-console: true
  console-command: "huskhomes:spawn %player%"

database:
  mysql:
    host: "localhost"
    port: 3306
    database: "multirespawn"
    username: "root"
    password: ""
  redis:
    host: "localhost"
    port: 6379
    password: ""

back-command:
  enabled: true
```

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/back` | Teleport to death location | `multirespawn.back` |
| `/multirespawn reload` | Reload config | `multirespawn.admin` |
| `/multirespawn info` | Plugin info | `multirespawn.admin` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `multirespawn.admin` | Admin commands | OP |
| `multirespawn.bypass` | Bypass respawn | false |
| `multirespawn.back` | Use /back | true |

## Requirements

- Folia 1.21.4+ or Paper 1.21+
- Java 21+
- MySQL 8.0+
- Redis
- HuskHomes (for cross-server teleport)

## Building

```bash
git clone https://github.com/altugcetin/MultiRespawn.git
cd MultiRespawn
./gradlew shadowJar
```

JAR: `build/libs/MultiRespawn-x.x.x.jar`

## Author

**AstroAlchemist**

## License

MIT License - see [LICENSE](LICENSE)
