# 转盘选择（Wheel Picker）

一个功能完整的安卓转盘选择 App。可自由编辑转盘上的选项文字、颜色与权重，点击旋转后按权重概率抽取结果；内置**隐藏后台控制**，活动主办方可通过密码进入后台精确指定下一轮转盘停在哪一个选项，旋转后自动恢复随机模式。适用于活动抽奖、课堂点名、团队决策、聚餐选择等场景。

## 功能特性

### 转盘展示
- 圆形转盘由 Compose Canvas 自绘，扇区数量与选项数实时对应
- 每个扇区使用对应选项的颜色填充，并显示选项文字
- 扇区文字沿扇区中分角居中排布，字号随扇区大小与文字长度自动缩放
- 中心固定指针（尖端朝上）与中心轴点，旋转结束后指针精确停在结果扇区正中，永不落在扇区缝隙

### 自由编辑
- **增删选项**：添加、删除任意选项，实时预览
- **文字编辑**：修改选项文字，保存后立即同步到转盘
- **颜色编辑**：12 色预设色板自由选择，新增选项自动分配未使用的颜色
- **权重编辑**：设置 1-100 权重，决定选项被抽中的概率占比
- **恢复默认**：一键恢复内置的 6 个示例选项
- 选项数量限制：最少 2 个、最多 20 个

### 权重随机
- 扇区角度按 `权重 / 权重总和 × 360°` 计算，扇区越大被抽中概率越高
- 随机选择基于累积权重区间的均匀抽样，统计上精确等于权重占比
- 旋转动画至少 4-8 圈，配缓动曲线模拟真实转盘减速停止

### 隐藏后台控制
- **隐蔽入口**：转盘页顶部标题区域连续快速点击 5 次
- **密码保护**：默认密码 `8888`，可在后台页底部随时修改
- **多轮指定**：在后台依次点选选项，可排定「第 1 次、第 2 次……」结果队列，一次设置连续多轮
- **按序生效**：每次旋转依次消费队列头部指定，队列耗尽自动恢复随机
- **无痕作弊**：指定期间主转盘页无任何提示，与随机模式视觉完全一致

### 历史记录
- 每次抽取自动记录结果选项与精确时间戳
- 历史按时间倒序排列，支持一键清空
- 方便事后核对抽奖过程与公平性

### 数据持久化
- 配置（选项、颜色、权重、后台密码、指定状态）与历史记录均通过 DataStore 本地保存
- 应用重启后完整恢复，无需重新设置

## 技术栈

| 类别 | 选型 |
|------|------|
| 语言 | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 + Navigation Compose |
| 架构 | MVVM + 单向数据流（StateFlow） |
| 绘制 | Compose Canvas 自定义绘制 + Animatable 旋转动画 |
| 持久化 | Preferences DataStore + kotlinx.serialization JSON |
| 构建 | Gradle 8.7 + AGP 8.5.2 |
| 测试 | JUnit 4 单元测试 |

## 下载

最新可安装包（`wheel-picker-v1.0.0.apk`）发布在 GitHub Releases：

https://github.com/fvfpq/wheel-picker/releases/tag/v1.0.0

> 安装时需在系统设置中允许「安装未知来源应用」。

## 构建运行

### 环境要求
- JDK 17
- Android Studio（Ladybug 或更新版本）
- 安卓 8.0（API 26）及以上设备

### 构建步骤

```bash
# 1. 下载代码
git clone https://github.com/fvfpq/wheel-picker.git

# 2. 用 Android Studio 打开项目根目录
#    等待首次 Gradle Sync 完成（会下载依赖，需联网）

# 3. 生成 APK
#    Build → Build App Bundle(s) / APK(s) → Build APK(s)

# 4. 或连接手机（开启 USB 调试）直接点击工具栏 Run ▶ 安装运行
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`，将该文件传到手机点击即可安装（需在系统设置中允许「安装未知来源应用」）。

### 运行单元测试

```bash
# 在 Android Studio 中打开 app/src/test 下的测试类，点击运行
# 或命令行执行
./gradlew testDebugUnitTest
```

## 使用指南

### 日常使用
1. 打开 App 进入转盘页，底部点击「开始旋转」
2. 旋转结束后弹窗展示抽取结果，并自动记入历史
3. 点击左上角编辑按钮调整选项、颜色、权重
4. 点击右上角历史按钮查看历史记录

