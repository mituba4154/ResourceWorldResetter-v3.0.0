# Usage Examples

This document shows practical examples of how the language system works.

## Example 1: Switching Languages

### English Configuration
```yaml
# config.yml
language: "en_us"
```

**Result:**
- GUI Title: "Resource World Admin"
- Change World button: "Change World"
- Message when changing world: "Resource world set to: Resources"
- Reset warning: "[ResourceWorldResetter] Resource world will reset in 5 minutes!"

### Japanese Configuration
```yaml
# config.yml
language: "ja_jp"
```

**Result:**
- GUI Title: "リソースワールド管理"
- Change World button: "ワールドを変更"
- Message when changing world: "リソースワールドを設定しました: Resources"
- Reset warning: "[ResourceWorldResetter] リソースワールドは5分後にリセットされます！"

## Example 2: GUI Menus

### Main Menu (English)
```
┌─────────────────────────────────────────┐
│        Resource World Admin             │
├─────────────────────────────────────────┤
│                                         │
│           Current Settings              │
│      World: Resources                   │
│      Reset Type: Daily                  │
│      Restart Time: 3:00                 │
│      Warning Time: 5 minutes            │
│                                         │
├─────────────────────────────────────────┤
│  [Grass]        [Clock]      [Flower]   │
│  Change World   Reset Type   Restart    │
│                              Time       │
│                                         │
│  [Bell]         [Map]        [TNT]      │
│  Warning Time   Region Mode  Force      │
│                              Reset      │
└─────────────────────────────────────────┘
```

### Main Menu (Japanese)
```
┌─────────────────────────────────────────┐
│        リソースワールド管理              │
├─────────────────────────────────────────┤
│                                         │
│           現在の設定                    │
│      ワールド: Resources                │
│      リセットタイプ: Daily              │
│      リセット時刻: 3:00                 │
│      警告時間: 5分前                    │
│                                         │
├─────────────────────────────────────────┤
│  [草]           [時計]       [花]       │
│  ワールドを変更  リセット    リセット    │
│                 タイプ       時刻        │
│                                         │
│  [ベル]         [地図]       [TNT]      │
│  警告時間       リージョン   強制        │
│                 モード       リセット    │
└─────────────────────────────────────────┘
```

## Example 3: Command Messages

### Region Commands (English)
```
> /rwrregion enable
[✓] Region mode enabled.

> /rwrregion add 0 0
[✓] Added region 0,0 to reset list.

> /rwrregion list
[i] Regions: 0,0, 1,0, 0,1

> /rwrregion addhere
[✓] Added current region 2,3.
```

### Region Commands (Japanese)
```
> /rwrregion enable
[✓] リージョンモードを有効にしました。

> /rwrregion add 0 0
[✓] リージョン0,0をリセットリストに追加しました。

> /rwrregion list
[i] リージョン: 0,0, 1,0, 0,1

> /rwrregion addhere
[✓] 現在のリージョン2,3を追加しました。
```

## Example 4: Broadcast Messages

### Reset Sequence (English)
```
[Time: 03:55]
[ResourceWorldResetter] Resource world will reset in 5 minutes!

[Time: 04:00]
[ResourceWorldResetter] Resource world reset in progress. Players in that world will be teleported to safety.
[You have been teleported to safety - the resource world is being reset.]

[30 seconds later]
[ResourceWorldResetter] Resource world reset completed in 30 seconds (TPS: 20.0 → 19.8).
[ResourceWorldResetter] The resource world has been reset and is ready to use!
```

### Reset Sequence (Japanese)
```
[Time: 03:55]
[ResourceWorldResetter] リソースワールドは5分後にリセットされます！

[Time: 04:00]
[ResourceWorldResetter] リソースワールドのリセットを実行中です。ワールド内のプレイヤーは安全な場所にテレポートされます。
[リソースワールドがリセットされるため、安全な場所にテレポートされました。]

[30秒後]
[ResourceWorldResetter] リソースワールドのリセットが30秒で完了しました（TPS: 20.0 → 19.8）。
[ResourceWorldResetter] リソースワールドがリセットされ、使用可能になりました！
```

## Example 5: Day Selection Menu

### Weekly Reset Days (English)
```
┌─────────────────────────────────┐
│      Select Reset Day           │
├─────────────────────────────────┤
│  Monday    Tuesday   Wednesday  │
│  Thursday  Friday    Saturday   │
│  Sunday                         │
│                                 │
│                      [Back]     │
└─────────────────────────────────┘
```

### Weekly Reset Days (Japanese)
```
┌─────────────────────────────────┐
│      リセット日を選択            │
├─────────────────────────────────┤
│  月曜日    火曜日    水曜日      │
│  木曜日    金曜日    土曜日      │
│  日曜日                         │
│                                 │
│                      [戻る]     │
└─────────────────────────────────┘
```

## Example 6: Custom Language

### Creating German Translation
1. Copy `en_us.yml` to `de_de.yml`
2. Translate entries:

```yaml
# de_de.yml
gui.title.main: "&3&lRessourcenwelt Admin"
gui.main.change_world: "Welt ändern"
gui.main.change_world_desc: "Wähle die zu zurücksetzende Welt"
message.world_set: "&aRessourcenwelt gesetzt auf: {world}"
message.reset.warning: "&e[ResourceWorldResetter] Ressourcenwelt wird in {minutes} Minuten zurückgesetzt!"
```

3. Update config:
```yaml
language: "de_de"
```

4. Run `/reloadrwr`

**Result:**
```
> /rwrgui
[Opens GUI with title "Ressourcenwelt Admin"]
[Buttons show "Welt ändern", "Reset-Typ", etc.]
```

## Example 7: Color Customization

You can customize colors in any language file:

```yaml
# Bright green success messages
message.world_set: "&a&lResource world set to: {world}"

# Bold red errors
message.no_permission: "&c&lYou do not have permission!"

# Gradient-like effect with multiple colors
gui.title.main: "&b&lResource &3&lWorld &9&lAdmin"

# Yellow with underline
message.reset.warning: "&e&n[ResourceWorldResetter] World resets in {minutes} minutes!"
```

## Example 8: Error Handling

### Missing Language File
```yaml
# config.yml
language: "fr_fr"  # File doesn't exist
```

**Console Output:**
```
[WARN] Language file fr_fr.yml not found! Falling back to en_us.yml
[INFO] Loaded language: en_us
```

### Missing Translation Key
If a key is missing from language file:

**Console Output:**
```
[WARN] Missing translation key: message.new_feature
```

**In-Game:**
```
message.new_feature
```

## Example 9: Placeholder Usage

### Language File
```yaml
message.reset_scheduled: "&aReset scheduled for {type} at {time}:00 on {day}"
```

### In Code
```java
lang.getMessage("message.reset_scheduled", 
    "{type}", "weekly",
    "{time}", "3",
    "{day}", "Monday")
```

**English Result:**
```
Reset scheduled for weekly at 3:00 on Monday
```

**Japanese Version:**
```yaml
message.reset_scheduled: "&a{type}リセットは{day}の{time}:00に予定されています"
```

**Japanese Result:**
```
weeklyリセットはMondayの3:00に予定されています
```

## Example 10: Multi-Language Server Setup

For servers with international players, you can:

1. Keep default language for most players
2. Create custom commands/signs with different languages
3. Use multiple language files for documentation

**Example Setup:**
```yaml
# config.yml - Default for most players
language: "en_us"
```

**Create Information Signs:**
```
EN: Resource world resets daily at 3 AM
JP: リソースワールドは毎日午前3時にリセットされます
DE: Ressourcenwelt wird täglich um 3 Uhr zurückgesetzt
```
