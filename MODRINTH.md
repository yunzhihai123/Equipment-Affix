# Equipment Affix

## Modrinth Summary

```text
Adds 17 Diablo-style equipment affixes to weapons, tools and armor, with crits, rarity-based loot rolls, an upgrade/breakthrough table, a reforge table, and optional JEI info. Vanilla enchanting stays untouched.
```

---

# Description (English)

Equipment Affix is a **NeoForge 1.20.1** mod that adds a separate affix layer to weapons, tools and armor. It is made for players who want more equipment build variety and more interesting loot without replacing vanilla enchanting. Enchantments continue to work normally; affixes are an additional, independent system.

## Why install it

Vanilla equipment mostly scales through material tier and enchantments. Equipment Affix adds another progression path: find or craft gear, roll affixes, upgrade them, break through level caps, and reforge bad rolls into a different combination. The goal is to make equipment drops worth inspecting and to give players a reason to keep using gear they already like.

## What it adds

- **17 affixes** covering melee weapons, ranged weapons, tools and armor.
- **A custom critical-hit system** with crit chance and crit damage affixes.
- **Rarity-based loot rolls**: generated equipment can receive 3 affixes (common/uncommon) or 4 affixes (rare/epic), with different tier ranges.
- **Crafted gear** receives 3 tier-1 affixes automatically.
- **Affix Upgrade Table** for initializing gear, leveling it from 0 to 20, and breaking through level caps.
- **Affix Reforge Table** for rerolling affix types and tier distribution while keeping the item's level, rarity, affix count and total tier power.
- **One-key upgrade** mode that repeatedly performs normal upgrades up to, but never through, the next breakthrough.
- **Optional JEI integration** with clear cost and material information.
- **Vanilla-styled blocks, GUI and pixel-art textures**; no mixins.

The affix pool includes attack damage, crit chance, crit damage, attack speed, attack reach, knockback, projectile damage and speed, mining speed, XP gain, luck, max health, armor, armor toughness, knockback resistance, movement speed and damage reduction.

## Progression

- Equipment starts at affix level **0**; the maximum is **20**.
- A normal upgrade costs **1 amethyst shard** plus XP.
- The XP cost follows `ceil(6 × 1.14^level)` levels: 7 at level 0, 73 at level 19.
- Breakthroughs happen at **3→4, 7→8, 11→12, 15→16 and 19→20**.
- Breakthrough materials, in order: **8 amethyst shards, 1 diamond, 1 netherite scrap, 1 echo shard, 1 nether star**.
- Each breakthrough adds a level and upgrades one random affix by one tier (maximum tier 5), rerolling that affix's value.
- Un-affixed gear can be initialized for **3 XP levels + 1 amethyst shard**, producing 3 tier-1 affixes.
- **Reforging** costs **12 XP levels + 1 echo shard** and preserves level, rarity, affix count and total tier sum.

## Loot and crafting

- Naturally generated equipment can receive affixes through a global loot modifier.
- Loot rarity weights are common 55, uncommon 28, rare 13 and epic 4; loot luck shifts the result toward rarer gear.
- The Upgrade Table is crafted from **8 amethyst shards around a smithing table**.
- The Reforge Table is crafted from **4 diamonds + 4 echo shards around a smithing table**.

## Compatibility

Affixable items are resolved first through item tags, including vanilla sword, axe, bow, crossbow, trident, pickaxe, shovel, hoe, shears and armor tags. A class-based fallback covers common modded tool, weapon and armor base classes. This means many modded equipment items work without extra configuration, but compatibility is not universal: an unusual item may need to be added to the matching tag by a data pack. Items can also be excluded through the server config blacklist.

Affixes are stored in the item's NBT, so they survive normal item transfers and upgrades. The mod works in single-player and on dedicated servers; for multiplayer, install it on both the client and the server.

## JEI and configuration

JEI is optional. With JEI 15.x installed, the mod provides item info pages for the upgrade table, reforge table, every catalyst, and the affix pool/value ranges of affixable items.

The server config includes:

- `lootChance` — chance for generated equipment to receive affixes (default 0.45)
- `autoDetectCrafted` — automatically affix player-crafted gear (default true)
- `affixCreativeItems` — affix affixable items in creative inventory (default true)
- `blacklist` — item ids that should never receive affixes

## Requirements

- Minecraft **1.20.1**
- NeoForge **47.1.106+**
- Java **17**
- JEI **15.x** (optional)

## Notes

- This mod is designed as an **additional layer**, not a replacement for enchanting.
- Balance is intentionally closer to a lightweight ARPG affix system than to a full loot overhaul.
- The mod does not use mixins.
- Version **1.0.0**, created by **yunzhihai**. Licensed under MIT.

