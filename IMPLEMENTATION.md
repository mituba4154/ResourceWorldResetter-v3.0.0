# Language System Implementation Summary

## Problem Statement
The original request (in Japanese) asked to:
1. Make all GUI and messages language-configurable
2. Add a lang folder at the same level as config
3. Add en_us.yml and ja_jp.yml files under the lang folder for flexible language support
4. Add a config setting to choose which language to use

## Implementation

### Architecture

```
plugins/ResourceWorldResetter/
├── config.yml                      # Main config with language setting
└── lang/                           # Language files directory
    ├── en_us.yml                  # English translations
    └── ja_jp.yml                  # Japanese translations
```

### Code Structure

#### 1. LanguageManager Class
Located: `src/main/java/com/lozaine/ResourceWorldResetter/lang/LanguageManager.java`

**Features:**
- Loads language files from resources and saves to plugin data folder
- Supports placeholder replacement (e.g., {world}, {minutes})
- Automatic color code translation (& to §)
- Fallback to en_us if specified language not found
- Warning logs for missing translation keys

**Key Methods:**
```java
public String getMessage(String key)
public String getMessage(String key, String... replacements)
public void reload(String language)
```

#### 2. Modified Classes

**ResourceWorldResetter.java:**
- Added `LanguageManager` field
- Initialize language manager in `onEnable()`
- Replaced all hardcoded messages with `lang.getMessage()`
- Language reloads when config reloads

**AdminGUI.java:**
- Added `LanguageManager` field
- All GUI titles now use language keys
- All item names and descriptions use language keys
- Dynamic text (world names, settings) use placeholders

**AdminGUIListener.java:**
- Added `LanguageManager` field
- All player messages use language keys
- Menu item matching uses `ChatColor.stripColor()` on translated text
- Supports both English and Japanese menu navigation

**RwrRegionCommand.java:**
- Added `LanguageManager` field
- All command messages use language keys
- Usage instructions, errors, and success messages all translatable

### Configuration

**config.yml:**
```yaml
# Language setting (available: en_us, ja_jp)
language: "en_us"
```

### Language Files

#### Structure (both files have same keys, different values):

**GUI Sections:**
- `gui.title.*` - Menu titles
- `gui.main.*` - Main menu items
- `gui.world.*` - World selection items
- `gui.reset_type.*` - Reset type items
- `gui.reset_day.*` - Day selection items
- `gui.warning_time.*` - Warning time items
- `gui.restart_time.*` - Restart time items
- `gui.monthly_day.*` - Monthly day items
- `gui.common.*` - Common items (Back button, etc.)

**Message Sections:**
- `message.*` - GUI action messages
- `message.region.*` - Region command messages
- `message.reset.*` - Reset broadcast messages
- `message.region_reset.*` - Region reset messages

#### Example Comparison:

**English (en_us.yml):**
```yaml
gui.title.main: "&3&lResource World Admin"
gui.main.change_world: "Change World"
gui.main.change_world_desc: "Select which world to reset"
message.world_set: "&aResource world set to: {world}"
message.reset.warning: "&e[ResourceWorldResetter] Resource world will reset in {minutes} minutes!"
```

**Japanese (ja_jp.yml):**
```yaml
gui.title.main: "&3&lリソースワールド管理"
gui.main.change_world: "ワールドを変更"
gui.main.change_world_desc: "リセットするワールドを選択"
message.world_set: "&aリソースワールドを設定しました: {world}"
message.reset.warning: "&e[ResourceWorldResetter] リソースワールドは{minutes}分後にリセットされます！"
```

### Placeholder System

The language system supports dynamic content through placeholders:

| Placeholder | Usage | Example |
|-------------|-------|---------|
| `{world}` | World name | "リソースワールド: {world}" → "リソースワールド: resource_world" |
| `{type}` | Reset type | "タイプ: {type}" → "タイプ: daily" |
| `{time}` | Time value | "時刻: {time}:00" → "時刻: 3:00" |
| `{minutes}` | Minutes | "{minutes}分前" → "5分前" |
| `{hour}` | Hour | "{hour}:00" → "15:00" |
| `{day}` | Day | "{day}日" → "15日" |
| `{x}`, `{z}` | Coordinates | "リージョン{x},{z}" → "リージョン0,0" |
| `{seconds}` | Seconds | "{seconds}秒" → "120秒" |
| `{tps_before}`, `{tps_after}` | TPS values | "TPS: {tps_before} → {tps_after}" → "TPS: 20.0 → 19.8" |

### Usage Example

**Code:**
```java
player.sendMessage(lang.getMessage("message.world_set", "{world}", worldName));
```

**With language = "en_us":**
```
[Server] Resource world set to: Resources
```

**With language = "ja_jp":**
```
[Server] リソースワールドを設定しました: Resources
```

### How to Add a New Language

1. Copy `en_us.yml` to a new file (e.g., `fr_fr.yml`)
2. Translate all text values (keep keys unchanged)
3. Save the file in `plugins/ResourceWorldResetter/lang/`
4. Update config.yml: `language: "fr_fr"`
5. Run `/reloadrwr`

### Color Codes

All language files support Minecraft color codes using `&`:

```yaml
message.success: "&aSuccess!"        # Green
message.error: "&cError!"            # Red
message.warning: "&eWarning!"        # Yellow
message.info: "&bInfo"               # Aqua
gui.title.main: "&3&lMain Menu"      # Dark Aqua + Bold
```

### Backwards Compatibility

✅ Default language is en_us (English)
✅ Missing language files fall back to en_us
✅ Missing translation keys show the key name with a warning in logs
✅ Existing configs without language setting default to en_us

## Testing Checklist

All the following should work in both English and Japanese:

- [x] Main GUI menu opens with translated title
- [x] All GUI items show translated names
- [x] GUI descriptions show translated text
- [x] World selection menu shows translated text
- [x] Reset type menu shows translated options
- [x] Day selection menu shows translated days
- [x] Warning time menu shows translated times
- [x] Restart time menu shows translated hours
- [x] Monthly day menu shows translated days
- [x] Player feedback messages are translated
- [x] Command messages are translated
- [x] Broadcast messages are translated
- [x] Reset warnings are translated
- [x] Config reload reloads language
- [x] Invalid language falls back to en_us

## Files Changed Summary

```
Modified:
  src/main/java/com/lozaine/ResourceWorldResetter/ResourceWorldResetter.java
  src/main/java/com/lozaine/ResourceWorldResetter/gui/AdminGUI.java
  src/main/java/com/lozaine/ResourceWorldResetter/gui/AdminGUIListener.java
  src/main/java/com/lozaine/ResourceWorldResetter/commands/RwrRegionCommand.java
  src/main/resources/config.yml

Added:
  src/main/java/com/lozaine/ResourceWorldResetter/lang/LanguageManager.java
  src/main/resources/lang/en_us.yml (146 translation keys)
  src/main/resources/lang/ja_jp.yml (146 translation keys)
  LANGUAGE.md (User documentation)
  IMPLEMENTATION.md (This file)
```

## Benefits

1. **Full Internationalization**: Every text shown to players can be translated
2. **Easy to Extend**: Add new languages by copying and translating one file
3. **Maintainable**: All text in one place per language
4. **Flexible**: Supports placeholders for dynamic content
5. **User-Friendly**: Server owners can customize any message
6. **Professional**: Color codes and formatting supported
7. **Safe**: Fallback system prevents errors if translations missing
