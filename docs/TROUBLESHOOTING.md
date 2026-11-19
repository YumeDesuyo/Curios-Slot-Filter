# 故障排查

## 模组列表中没有 Curios Slot Filter

1. 确认 JAR 位于具体版本目录的 `mods` 文件夹。
2. 确认游戏版本是 Minecraft 1.20.1、Forge 47.x。
3. 检查 `logs/latest.log` 中是否存在依赖错误。
4. 确认 Curios 至少为 5.9.0，JEI 至少为 15.59.0。

当前发行 JAR 的显示名称为 `Curios Slot Filter`，中文名称为“饰品筛选”，
技术 Mod ID 仍保持为 `curios_slot_finder`。

## 游戏启动时报 `javafml:47 or above`

该 JAR 被安装到了旧版本整合包。本模组不兼容 1.19.2 的 `javafml:43`。

## 点击空槽没有反应

- 确认点击的是 Curios 饰品界面中的槽位。
- 确认鼠标上没有拿着物品。
- 不要按 `Shift` 或 `Ctrl`。
- 检查日志是否出现 `Detected empty Curios slot`。
- 如果尚未出现该日志，说明点击事件没有识别到 `CurioSlot`。

## JEI 搜索没有变化

- 确认 JEI 已成功加载。
- 检查日志中的 `Curios Slot Filter connected to JEI`。
- 如果 JEI 注册失败，模组不会覆盖当前搜索内容。

## `#ring` 显示石头等非饰品

这是 JEI 15.x 的标签路径搜索限制。`#ring` 不区分 `curios:ring` 与其他命名空间中的
同名路径。当前版本接受少量误匹配，不会再叠加额外全局过滤。

## 通用槽没有显示全部饰品

通用槽依赖 `curio` JEI 搜索别名。请查看日志：

```text
Registered the 'curio' JEI alias for N Curios items
```

如果 `N` 为 0，说明 Curios 数据尚未就绪，或 JEI 禁用了搜索别名。

## 收集诊断信息

至少提供：

- `logs/latest.log`
- `crash-reports/` 中最新的崩溃报告（如有）
- 游戏版本、Forge 版本
- Curios 和 JEI 的完整文件名
