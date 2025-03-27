# ResourceWorldResetter v3.0.0

<p align="center">
  <img src="https://files.catbox.moe/xhfveh.png" alt="project-image">
</p>

## Overview

ResourceWorldResetter is a Minecraft plugin designed to automate the resetting of a resource world at scheduled intervals. It seamlessly integrates with **Multiverse-Core** to manage world regeneration without requiring server restarts. The plugin provides a GUI-based configuration system for easy management and supports multiple reset schedules (daily, weekly, and monthly).

## Features

- Fully automated resource world resets
- Supports **daily, weekly, and monthly** reset schedules
- GUI-based configuration for ease of use
- **Multiverse-Core integration** for world management
- Safe player teleportation before resets
- Reset warnings with configurable countdown timers
- **bStats metrics** for tracking usage statistics
- Supports Minecraft **1.21+**

## Dependencies
- [Multiverse-Core](https://www.spigotmc.org/resources/multiverse-core.390/) **4.3.1**


## Installation

1. **Download** the latest version from the [GitHub Releases](https://github.com/Lozaine/ResourceWorldResetter-v2.3.0/releases).
2. Place the `ResourceWorldResetter.jar` into your `plugins/` folder.
3. Ensure **Multiverse-Core** is installed and running.
4. Restart your server.
5. Modify `config.yml` as needed and reload the plugin with `/reloadrwr`.

## Configuration (`config.yml`)

```yaml
worldName: "Resources"  # Name of the resource world
resetWarningTime: 5  # Minutes before reset to send warnings
restartTime: 3  # Time of day (24-hour format) for resets
resetType: "daily"  # Options: daily, weekly, monthly
resetDay: 1  # Day of reset (1-31 for monthly, 1-7 for weekly)
```

## Commands & Permissions

| Command       | Description                       | Permission                    |
| ------------- | --------------------------------- | ----------------------------- |
| `/rwrgui`     | Open the Resource World Reset GUI | `resourceworldresetter.admin` |
| `/reloadrwr`  | Reload plugin configuration       | `resourceworldresetter.admin` |
| `/resetworld` | Manually reset the resource world | `resourceworldresetter.admin` |

## GUI Breakdown

<p align="center">
  <img src="https://files.catbox.moe/ltat9g.png" alt="project-image">
</p>

Admins can configure the plugin via the **in-game GUI** by running `/rwrgui`. The GUI consists of the following sections:

### **1. World Selection**
- **Displays a list of worlds** detected by Multiverse-Core.
- Click on a world to set it as the resource world to reset.

### **2. Reset Schedule Settings**
- **Set Reset Interval**: Choose between **daily, weekly, or monthly** resets.
- **Set Reset Day**: If `weekly` is selected, choose a day (Monday-Sunday).
  - If `monthly`, choose a specific date (1-31).

### **3. Reset Timing Options**
- **Set Reset Time**: Configure the exact hour (24-hour format) when the reset occurs.
- **Set Warning Time**: Choose how many minutes before reset a warning should be sent.

### **4. Manual Reset**
- **Force Reset Now**: Instantly resets the selected resource world.

### **5. Reload Plugin**
- **Reload Configurations**: Applies changes from `config.yml` without restarting the server.

## How It Works

1. The plugin reads the configured **reset schedule**.
2. **Scheduled task system** checks if a reset is due.
3. Players receive a warning (`resetWarningTime` minutes before reset).
4. **All players** are teleported out of the resource world.
5. The world is **unloaded and deleted asynchronously**.
6. The world is **recreated** using Multiverse-Core settings.
7. A **broadcast message** confirms the successful reset.

## Differences from [ResourcesWorldResetter](https://github.com/Lozaine/ResourcesWorldResetter)

- Improved **performance optimization**
- Enhanced **GUI** for world selection and scheduling
- More **efficient chunk unloading and player teleportation**
- **Better compatibility** with newer Minecraft versions
- Supports **weekly and monthly resets**, unlike the previous version
- Added **bStats integration** for tracking usage statistics

## Support & Contribution
- **Issues & Bugs**: Report them on [GitHub Issues](https://github.com/Lozaine/ResourceWorldResetter-v2.3.0/issues) or join our Discord server [The LozDev Mines](https://discord.gg/Y3UuG7xu9x)
- **Feature Requests**: Submit ideas for improvements!
- **Contributions**: Pull requests are welcome.

## License
This project is licensed under the [MIT License](LICENSE).