### 后台指定结果（主持人专用）
1. 在转盘页顶部标题区域**连续快速点击 5 次**
2. 在弹出框中输入后台密码（默认 `8888`）
3. 进入后台页，依次点选选项，排定第 1 次、第 2 次……的指定结果队列
4. 可在队列预览区单独移除某项，或点「清空全部指定」
5. 返回转盘页点击旋转，每次旋转依次命中队列中的指定
6. 队列耗尽后自动恢复随机模式；可在后台页底部修改后台密码

> 提示：请将密码告知可信主持人，避免普通参与者误入后台。

## 工程结构

```
wheel-picker/
├── settings.gradle.kts          # 工程配置
├── build.gradle.kts             # 根构建脚本
├── gradle.properties            # Gradle 参数
├── gradle/wrapper/              # Gradle Wrapper（固定构建版本）
└── app/
    ├── build.gradle.kts         # App 模块构建脚本
    ├── proguard-rules.pro       # 混淆规则
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/example/wheelpicker/
        │   │   ├── MainActivity.kt          # 应用入口
        │   │   ├── ServiceLocator.kt        # 依赖单例
        │   │   ├── domain/                  # 领域层（纯逻辑，可单元测试）
        │   │   │   ├── Selectors.kt         #   按权重随机 / 强制指定选择器
        │   │   │   ├── SpinEngine.kt        #   扇区角度计算与目标角度引擎
        │   │   │   └── BackdoorController.kt#   连点检测与密码校验
        │   │   ├── data/                    # 数据层
        │   │   │   ├── OptionRepository.kt  #   DataStore 读写封装
        │   │   │   └── model/WheelModels.kt #   数据模型与默认配置
        │   │   └── ui/                      # UI 层
        │   │       ├── AppNavigation.kt     #   导航图
        │   │       ├── wheel/               #   转盘页（绘制+旋转+入口）
        │   │       ├── edit/                #   编辑页（选项/颜色/权重）
        │   │       ├── backdoor/            #   后台控制页
        │   │       ├── history/             #   历史记录页
        │   │       ├── components/          #   通用对话框组件
        │   │       └── theme/               #   主题
        │   └── res/                         # 资源（主题、图标）
        └── test/                            # 单元测试
            ├── domain/                      # 选择器/引擎/后台控制测试
            └── data/                        # 模型与序列化测试
```

## 架构说明

采用 MVVM + 单向数据流分层：

```mermaid
flowchart TD
    UI["Compose UI 层"] --> VM["ViewModel (StateFlow)"]
    VM --> SE["领域层 SpinEngine / Selectors / BackdoorController"]
    VM --> R["数据层 OptionRepository"]
    R --> DS["DataStore 持久化"]
```

- **UI 层**：纯声明式组合，只展示状态与分发事件
- **ViewModel 层**：维护转盘状态机（静止/旋转中）、配置流与后台控制
- **领域层**：转盘角度计算、权重随机、连点密码校验，不依赖 Android，可直接单元测试
- **数据层**：DataStore 持久化，配置以 JSON 整体序列化

## 测试覆盖

单元测试覆盖以下核心逻辑：

- `RandomSelectorTest`：权重分布统计验证（10 万次抽样，容差 2%）
- `SpinEngineTest`：扇区角度和恒为 360°、角度到扇区唯一映射、指定结果必然收敛到目标扇区
- `BackdoorControllerTest`：连点计数窗口、密码校验、指定状态消费
- `WheelModelsTest`：权重钳制、标签裁剪、JSON 序列化往返、默认配置

## 常见问题

**Q：安装 APK 提示「应用未安装」**
A：可能已安装同名应用，先卸载旧版本；或设备系统版本低于安卓 8.0。

**Q：忘记后台密码怎么办**
A：删除应用数据会重置为默认密码 `8888`，同时会清空历史记录。

**Q：最多能添加多少选项**
A：20 个。选项过多时扇区会过窄，影响可读性。

**Q：权重具体如何影响概率**
A：选中概率 = 该选项权重 ÷ 全部选项权重之和。全部权重相等时，各选项概率完全一致。

## 项目状态

当前版本 1.0.0，已完成全部规划功能。后续可扩展方向：选项图标、转盘音效、局域网远程控制端、导出历史记录。

## 贡献者

- [fvfpq](https://github.com/fvfpq/wheel-picker)

感谢所有为本项目提交过代码、Issue 或建议的贡献者！

## 许可证

本项目采用 [MIT License](LICENSE)。