---

# 描述（中文）

Equipment Affix 是一个 **NeoForge 1.20.1** 模组，为武器、工具和盔甲增加了一套独立的“词条”系统。它面向希望获得更多装备构筑变化、让掉落更有检查价值的玩家，同时**不会替换或禁用原版附魔**。附魔照常生效，词条是额外的一层独立系统。

## 为什么安装

原版装备的成长主要来自材质等级和附魔。Equipment Affix 增加了另一条成长路线：寻找或合成装备、随机获得词条、强化词条、突破等级上限，再通过重赋把不理想的词条组合换掉。目标是让装备掉落值得多看两眼，也让玩家有理由继续使用自己喜欢的装备。

## 主要内容

- **17 种词条**，覆盖近战武器、远程武器、工具和盔甲。
- **独立的暴击系统**，包含暴击率和暴击伤害词条。
- **按稀有度生成战利品词条**：普通/罕见装备 3 条词条，稀有/史诗装备 4 条词条，不同稀有度的层级范围不同。
- **玩家合成的装备**自动获得 3 条一级词条。
- **词条强化台**：用于初始化装备、从 0 级强化到 20 级，并进行突破。
- **词条重赋台**：重新分配词条类型和层级分布，同时保留装备等级、稀有度、词条数量和总层级。
- **一键强化**：连续执行普通强化，直到下一次突破前停止，不会自动执行突破。
- **可选 JEI 支持**：清晰展示强化、突破、重赋的费用和材料。
- **原版风格**的方块、GUI 和像素贴图；不使用 Mixin。

词条池包括攻击力、暴击率、暴击伤害、攻击速度、攻击距离、击退距离、弹射物伤害与速度、挖掘速度、经验加成、幸运、生命上限、护甲值、护甲韧性、击退抗性、移动速度和伤害减免。

## 成长流程

- 装备初始词条等级为 **0**，最高 **20**。
- 普通强化每级消耗 **1 个紫水晶碎片**和经验。
- 经验消耗为 `ceil(6 × 1.14^等级)`：0 级需要 7 级经验，19 级需要 73 级经验。
- 突破点位于 **3→4、7→8、11→12、15→16、19→20**。
- 突破材料依次为：**8 个紫水晶碎片、1 个钻石、1 个下界合金碎片、1 个回响碎片、1 个下界之星**。
- 每次突破提升 1 级，并随机将一条词条提升 1 层（最高 5 层），同时重掷该词条的数值。
- 没有词条的装备可以在强化台初始化：**3 级经验 + 1 个紫水晶碎片**，获得 3 条一级词条。
- **重赋**消耗 **12 级经验 + 1 个回响碎片**，保留等级、稀有度、词条数量和总层级。

## 掉落与合成

- 自然生成的装备可以通过全局战利品修改器获得词条。
- 战利品稀有度权重为普通 55、罕见 28、稀有 13、史诗 4；击杀者的幸运值会把结果推向更高稀有度。
- 强化台配方：**8 个紫水晶碎片围绕 1 个锻造台**。
- 重赋台配方：**4 个钻石 + 4 个回响碎片围绕 1 个锻造台**。

## 兼容性

词条适配首先通过物品标签判断，包括原版的剑、斧、弓、弩、三叉戟、镐、锹、锄、剪刀和盔甲标签。对于常见的模组工具、武器和盔甲基类，还有基于类的后备判断。因此大部分模组装备无需额外配置即可使用，但兼容性并非绝对：特别特殊的物品可能需要通过数据包加入对应标签。也可以在服务器配置中用黑名单排除物品。

词条保存在物品 NBT 中，会随正常的物品转移和强化保留。模组支持单人和专用服务器；多人游戏时，客户端和服务端都需要安装。

## JEI 与配置

JEI 是可选依赖。安装 JEI 15.x 后，模组会为强化台、重赋台、每种材料，以及可附词条装备的词条池和 T1–T5 数值范围提供信息页。

服务器配置包括：

- `lootChance` — 生成装备获得词条的概率（默认 0.45）
- `autoDetectCrafted` — 自动为玩家合成的装备添加词条（默认 true）
- `affixCreativeItems` — 自动为创造模式物品栏中的可附词条装备添加词条（默认 true）
- `blacklist` — 永不获得词条的物品 id

## 需求

- Minecraft **1.20.1**
- NeoForge **47.1.106+**
- Java **17**
- JEI **15.x**（可选）

## 说明

- 本模组定位为**额外的装备层**，不是附魔替代品。
- 数值风格更接近轻量 ARPG 词条，而不是彻底重做战利品。
- 模组不使用 Mixin。
- 版本 **1.0.0**，作者 **yunzhihai**，MIT 许可。
