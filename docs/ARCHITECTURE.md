# 技术架构

## 目标

Curios Slot Filter（技术 Mod ID：`curios_slot_finder`）将“物品是否适用于某个 Curios 槽位”转换为 JEI 可以展示的搜索条件
或匹配结果，同时避免重复扫描整个物品索引。

## 数据流

```text
空槽点击
  -> CurioSlot 识别
  -> ISlotType 与 Validator 读取
  -> 规则分类
  -> JEI 标签筛选 / Validator 匹配 / 全部允许
  -> 状态面板与搜索恢复
```

## 规则分类

`SlotReader` 将槽位分为三类：

- `TAG`：仅使用 `curios:tag` 的标准槽位。
- `VALIDATORS`：包含自定义 Curios Predicate 的槽位。
- `ALL`：包含 `curios:all` 的槽位。

槽位 ID、索引和玩家上下文会封装为 `SlotDescriptor`。

## JEI 集成

### 标准标签槽位

JEI 15.x 的 `#` 搜索只索引标签路径。例如：

```text
curios:ring -> #ring
```

通用 `curio` 槽无法可靠使用 `#curio`，因此模组在 JEI 别名注册阶段给具备 Curios
标签或 Capability 的物品添加 `curio` 别名。

### 自定义 Validator

`JeiFilterController` 从 JEI 已建立的物品索引读取 `ItemStack`，使用
`CuriosApi.testCurioPredicates` 对真实 `SlotResult` 求值，并同时考虑通用
`curios:curio` 槽位规则。

匹配结果不会通过修改 JEI 全局可见性实现，而是由客户端结果面板展示。

## 缓存

`CompatibilityCache` 的缓存键包含：

- 槽位 ID
- 槽位索引
- 是否为 Cosmetic 槽
- Validator 列表
- 玩家状态指纹

缓存默认有效 20 client ticks。玩家生命、饥饿、经验、效果和物品栏发生变化时，
新的指纹会使旧缓存失效。

## 事件

- `ScreenEvent.MouseButtonPressed.Pre`：拦截普通左键空槽。
- `ScreenEvent.KeyPressed.Pre`：处理 `Esc` 清理。
- `ScreenEvent.Closing`：关闭界面时恢复搜索。
- `ScreenEvent.Render.Post`：绘制状态和 Validator 结果。
- `TickEvent.ClientTickEvent`：更新缓存时间和快捷键。

## 客户端边界

`mods.toml` 使用 `clientSideOnly=true`。模组不注册物品、方块、网络包或服务端事件。
