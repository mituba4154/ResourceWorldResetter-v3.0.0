# Language System Documentation

## Overview
The ResourceWorldResetter plugin now supports multiple languages. All GUI menus and messages can be customized through language files.

## Configuration

### Setting the Language
Edit `config.yml` and change the `language` setting:

```yaml
# Language setting (available: en_us, ja_jp)
language: "en_us"
```

Available languages:
- `en_us` - English (United States)
- `ja_jp` - Japanese (Japan)

## Language Files

Language files are located in the `plugins/ResourceWorldResetter/lang/` folder:
- `en_us.yml` - English translations
- `ja_jp.yml` - Japanese translations

### File Structure

Each language file contains keys organized by category:

#### GUI Titles
- `gui.title.main` - Main menu title
- `gui.title.world_selection` - World selection menu title
- `gui.title.reset_type` - Reset type menu title
- etc.

#### GUI Items
- `gui.main.*` - Main menu items
- `gui.reset_type.*` - Reset type menu items
- `gui.warning_time.*` - Warning time menu items
- etc.

#### Messages
- `message.world_set` - Message when world is changed
- `message.reset.warning` - Reset warning broadcast
- `message.region.*` - Region command messages
- etc.

### Color Codes
Use the `&` symbol for color codes in language files:
- `&a` - Green
- `&c` - Red
- `&e` - Yellow
- `&b` - Aqua
- `&6` - Gold
- `&3` - Dark Aqua
- `&l` - Bold
- etc.

### Placeholders
Some messages support placeholders that are replaced with dynamic values:
- `{world}` - World name
- `{type}` - Reset type
- `{time}` - Time value
- `{minutes}` - Minutes value
- `{hour}` - Hour value
- `{day}` - Day value
- `{x}`, `{z}` - Coordinates
- `{seconds}` - Seconds value
- `{tps_before}`, `{tps_after}` - TPS values

Example:
```yaml
message.world_set: "&aリソースワールドを設定しました: {world}"
```

## Creating Custom Languages

1. Copy an existing language file (e.g., `en_us.yml`)
2. Rename it to your language code (e.g., `fr_fr.yml` for French)
3. Translate all the messages
4. Update `config.yml` to use your new language:
   ```yaml
   language: "fr_fr"
   ```
5. Reload the plugin with `/reloadrwr`

## Reloading Languages

After editing language files, reload the configuration:
```
/reloadrwr
```

This will reload both the config and the language files.

## Examples

### English (en_us.yml)
```yaml
gui.title.main: "&3&lResource World Admin"
message.world_set: "&aResource world set to: {world}"
message.reset.warning: "&e[ResourceWorldResetter] Resource world will reset in {minutes} minutes!"
```

### Japanese (ja_jp.yml)
```yaml
gui.title.main: "&3&lリソースワールド管理"
message.world_set: "&aリソースワールドを設定しました: {world}"
message.reset.warning: "&e[ResourceWorldResetter] リソースワールドは{minutes}分後にリセットされます！"
```

## Troubleshooting

### Language file not found
If a language file is not found, the plugin will fall back to `en_us.yml`.

### Missing translation keys
If a translation key is missing, the key itself will be displayed instead of the translated text. Check the server logs for warnings about missing keys.

### Unicode characters not displaying
Make sure your language files are saved with UTF-8 encoding to support special characters (Japanese, Chinese, etc.).
