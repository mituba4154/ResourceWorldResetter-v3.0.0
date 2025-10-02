# 🌐 Language System - ResourceWorldResetter v3.0.0

## Quick Start

### Switch to Japanese
```yaml
# plugins/ResourceWorldResetter/config.yml
language: "ja_jp"
```
Then run: `/reloadrwr`

### Switch to English
```yaml
# plugins/ResourceWorldResetter/config.yml
language: "en_us"
```
Then run: `/reloadrwr`

---

## What's New

### ✨ Complete Multilingual Support

Every text that players see can now be translated:
- ✅ All GUI menu titles
- ✅ All GUI button labels  
- ✅ All GUI descriptions
- ✅ All chat messages
- ✅ All command responses
- ✅ All broadcast messages

### 🗂️ File Structure

```
plugins/ResourceWorldResetter/
├── config.yml                 # Contains: language: "en_us"
└── lang/
    ├── en_us.yml             # English translations (default)
    └── ja_jp.yml             # Japanese translations
```

### 📝 Language Files

Both files contain **105+ translation keys** organized by category:

**GUI Elements:**
- `gui.title.*` - Menu titles
- `gui.main.*` - Main menu items
- `gui.reset_type.*` - Reset type options
- `gui.warning_time.*` - Warning time options
- And more...

**Messages:**
- `message.*` - Player feedback
- `message.reset.*` - Reset broadcasts
- `message.region.*` - Region commands

**Example (en_us.yml):**
```yaml
gui.title.main: "&3&lResource World Admin"
message.world_set: "&aResource world set to: {world}"
message.reset.warning: "&e[ResourceWorldResetter] Resource world will reset in {minutes} minutes!"
```

**Example (ja_jp.yml):**
```yaml
gui.title.main: "&3&lリソースワールド管理"
message.world_set: "&aリソースワールドを設定しました: {world}"
message.reset.warning: "&e[ResourceWorldResetter] リソースワールドは{minutes}分後にリセットされます！"
```

---

## Comparison

### English GUI
```
┌────────────────────────────┐
│  Resource World Admin      │
├────────────────────────────┤
│  Current Settings          │
│  World: Resources          │
│  Reset Type: Daily         │
│  Restart Time: 3:00        │
│  Warning Time: 5 minutes   │
├────────────────────────────┤
│  [Change World]            │
│  [Reset Type]              │
│  [Restart Time]            │
│  [Warning Time]            │
│  [Force Reset]             │
└────────────────────────────┘
```

### Japanese GUI
```
┌────────────────────────────┐
│  リソースワールド管理       │
├────────────────────────────┤
│  現在の設定                │
│  ワールド: Resources       │
│  リセットタイプ: Daily     │
│  リセット時刻: 3:00        │
│  警告時間: 5分前           │
├────────────────────────────┤
│  [ワールドを変更]          │
│  [リセットタイプ]          │
│  [リセット時刻]            │
│  [警告時間]                │
│  [強制リセット]            │
└────────────────────────────┘
```

---

## Adding New Languages

### Step 1: Copy Template
```bash
cp plugins/ResourceWorldResetter/lang/en_us.yml plugins/ResourceWorldResetter/lang/fr_fr.yml
```

### Step 2: Translate
Edit `fr_fr.yml` and translate all values:
```yaml
gui.title.main: "&3&lAdmin Monde Ressource"
gui.main.change_world: "Changer le Monde"
message.world_set: "&aMonde ressource défini sur: {world}"
```

### Step 3: Activate
Edit `config.yml`:
```yaml
language: "fr_fr"
```

### Step 4: Reload
```
/reloadrwr
```

Done! Your server now uses French.

---

## Features

### 🎯 Smart Placeholders
Messages support dynamic content:
```yaml
# In language file:
message.reset.warning: "World resets in {minutes} minutes!"

# In game:
"World resets in 5 minutes!"
```

**Available Placeholders:**
- `{world}` - World name
- `{type}` - Reset type (daily/weekly/monthly)
- `{time}` - Time value
- `{minutes}` - Minutes
- `{hour}` - Hour
- `{day}` - Day
- `{x}`, `{z}` - Coordinates
- `{seconds}` - Seconds
- `{tps_before}`, `{tps_after}` - TPS values

### 🎨 Color Codes
Use `&` for colors in language files:
```yaml
message.success: "&aSuccess!"        # Green
message.error: "&cError!"            # Red  
message.warning: "&eWarning!"        # Yellow
gui.title.main: "&3&lMain Menu"      # Dark Aqua + Bold
```

### 🔄 Hot Reload
No server restart needed:
```
/reloadrwr
```
Changes apply immediately!

### 🛡️ Fallback System
- Invalid language? Falls back to `en_us`
- Missing translation? Shows key with warning
- Never crashes!

---

## Documentation

📚 **Full Documentation Available:**

1. **LANGUAGE.md** - User guide for server owners
2. **IMPLEMENTATION.md** - Technical documentation for developers  
3. **USAGE_EXAMPLES.md** - 10 practical examples
4. **VERIFICATION.md** - Complete test report

---

## Support

### English Language
Default language with complete translations for all features.

### Japanese Language (日本語)
完全な日本語翻訳が含まれています。すべてのGUI要素、メッセージ、コマンドが日本語で表示されます。

### Custom Languages
Easy to add! Just copy and translate one file.

---

## Credits

**Implementation:** Complete language system with 105+ translation keys
**Languages:** English (en_us), Japanese (ja_jp)
**Status:** Production ready ✅

---

## Quick Reference

| Action | Command/Config |
|--------|---------------|
| Open GUI | `/rwrgui` |
| Reload config & language | `/reloadrwr` |
| Switch to Japanese | Set `language: "ja_jp"` in config.yml |
| Switch to English | Set `language: "en_us"` in config.yml |
| Add custom language | Copy en_us.yml, translate, update config |

---

## Examples

### Example 1: Region Command (English)
```
> /rwrregion add 0 0
✓ Added region 0,0 to reset list.
```

### Example 1: Region Command (Japanese)
```
> /rwrregion add 0 0
✓ リージョン0,0をリセットリストに追加しました。
```

### Example 2: Reset Broadcast (English)
```
[ResourceWorldResetter] Resource world will reset in 5 minutes!
[ResourceWorldResetter] Resource world reset completed in 30 seconds (TPS: 20.0 → 19.8).
```

### Example 2: Reset Broadcast (Japanese)
```
[ResourceWorldResetter] リソースワールドは5分後にリセットされます！
[ResourceWorldResetter] リソースワールドのリセットが30秒で完了しました（TPS: 20.0 → 19.8）。
```

---

## Need Help?

1. Check LANGUAGE.md for detailed user guide
2. Check USAGE_EXAMPLES.md for 10 practical examples
3. Check IMPLEMENTATION.md for technical details

---

**Version:** 3.0.0+language-system  
**Status:** ✅ Complete and Production Ready  
**Languages:** 2 (English, Japanese)  
**Translation Keys:** 105+ per language
