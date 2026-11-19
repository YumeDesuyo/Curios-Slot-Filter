# 贡献指南

## 提交问题

请附上：

- Minecraft、Forge、Curios、JEI 的准确版本
- 问题复现步骤
- `logs/latest.log`
- 对应槽位 ID 和操作方式

## 开发环境

- JDK 17
- Minecraft 1.20.1
- ForgeGradle 6.0.54

## 构建

```powershell
.\gradlew.bat build
```

## 代码约定

- 保持模组客户端专用。
- 不修改 Curios 或 JEI 的全局数据。
- 新功能应优先使用公开 API。
- 不在日志中输出玩家敏感信息。
- 涉及缓存时需要考虑玩家状态和资源重载。

## 提交前检查

- 项目可以完成 `build`。
- `latest.log` 中没有本模组产生的 `ERROR` 或 `FATAL`。
- 普通左键、当前槽再次点击、切换槽位和 `Esc` 清理均正常。
