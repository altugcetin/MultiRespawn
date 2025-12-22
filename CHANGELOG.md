# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-12-22

### Added
- Initial release
- Cross-server respawn on player death
- Folia 1.21.8 compatibility with RegionScheduler API
- BungeeCord/Velocity support for server transfers
- Modern transfer packet support (1.20.5+)
- World-specific target server configuration
- PvP-only death detection
- Permission-based access control
- Configurable transfer delay with action bar countdown
- Separate `messages.yml` for full message customization
- Turkish language support by default
- Admin commands: `/foliarespawn reload`, `/foliarespawn info`, `/foliarespawn help`
- Bypass permission for players who don't want cross-server respawn

### Configuration
- `config.yml` - Main plugin settings
- `messages.yml` - All plugin messages (fully customizable)

### Permissions
- `foliarespawn.admin` - Access to admin commands
- `foliarespawn.bypass` - Skip cross-server respawn
- `foliarespawn.transfer` - Required permission for transfer (optional)
