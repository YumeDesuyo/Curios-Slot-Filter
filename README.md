<p align="center">
  <img src="icon.png" alt="Curios Slot Filter" width="192">
</p>

<h1 align="center">Curios Slot Filter · 饰品筛选</h1>

<p align="center">
  点击空的 Curios 饰品槽，快速查看能装备到该槽位的物品。
</p>

<p align="center">
  Minecraft 1.20.1 · Forge 47.x · 客户端辅助模组
</p>

## 简介

Curios Slot Filter（饰品筛选）是一个独立、客户端专用的 Minecraft 模组。它与 Curios 和 JEI 联动，
通过点击空的 Curios 饰品槽执行反向查询，减少在 JEI 中手动搜索饰品的步骤。

模组不会修改服务端逻辑，也不需要安装在服务器上。

技术 Mod ID 保持为 `curios_slot_finder`，以兼容已有配置、日志和依赖关系。

## 功能

- 普通左键点击空的 Curios 槽位即可查询。
- 标准 `curios:tag` 槽位直接更新 JEI 搜索筛选。
- 自定义 Curios Validator 通过实际 Predicate API 计算匹配物品。
- 通用 `curio` 槽使用专用搜索别名，显示通用饰品和具体槽位饰品。
- 再次点击当前槽位或按 `Esc` 恢复之前的 JEI 搜索。
- `F6` 开关自动槽位筛选。
- Validator 结果带有玩家状态感知缓存。
- 资源重载时自动清理兼容性缓存。
- 提供中英文界面文本。

## 环境要求

| 项目 | 要求 |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.x |
| Java | 17 |
| Curios | 5.9.0 或更新 |
| JEI | 15.59.0 或更新 |
| 运行侧 | 仅客户端 |

本模组明确不兼容 Minecraft 1.19.2 或其他 Forge 大版本。Forge 会在启动阶段拒绝加载错误的版本。

## 安装

1. 安装 Minecraft 1.20.1 与 Forge 47.x。
2. 将 Curios 和 JEI 放入游戏的 `mods` 目录。
3. 将 `curios_slot_finder-<版本>.jar` 放入同一个 `mods` 目录。
4. 启动客户端。

服务器端无需安装本模组。多人游戏连接时，Forge 会忽略该客户端专用模组的版本差异。

## 使用方法

1. 打开 Curios 饰品界面。
2. 确保鼠标上没有拿着物品。
3. 普通左键点击一个空槽位，不要按 `Shift` 或 `Ctrl`。
4. JEI 搜索框会更新为对应槽位的筛选条件。
5. 再次点击同一个槽位即可清除筛选。

### 通用 Curios 槽

通用 `curio` 槽使用 `curio` 搜索别名。JEI 15.x 的 `#` 标签搜索不保留命名空间，
因此直接使用 `#curio` 会漏掉只带 `ring`、`necklace` 等具体标签的饰品。

### 同名标签

JEI 的 `#ring` 只匹配标签路径，无法区分 `curios:ring` 与其他命名空间中的同名标签。
因此搜索结果可能包含少量非饰品物品。这是 JEI 15.x 公共搜索接口的限制。

## 配置

客户端配置位于：

```text
config/curios_slot_finder-client.toml
```

可配置项目包括：

- 是否在点击空槽时自动筛选
- 关闭界面时是否恢复原搜索
- 是否显示兼容性结果面板
- Validator 缓存时间
- 结果面板列数

## 快捷键

| 按键 | 功能 |
|---|---|
| `F6` | 开关自动槽位筛选 |
| `Esc` | 清除当前筛选并关闭界面 |

按键可以在 Minecraft 的“选项 → 控制 → 按键绑定”中修改。

## 兼容性说明

- 模组只注册客户端事件和 JEI 插件。
- 不修改 Curios 源码。
- 不修改 JEI 全局物品可见性。
- 自定义 Validator 使用 Curios 实际 Predicate API。
- 通用槽别名只影响 JEI 搜索别名，不修改物品注册名。
- 其他模组注册同名别名时，JEI 会合并结果，但不会导致崩溃。

## 日志与排错

正常启动时，`logs/latest.log` 应包含：

```text
Curios Slot Finder initialized
Curios Slot Finder connected to JEI
Registered the 'curio' JEI alias for N Curios items
```

点击空槽后应包含：

```text
Detected empty Curios slot ring#0 with rule TAG
Slot filter active: identifier=ring, rule=TAG
```

更多说明见 [故障排查](docs/TROUBLESHOOTING.md)。

项目附带启动日志监测脚本：

```powershell
.\tools\monitor-startup.ps1 -VersionName "你的 PCL 版本名"
```

脚本会标记长时间无新日志、`WARN` 和 `ERROR`。

## 从源码构建

环境：

- JDK 17
- Windows、Linux 或 macOS

```powershell
.\gradlew.bat build
```

构建产物位于：

```text
build/libs/curios_slot_finder-<version>.jar
```

Linux/macOS 使用：

```bash
./gradlew build
```

## 项目结构

```text
src/main/java/com/yume/curiosslotfinder/
├── cache/       玩家状态感知缓存
├── client/      筛选会话与结果面板
├── config/      Forge 客户端配置
├── curios/      Curios 槽位读取与规则分类
├── event/       客户端输入和界面事件
└── jei/         JEI 插件、别名和筛选桥接
```

架构说明见 [技术文档](docs/ARCHITECTURE.md)。

## 参与开发

提交代码前请阅读 [贡献指南](CONTRIBUTING.md)。

## 许可证

当前项目为公开源码，但保留所有权利。详见 [LICENSE](LICENSE)。
